import type { RefObject } from "react";
import type { Chat, ChatMessagesItem } from "@/gen/models";
import { createSignal, type SignalState } from "@/lib/hooks/state/signal";
import { getStored, updateStored } from "@/lib/hooks/state/storage";

export type ChatId = "new" | (string & {});
export type CreateChatOptions = {
	id?: ChatId;
	executeOnPrompt?: boolean;
	onMessagePushed?: (item: ChatMessagesItem) => void;
	onExecutePrompt?: (item: Chat, onMessage: (item: string) => void) => void;
	initial?: { prompt?: string };
};
export interface ChatState {
	messages: SignalState<Chat["messages"]>;
	model: SignalState<string | undefined>;
	provider: SignalState<string | undefined>;
	prompt: SignalState<string>;
	pushMessage: (args: ChatMessagesItem) => ChatMessagesItem[];
	pushPrompt: (p: string) => ChatMessagesItem[];
	executePrompt: () => void;
}
export function createChat(opt: RefObject<CreateChatOptions>): ChatState {
	const messages = createSignal<Chat["messages"]>([]);
	const model = createSignal(getStored<string>("chat-model"), (s) =>
		updateStored("chat-model", s),
	);
	const provider = createSignal(getStored<string>("chat-provider"), (s) =>
		updateStored("chat-provider", s),
	);
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
	const executePrompt = () =>
		model.state &&
		provider.state &&
		opt.current.onExecutePrompt?.(
			{
				messages: messages.state,
				model: model.state,
				provider: provider.state,
			},
			(p) => pushMessage({ prompt: p, author: "agent" }),
		);
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
		provider,
		messages,
		model,
		prompt,
		pushMessage,
		pushPrompt,
		executePrompt,
	};
}
