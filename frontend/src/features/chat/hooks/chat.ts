import { useMutation } from "@tanstack/react-query";
import { useMemo, useState } from "react";
import { getPostAiChatUrl, useGetAiModels } from "@/gen/api/default/default";
import type { Chat, ChatMessagesItem } from "@/gen/models";
import {
	createSignal,
	type SignalState,
	useSignalState,
} from "@/lib/hooks/state/signal";
import { useUpdateCallback } from "@/lib/hooks/update";

export interface UseChatReturn {
	chat: Chat["messages"];
	status: "idle" | "error" | "pending" | "success";
	error: Error | null;
	pushPrompt: (p: string) => void;
	pushMessage: (
		{ prompt, author }: ChatMessagesItem,
		on?: (c: ChatMessagesItem[]) => void,
	) => void;
}

export interface UseChatModelsReturn {
	model: Chat["model"];
	models: string[];
	status: "error" | "pending" | "success";
	error: Error | null;
	selectModel: (str: string | null) => void;
}

class ChatState {
	messages: SignalState<Chat["messages"]> = createSignal([]);
	model: SignalState<Chat["model"]> = createSignal(null);
}
export type { ChatState };

export function useCreateChat(): ChatState {
	return useState(() => new ChatState())[0];
}

export function useChat(s: ChatState): UseChatReturn {
	const state = useState<UseChatReturn>(() => {
		const pushMessage = (
			{ prompt, author }: ChatMessagesItem,
			on?: (c: ChatMessagesItem[]) => void,
		) => {
			const o = ((p) => {
				const prev = p.length > 0 ? p[p.length - 1] : undefined;
				if (prev?.author === author) {
					p[p.length - 1] = {
						...prev,
						prompt: prev.prompt + prompt,
					};
					return [...p];
				}
				return [...p, { prompt: prompt, author }];
			})(s.messages.state);
			on?.(o);
			s.messages.update(o);
		};
		const pushPrompt = (p: string) => {
			p = p.trim();
			if (p.length === 0) return;
			pushMessage({ prompt: p, author: "user" }, (messages) => {
				m.mutate({ messages, model: s.model.state });
			});
		};
		return {
			chat: [],
			status: "idle",
			error: null,
			pushPrompt,
			pushMessage,
		};
	})[0];

	const m = useMutation({
		mutationKey: ["postAiChat"],
		mutationFn: async (p: Chat) => {
			const res = await fetch(getPostAiChatUrl(), {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify(p),
			});
			if (res.status >= 400) throw new Error(res.statusText);
			const body = [204, 205, 304].includes(res.status) ? null : res.body;

			const contentType = res.headers.get("Content-Type");
			if (contentType?.includes("text/event-stream")) {
				const reader = body?.getReader();
				await collect(reader, (d) =>
					state.pushMessage({ prompt: d, author: "agent" }),
				);
			} else {
				state.pushMessage({ prompt: await res.text(), author: "agent" });
			}
		},
	});
	state.status = m.status;
	state.error = m.error;
	state.chat = useSignalState(s.messages);

	return state;
}
export function useChatModels(s: ChatState): UseChatModelsReturn {
	const state = useMemo<UseChatModelsReturn>(() => {
		const selectModel: UseChatModelsReturn["selectModel"] = (str) =>
			s.model.update(str);
		return {
			model: s.model.state,
			models: [],
			status: "pending",
			error: null,
			selectModel,
		};
	}, [s]);

	const models = useGetAiModels();
	state.models = models.data?.data.models ?? state.models;
	state.status = models.status;
	state.model = useSignalState(s.model) ?? models.data?.data.default;

	return state;
}

export function useCollect(
	reader: ReadableStreamDefaultReader<Uint8Array<ArrayBuffer>> | undefined,
	onData: (data: string) => void,
) {
	onData = useUpdateCallback(onData);

	useMemo(() => collect(reader, onData), [reader]);
}
export async function collect(
	reader: ReadableStreamDefaultReader<Uint8Array<ArrayBuffer>> | undefined,
	onData: (data: string) => void,
) {
	if (!reader) return;
	const decoder = new TextDecoder();
	while (true) {
		const { done, value } = await reader.read();
		if (done) break;

		const chunk = decoder.decode(value, { stream: true });

		onData(chunk);
	}
}
