import { useIsMutating, useMutation } from "@tanstack/react-query";
import { useId, useMemo, useState } from "react";
import { getPostAiChatUrl, useGetAiModels } from "@/gen/api/default/default";
import type { Chat, ChatMessagesItem } from "@/gen/models";
import {
	createSignal,
	type SignalState,
	useSignalState,
} from "@/lib/hooks/state/signal";
import { collect } from "./collect";

export interface UseChatReturn {
	chat: Chat["messages"];
	status: "idle" | "error" | "pending" | "success";
	error: Error | null;
	pushPrompt: (p: string) => void;
	pushMessage: (args: ChatMessagesItem) => void;
}

export interface UseChatModelsReturn {
	model: Chat["model"];
	models: string[];
	status: "error" | "pending" | "success";
	error: Error | null;
	selectModel: (str: string | null) => void;
}

class ChatState {
	constructor(scope?: string) {
		if (scope) this.scope = createSignal(scope);
	}
	messages: SignalState<Chat["messages"]> = createSignal([]);
	model: SignalState<Chat["model"]> = createSignal(null);
	scope: SignalState<string> = createSignal("global");
	pushMessage = ({ prompt, author }: ChatMessagesItem) => {
		const p = this.messages.state;
		const prev = p.length > 0 ? p[p.length - 1] : undefined;
		if (prev?.author === author) {
			p[p.length - 1] = {
				...prev,
				prompt: prev.prompt + prompt,
			};
		} else {
			p.push({ prompt: prompt, author: author });
		}
		return this.messages.update([...p]);
	};
	pushPrompt = (p: string) => {
		p = p.trim();
		if (!p.length) return this.messages.state;
		return this.pushMessage({ prompt: p, author: "user" });
	};
}
export type { ChatState };

export function useCreateChat(scope: string = useId()): ChatState {
	return useMemo(() => new ChatState(scope), [scope]);
}

export function useChat(s: ChatState): UseChatReturn {
	const state = useState<UseChatReturn>(() => {
		const pushPrompt = (p: string) => {
			const prev = s.messages.state;
			const messages = s.pushPrompt(p);
			if (prev !== messages) m.mutate({ messages, model: s.model.state });
		};
		return {
			chat: [],
			status: "idle",
			error: null,
			pushPrompt,
			pushMessage: s.pushMessage,
		};
	})[0];

	const m = useMutation({
		mutationKey: ["postAiChat"],
		scope: {
			id: useSignalState(s.scope),
		},
		mutationFn: async (p: Chat) =>
			fetchAiChat(p, (d) => {
				console.log(d);
				state.pushMessage({
					prompt: d,
					author: "agent",
				});
			}),
	});
	state.status = m.status;
	state.error = m.error;
	state.chat = useSignalState(s.messages);

	return state;
}
export function useIsChatMutating() {
	return (
		useIsMutating({
			mutationKey: ["postAiChat"],
		}) > 0
	);
}

async function fetchAiChat(chat: Chat, onCollect: (d: string) => void) {
	const res = await fetch(getPostAiChatUrl(), {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(chat),
	});
	if (res.status >= 400) throw new Error(res.statusText);
	const body = [204, 205, 304].includes(res.status) ? null : res.body;

	const contentType = res.headers.get("Content-Type");
	if (contentType?.includes("text/event-stream")) {
		const reader = body?.getReader();
		await collect(reader, onCollect);
	} else {
		onCollect(await res.text());
	}
}

export function useChatModels(s: ChatState): UseChatModelsReturn {
	"use no memo";
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
	state.model = useSignalState(s.model) || models.data?.data.default;

	return state;
}
