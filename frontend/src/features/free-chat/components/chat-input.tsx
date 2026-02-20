import { ArrowRight } from "lucide-react";
import { useEffect, useTransition } from "react";
import {
	InputGroup,
	InputGroupAddon,
	InputGroupButton,
	InputGroupTextarea,
} from "@/components/ui/input-group";
import { useChatContext, useChatInput } from "@/features/chat/lib/chat";
import type { ChatState } from "../lib/chat/state";
import { ModelSelect } from "./model-select";

export function ChatInput({
	initial,
	state = useChatContext(),
	className,
	disabled,
	onFocus,
	autoFocus,
}: {
	initial?: { model?: string; prompt?: string };
	onFocus?: (e: React.FocusEvent<HTMLTextAreaElement>) => void;
	autoFocus?: boolean;
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
				className="max-h-24 focus:max-h-[40svh]"
				onChange={(e) => setPrompt(e.target.value)}
				onFocus={onFocus}
				autoFocus={autoFocus}
				onKeyDown={(e) => {
					if (e.key === "Enter" && (e.shiftKey || e.ctrlKey) === true) {
						submit();
					}
				}}
				value={prompt}
			/>
			<InputGroupAddon align="block-end">
				<ModelSelect chatState={state} />
				<InputGroupButton
					className="ml-auto"
					onClick={() => disabled || submit()}
				>
					{disabled ? "type something first" : <ArrowRight />}
				</InputGroupButton>
			</InputGroupAddon>
		</InputGroup>
	);
}
