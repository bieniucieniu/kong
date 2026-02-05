import type { RefObject } from "react";
import type { Chat, ChatMessagesItem } from "@/gen/models";
import { createSignal, type SignalState } from "@/lib/hooks/state/signal";

export type CreateChatOptions = {
	executeOnPrompt?: boolean;
	onMessagePushed?: (item: ChatMessagesItem) => void;
	onExecutePrompt?: (item: Chat, onMessage: (item: string) => void) => void;
	initial?: { model?: string; prompt?: string };
};
export interface ChatState {
	messages: SignalState<Chat["messages"]>;
	model: SignalState<string | undefined>;
	prompt: SignalState<string>;
	pushMessage: (args: ChatMessagesItem) => ChatMessagesItem[];
	pushPrompt: (p: string) => ChatMessagesItem[];
	executePrompt: () => void;
}
export function createChat(opt: RefObject<CreateChatOptions>) {
	const messages = createSignal<Chat["messages"]>([]);
	const model = createSignal<string | undefined>(opt.current.initial?.model);
	const prompt = createSignal<string>(opt.current.initial?.prompt || "");
	const pushMessage = ({ prompt, author }: ChatMessagesItem) => {
		const p = messages.state;
		const prev = p.length > 0 ? p[p.length - 1] : undefined;
		if (prev?.author === author) {
			p[p.length - 1] = {
				...prev,
				prompt: prev.prompt + prompt,
			};
		} else {
			const item = { prompt, author };
			p.push(item);
			opt.current.onMessagePushed?.(item);
		}
		return messages.update([...p]);
	};
	const executePrompt = () => {
		opt.current.onExecutePrompt?.(
			{ messages: messages.state, model: model.state },
			(p) => pushMessage({ prompt: p, author: "agent" }),
		);
	};
	const pushPrompt = (p: string = prompt.state) => {
		p = p.trim();
		if (!p.length) return messages.state;
		if (
			messages.state !== pushMessage({ prompt: p, author: "user" }) &&
			opt.current.executeOnPrompt !== false
		) {
			executePrompt();
		}
		prompt.update("");
		return messages.state;
	};

	return {
		options: opt.current,
		messages,
		model,
		prompt,
		pushMessage,
		pushPrompt,
		executePrompt,
	};
}
