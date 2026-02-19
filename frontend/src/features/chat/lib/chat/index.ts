import { useIsMutating, useMutation } from "@tanstack/react-query";
import {
	createContext,
	createElement,
	use,
	useEffect,
	useRef,
	useState,
} from "react";
import {
	useGetApiAiModelsProviderId,
	useGetApiAiModelsProviderIdDefault,
	useGetApiAiProviders,
	useGetApiAiProvidersDefault,
} from "@/gen/api/default/default";
import type {
	ChatPrompt,
	ChatPromptsList,
	ErrorResponse,
	SerializableLLModel,
	SerializableLLMProvider,
} from "@/gen/models";
import { useSignal, useSignalState } from "@/lib/hooks/state/signal";
import { fetchAiChat, fetchAiChatFree } from "./source";
import { type ChatState, type CreateChatOptions, createChat } from "./state";

export interface UseChatModelsReturn {
	model: string | undefined;
	models: SerializableLLModel[];
	provider: string | undefined;
	providers: SerializableLLMProvider[];
	status: "error" | "pending" | "success";
	error: ErrorResponse | null;
	selectModel: (str: string | undefined | null) => void;
	selectProvider: (str: string | undefined | null) => void;
}

export function useChatMessages(
	s: ChatState = useChatContext(),
): ChatPromptsList["messages"] {
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
	const providers = useGetApiAiProviders();
	const defaultProvider = useGetApiAiProvidersDefault();
	const provider = useSignalState(s.provider) || defaultProvider.data?.data.id;

	const models = useGetApiAiModelsProviderId(provider ?? "", {
		query: { enabled: !!provider },
	});
	const defaultModel = useGetApiAiModelsProviderIdDefault(provider ?? "", {
		query: { enabled: !!provider },
	});

	const model = useSignalState(s.model) || defaultModel.data?.data.id;
	return {
		model,
		models: models.data?.data || [],
		provider,
		providers: providers.data?.data || [],
		status: providers.status !== "success" ? providers.status : models.status,
		error:
			models.error ||
			defaultModel.error ||
			providers.error ||
			defaultProvider.error,
		selectModel: (m) => s.model.update(m || undefined),
		selectProvider: (m) => s.provider.update(m || undefined),
	};
}

export function useChatInput(chat: ChatState = useChatContext()) {
	const [prompt, setPrompt] = useSignal(chat.prompt);
	return [prompt, setPrompt, chat.pushPrompt] as const;
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

function useChatMutation(id: string) {
	return useMutation({
		mutationKey: ["postAiChat"],
		scope: {
			id,
		},
		mutationFn: async ({
			id,
			chat,
			onMessage: onMutate,
		}: {
			id: string;
			chat: ChatPromptsList & ChatPrompt;
			onMessage: (item: string) => void;
		}) => {
			if (id === "free") {
				return fetchAiChatFree(
					{
						messages: chat.messages,
						model: chat.model,
						provider: chat.provider,
					},
					(d) => onMutate(d),
				);
			}

			return fetchAiChat(
				id,
				{
					message: chat.message,
					model: chat.model,
					provider: chat.provider,
				},
				(d) => onMutate(d),
			);
		},
	});
}

export function useCreateChat(
	id: string,
	options: Omit<CreateChatOptions, "onExecutePrompt"> = {},
	scope: string = "global",
): ChatState {
	const m = useChatMutation(scope);
	const opt = useRef<CreateChatOptions>(options);
	opt.current = {
		...options,
		onExecutePrompt: (chat, onMessage) => {
			return m.mutate({ id, chat, onMessage });
		},
	};

	const s = useState(() => createChat(opt))[0];

	useEffect(() => {
		if (options.initial?.prompt?.trim()) s.pushPrompt(options.initial?.prompt);
	}, [options.initial?.prompt]);

	return s;
}
