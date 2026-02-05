import { ArrowRight } from "lucide-react";
import { useEffect, useTransition } from "react";
import {
	InputGroup,
	InputGroupAddon,
	InputGroupButton,
	InputGroupTextarea,
} from "@/components/ui/input-group";
import { Menubar } from "@/components/ui/menubar";
import { ModelMenubarMenu } from "@/features/chat/components/model-select";
import { useChatContext, useChatInput } from "@/features/chat/lib/chat";
import type { ChatState } from "../lib/chat/state";

export function ChatInput({
	initial,
	state = useChatContext(),
	className,
	disabled,
}: {
	initial?: { model?: string; prompt?: string };
	state?: ChatState;
	className?: string;
	disabled?: boolean;
}) {
	const [prompt, setPrompt, pushPrompt] = useChatInput(state);
	const [isPending, startTransition] = useTransition();

	useEffect(() => {
		if (initial) {
			initial.model && state.model.update(initial.model);
			initial.prompt && setPrompt(initial.prompt);
		}
	}, []);

	const submit = (p: string = prompt) =>
		startTransition(() => {
			p = p.trim();
			pushPrompt(p);
			setPrompt("");
		});
	disabled ||= isPending || !prompt.trim();
	return (
		<InputGroup className={className}>
			<InputGroupTextarea
				onChange={(e) => setPrompt(e.target.value)}
				onKeyDown={(e) => {
					if (e.key === "Enter" && (e.shiftKey || e.ctrlKey) === true) {
						submit();
					}
				}}
				value={prompt}
			/>
			<InputGroupAddon align="block-end">
				<Menubar>
					<ModelMenubarMenu chatState={state} />
				</Menubar>
				<InputGroupButton
					className="ml-auto"
					disabled={disabled}
					onClick={() => submit()}
				>
					{disabled ? "" : <ArrowRight />}
				</InputGroupButton>
			</InputGroupAddon>
		</InputGroup>
	);
}
