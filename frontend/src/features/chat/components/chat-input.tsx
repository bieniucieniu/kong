import { ArrowRight, Loader } from "lucide-react";
import { useState, useTransition } from "react";
import {
	InputGroup,
	InputGroupAddon,
	InputGroupButton,
	InputGroupTextarea,
} from "@/components/ui/input-group";
import { Menubar } from "@/components/ui/menubar";
import { ModelMenubarMenu } from "@/features/chat/components/model-select";
import { type ChatState, useChat } from "@/features/chat/lib/chat";

export function ChatInput({
	state,
	className,
	disabled,
}: {
	state: ChatState;
	className?: string;
	disabled?: boolean;
}) {
	const [prompt, setPrompt] = useState(
		"count to 100 as md list, like:\n- 1\n- 2\n- 3",
	);
	const { status, pushPrompt } = useChat(state);
	const [isPending, startTransition] = useTransition();

	const submit = (p: string = prompt) =>
		startTransition(() => {
			p = p.trim();
			pushPrompt(p);
			setPrompt("");
		});
	disabled ||= isPending || status === "pending";
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
					{status === "pending" ? (
						<Loader className="animate-spin" />
					) : (
						<ArrowRight />
					)}
				</InputGroupButton>
			</InputGroupAddon>
		</InputGroup>
	);
}
