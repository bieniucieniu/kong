import { createContext, createElement, use, useEffect, useMemo } from "react";
import { proxy } from "valtio";
import { useProxy } from "valtio/utils";
import { effect } from "valtio-reactive";
import { getStored, updateStored } from "@/lib/hooks/state/storage";

interface ChatPromptState {
	provider: string | undefined;
	model: string | undefined;
	prompt: string;
}
export class ChatPromptController {
	private state: ChatPromptState;
	key: string;
	constructor(init?: Partial<ChatPromptState>, key: string = "global") {
		this.key = key;
		this.state = proxy<ChatPromptState>({
			prompt: init?.prompt ?? "",
			provider: getStored(`${key}-provider`, init?.provider),
			model: getStored(`${key}-model`, init?.model),
		});
	}

	syncWithStorage(store: Storage = localStorage) {
		return effect(() => {
			updateStored(`${this.key}-provider`, this.state.provider, store);
			updateStored(`${this.key}-model`, this.state.model, store);
		});
	}

	getState() {
		return this.state;
	}
}

export const globalChatPromptController = new ChatPromptController();

effect(() => globalChatPromptController.syncWithStorage());

const context = createContext<ChatPromptController>(globalChatPromptController);

export function useChatPromptController() {
	return use(context);
}
export function useChatPrompt(
	c: ChatPromptController = useChatPromptController(),
) {
	return useProxy(c.getState());
}

export function ChatPromptControllerProvider({
	children,
	...props
}: {
	value: ChatPromptController;
	children: React.ReactNode;
}) {
	return createElement(context.Provider, props, children);
}

/**
 * Create a new ChatController instance.
 *
 * @param fork - If true, the new ChatController will be a fork of the existing one.
 * @param deps - Dependencies of the new ChatController by default set to [fork]
 * @returns A new ChatController instance.
 */
export function useCreateChatPromptContext(
	fork: boolean | ChatPromptController | Partial<ChatPromptState> = false,
	deps: React.DependencyList = [fork],
) {
	const prev = fork
		? fork instanceof ChatPromptController
			? fork.getState()
			: typeof fork === "object"
				? fork
				: use(context)?.getState()
		: undefined;

	const p = useMemo(() => new ChatPromptController(prev), deps);
	useEffect(() => p.syncWithStorage(), [p]);
	return p;
}
