import { useMutation } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import { useMemo, useState } from "react";
import { Card } from "@/components/ui/card";
import {
	InputGroup,
	InputGroupButton,
	InputGroupInput,
	InputGroupText,
} from "@/components/ui/input-group";
import { MarkdownCard } from "@/components/ui/markdown";
import { ScrollArea } from "@/components/ui/scroll-area";
import { getPostAiChatUrl } from "@/gen/api/default/default";
import type { Chat, ChatMessagesItem } from "@/gen/models";
import { useUpdateCallback } from "@/lib/hooks/update";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/")({
	component: RouteComponent,
});

const authorMap: Record<string, "user" | "agent" | "tool" | undefined> = {
	a: "agent",
	u: "user",
	t: "tool",
};

function RouteComponent() {
	const [chat, setResponse] = useState<Chat["messages"]>([]);
	const [prompt, setPrompt] = useState("");

	const m = useMutation({
		mutationFn: async (p: Chat) => {
			const res = await fetch(getPostAiChatUrl(), {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify(p),
			});
			if (res.status !== 200) throw new Error(res.statusText);

			return res.body?.getReader();
		},
	});

	const pushMessage = useUpdateCallback(
		(propt: ChatMessagesItem["prompt"], author: ChatMessagesItem["author"]) => {
			setResponse((p) => {
				const prev = p.length > 0 ? p[p.length - 1] : undefined;
				if (prev?.author === author) {
					p[p.length - 1] = {
						...prev,
						prompt: prev.prompt + propt,
					};
					return [...p];
				}
				return [...p, { prompt: propt, author }];
			});
		},
	);

	useCollect(m.data, (d) => {
		const author = authorMap[d.slice(0, 1)];
		if (author) pushMessage(d.slice(2), author);
	});

	const submit = useUpdateCallback((p: string = prompt) => {
		pushMessage(p, "user");
		setPrompt("");
		m.mutate({ messages: chat });
	});

	return (
		<main>
			<div className="h-svh mx-auto  flex flex-col items-center justify-end gap-4 p-4">
				<div className="flex-1">
					<ScrollArea className="max-w-4xl max-h-0 min-h-full overflow-auto">
						<div className="flex flex-col gap-4 items-start">
							{chat.map(({ prompt: message, author }, i) => {
								const key = `${author}-${i}-${message.slice(0, 10)}`;
								return (
									<MarkdownCard
										key={key}
										className={cn({
											"self-end bg-accent": author === "user",
										})}
									>
										{message}
									</MarkdownCard>
								);
							})}
						</div>
					</ScrollArea>
				</div>
				<InputGroup className="max-w-100">
					<InputGroupInput
						onChange={(e) => setPrompt(e.target.value)}
						onKeyDown={(e) => {
							if (e.key === "Enter" && (e.shiftKey || e.ctrlKey) === true) {
								submit();
							}
						}}
						value={prompt}
					/>
					<InputGroupButton onClick={() => submit()}>
						{m.status}
					</InputGroupButton>
					<InputGroupText>{m.error?.message}</InputGroupText>
				</InputGroup>
			</div>
		</main>
	);
}

function useCollect(
	reader: ReadableStreamDefaultReader<Uint8Array<ArrayBuffer>> | undefined,
	onData: (data: string) => void,
) {
	onData = useUpdateCallback(onData);

	useMemo(async () => {
		if (!reader) return;
		const decoder = new TextDecoder();
		while (true) {
			const { done, value } = await reader.read();
			if (done) break;

			const chunk = decoder.decode(value, { stream: true });

			onData(chunk);
		}
	}, [reader]);
}
