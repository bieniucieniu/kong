import { ArrowRightIcon } from "@phosphor-icons/react";
import { useTransition } from "react";
import {
	InputGroup,
	InputGroupAddon,
	InputGroupButton,
	InputGroupTextarea,
} from "@/components/ui/input-group";
import type { Maybe, MaybePromise } from "@/lib/helper-types";
import { useChatMutation } from "..";
import {
	type ChatPromptController,
	useChatPromptController,
	useChatPromptState,
} from "../state/prompt";
import { ModelSelect, type ModelSelectProps } from "./model-select";

export interface ChatInputProps extends ModelSelectProps {
	onSubmit?: (p: string) => MaybePromise<Maybe<unknown>>;
	id: string;
	onFocus?: (e: React.FocusEvent<HTMLTextAreaElement>) => void;
	controller?: ChatPromptController;
	autoFocus?: boolean;
	className?: string;
	disabled?: boolean;
}

export function ChatInput({
	id,
	className,
	onSubmit,
	onFocus,
	autoFocus,
	disabled,
	controller: state = useChatPromptController(),
}: ChatInputProps) {
	const s = useChatPromptState(state);

	const m = useChatMutation({ id });

	const [isPending, startTransition] = useTransition();

	const submit = (p = s.prompt) =>
		startTransition(() => {
			p = p?.trim();
			m.mutate({
				message: p,
				provider: s.provider,
				model: s.model,
			});
			const clean = (p && onSubmit?.(p)) ?? true;
			if (clean) s.prompt = "";
		});

	disabled ||= isPending || !s.prompt?.trim();
	return (
		<InputGroup className={className}>
			<InputGroupTextarea
				id={`prompt-${id}`}
				className="max-h-24 focus:max-h-[40svh]"
				onChange={(e) => (s.prompt = e.target.value)}
				onFocus={onFocus}
				autoFocus={autoFocus}
				onKeyDown={(e) => {
					if (e.key === "Enter" && (e.shiftKey || e.ctrlKey) === true) {
						submit();
					}
				}}
				value={s.prompt}
			/>
			<InputGroupAddon align="block-start">
				<ModelSelect controller={state} className="ml-auto" />
			</InputGroupAddon>
			<InputGroupAddon align="block-end">
				<InputGroupButton
					className="ml-auto"
					onClick={() => disabled || submit()}
				>
					{disabled ? "type something first" : <ArrowRightIcon />}
				</InputGroupButton>
			</InputGroupAddon>
		</InputGroup>
	);
}

export function ChatStateInput({
	state = useChatPromptController(),
	className,
	disabled,
	onSubmit,
	onFocus,
	autoFocus,
}: {
	onSubmit?: (p: string) => void;
	onFocus?: (e: React.FocusEvent<HTMLTextAreaElement>) => void;
	autoFocus?: boolean;
	state?: ChatPromptController;
	className?: string;
	disabled?: boolean;
}) {
	const s = useChatPromptState(state);
	const [isPending, startTransition] = useTransition();

	const submit = (p: string = s.prompt) =>
		startTransition(() => {
			p = p.trim();
			onSubmit?.(p);
			s.prompt = "";
		});
	disabled ||= isPending || !s.prompt.trim();
	return (
		<InputGroup className={className}>
			<InputGroupTextarea
				className="max-h-24 focus:max-h-[40svh]"
				onChange={(e) => (s.prompt = e.target.value)}
				onFocus={onFocus}
				autoFocus={autoFocus}
				onKeyDown={(e) => {
					if (e.key === "Enter" && (e.shiftKey || e.ctrlKey) === true) {
						submit();
					}
				}}
				value={s.prompt}
			/>
			<InputGroupAddon align="block-end">
				<ModelSelect controller={state} />
				<InputGroupButton
					className="ml-auto"
					onClick={() => disabled || submit()}
				>
					{disabled ? "type something first" : <ArrowRightIcon />}
				</InputGroupButton>
			</InputGroupAddon>
		</InputGroup>
	);
}
