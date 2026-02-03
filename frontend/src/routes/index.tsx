import { createFileRoute } from "@tanstack/react-router";
import { ChevronDown } from "lucide-react";
import { useState, useTransition } from "react";
import {
	InputGroup,
	InputGroupAddon,
	InputGroupButton,
	InputGroupText,
	InputGroupTextarea,
} from "@/components/ui/input-group";
import { MarkdownCard } from "@/components/ui/markdown";
import { ScrollArea } from "@/components/ui/scroll-area";
import {
	Select,
	SelectContent,
	SelectItem,
	SelectTrigger,
	SelectValue,
} from "@/components/ui/select";
import {
	type ChatState,
	useChat,
	useChatModels,
	useCreateChat,
} from "@/features/chat/hooks/chat";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/")({
	component: RouteComponent,
});

function RouteComponent() {
	const chatState = useCreateChat();
	const [prompt, setPrompt] = useState(
		"how long message can you create before crushing",
	);

	const { status, pushPrompt, chat, error } = useChat(chatState);

	let [isPending, startTransition] = useTransition();
	isPending ||= status === "pending";

	const submit = (p: string = prompt) =>
		startTransition(() => {
			p = p.trim();
			pushPrompt(p);
			setPrompt("");
		});

	return (
		<div className="h-full flex flex-col items-center justify-end gap-4 p-4">
			<div className="flex-1">
				<ScrollArea className="w-full max-w-4xl max-h-0 min-h-full overflow-auto">
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
				<InputGroupTextarea
					onChange={(e) => setPrompt(e.target.value)}
					onKeyDown={(e) => {
						if (e.key === "Enter" && (e.shiftKey || e.ctrlKey) === true) {
							submit();
						}
					}}
					value={prompt}
				/>
				<InputGroupText>{error?.message}</InputGroupText>
				<InputGroupAddon align="block-end">
					<ModelSelect chatState={chatState} />
					<InputGroupButton
						className="ml-auto"
						disabled={isPending}
						onClick={() => submit()}
					>
						{status}
					</InputGroupButton>
				</InputGroupAddon>
			</InputGroup>
		</div>
	);
}

function ModelSelect({
	chatState,
	className,
}: {
	chatState: ChatState;
	className?: string;
}) {
	const { model, models, status, error, selectModel } =
		useChatModels(chatState);
	const disabled = status !== "success";
	return (
		<Select value={model} onValueChange={selectModel}>
			<SelectTrigger
				disabled={disabled}
				size="none"
				className="border-none"
				render={
					<InputGroupButton className={className} variant="ghost">
						<SelectValue placeholder="default" />
						<ChevronDown />
					</InputGroupButton>
				}
			/>
			<SelectContent>
				{models.map((v) => (
					<SelectItem key={v} value={v}>
						{v}
					</SelectItem>
				))}
			</SelectContent>
		</Select>
	);
}
