import { useIsMutating, useMutation } from "@tanstack/react-query";
import {
	createContext,
	createElement,
	use,
	useEffect,
	useRef,
	useState,
} from "react";
import { useGetApiAiModels } from "@/gen/api/default/default";
import type { Chat, LLModel } from "@/gen/models";
import { useSignal, useSignalState } from "@/lib/hooks/state/signal";
import { fetchAiChat } from "./source";
import { type ChatState, type CreateChatOptions, createChat } from "./state";

export interface UseChatModelsReturn {
	model: string | undefined;
	models: LLModel[];
	status: "error" | "pending" | "success";
	error: Error | null;
	selectModel: (str: string | undefined | null) => void;
}

export function useChatMessages(
	s: ChatState = useChatContext(),
): Chat["messages"] {
	return useSignalState(s.messages);
}
export function useIsChatMutating() {
	return (
		useIsMutating({
			mutationKey: ["postAiChat"],
		}) > 0
	);
}

export function useChatModels(
	s: ChatState = useChatContext(),
): UseChatModelsReturn {
	const q = useGetApiAiModels();
	return {
		model: useSignalState(s.model) || undefined,
		models: q.data?.data || [],
		status: q.status,
		error: q.error as Error,
		selectModel: (m) => s.model.update(m || undefined),
	};
}

export function useChatInput(chat: ChatState = useChatContext()) {
	return [...useSignal(chat.prompt), chat.pushPrompt] as const;
}
const context = createContext<ChatState | null>(null);

export function useChatContext() {
	const c = use(context);
	if (!c) throw new Error("useChatContext must be used within a ChatProvider");
	return c;
}
export function ChatProvider({
	children,
	state,
}: {
	children: React.ReactNode;
	state: ChatState;
}) {
	return createElement(context.Provider, { value: state }, children);
}

function useChatMutation(scope: string) {
	return useMutation({
		mutationKey: ["postAiChat"],
		scope: {
			id: scope,
		},
		mutationFn: async ({
			chat,
			onMessage: onMutate,
		}: {
			chat: Chat;
			onMessage: (item: string) => void;
		}) => fetchAiChat(chat, (d) => onMutate(d)),
	});
}

export function useCreateChat(
	options: Omit<CreateChatOptions, "onExecutePrompt"> = {},
	scope: string = "global",
): ChatState {
	const m = useChatMutation(scope);
	const opt = useRef<CreateChatOptions>(options);
	opt.current = {
		...options,
		onExecutePrompt: (chat, onMessage) => m.mutate({ chat, onMessage }),
	};

	const s = useState(() => createChat(opt))[0];

	useEffect(() => {
		if (options.initial?.model) s.model.update(options.initial?.model);
		if (options.initial?.prompt?.trim()) s.pushPrompt(options.initial?.prompt);
	}, []);

	return s;
}
