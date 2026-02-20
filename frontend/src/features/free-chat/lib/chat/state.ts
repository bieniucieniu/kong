import type { RefObject } from "react";
import type {
	ChatPrompt,
	ChatPromptsList,
	ChatPromptsListMessagesItem,
} from "@/gen/models";
import { createSignal, type SignalState } from "@/lib/hooks/state/signal";
import { createStoredSignal } from "@/lib/hooks/state/stored-signal";

export type ChatId = "new" | (string & {});
export type CreateChatOptions = {
	executeOnPrompt?: boolean;
	onMessagePushed?: (item: ChatPromptsListMessagesItem) => void;
	onExecutePrompt?: (
		item: ChatPromptsList & ChatPrompt,
		onMessage: (item: string) => void,
	) => void;
	initial?: { prompt?: string };
};
export interface ChatState {
	messages: SignalState<ChatPromptsList["messages"]>;
	model: SignalState<string | undefined>;
	provider: SignalState<string | undefined>;
	prompt: SignalState<string>;
	pushMessage: (
		args: ChatPromptsListMessagesItem,
	) => ChatPromptsListMessagesItem[];
	pushPrompt: (p: string) => ChatPromptsListMessagesItem[];
	executePrompt: (str?: string) => void;
}
export function createChat(opt: RefObject<CreateChatOptions>): ChatState {
	const messages = createSignal<ChatPromptsList["messages"]>([]);
	const model = createStoredSignal<string>("chat-model");
	const provider = createStoredSignal<string>("chat-provider");
	const prompt = createSignal<string>(opt.current.initial?.prompt || "");
	const pushMessage = ({ content, role }: ChatPromptsListMessagesItem) => {
		const p = messages.state;
		const prev = p.length > 0 ? p[p.length - 1] : undefined;
		if (prev?.role === role) {
			p[p.length - 1] = {
				...prev,
				content: prev.content + content,
			};
		} else {
			const item = { content, role };
			p.push(item);
			opt.current.onMessagePushed?.(item);
		}
		return messages.update([...p]);
	};
	const getLastMessage = () => messages.state[messages.state.length - 1];

	const executePrompt = (message: string = getLastMessage()?.content || "") =>
		opt.current.onExecutePrompt?.(
			{
				message,
				messages: messages.state,
				model: model.state,
				provider: provider.state,
			},
			(p) => pushMessage({ content: p, role: "agent" }),
		);
	const pushPrompt = (p: string = prompt.state) => {
		p = p.trim();
		if (!p.length) return messages.state;

		const prev = messages.state;
		const next = pushMessage({ content: p, role: "user" });
		console.log(
			prev,
			next,
			prev !== next,
			opt.current.executeOnPrompt !== false,
			model.state,
			provider.state,
		);
		if (prev !== next && opt.current.executeOnPrompt !== false) {
			executePrompt(p);
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
