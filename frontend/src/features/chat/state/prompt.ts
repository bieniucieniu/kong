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
	state: ChatPromptState;
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
}

export const globalChatPromptController = new ChatPromptController();

effect(() => globalChatPromptController.syncWithStorage());

const context = createContext<ChatPromptController>(globalChatPromptController);

export function useChatPromptController() {
	return use(context);
}
export function useChatPromptState(
	c: ChatPromptController = useChatPromptController(),
) {
	return useProxy(c.state);
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
export function useCreateChatPromptController(
	fork: boolean | ChatPromptController | Partial<ChatPromptState> = false,
	key: string = "global",
	deps: React.DependencyList = [fork, key],
) {
	const prev = fork
		? fork instanceof ChatPromptController
			? fork.state
			: typeof fork === "object"
				? fork
				: use(context)?.state
		: undefined;

	const p = useMemo(() => new ChatPromptController(prev, key), deps);
	useEffect(() => p.syncWithStorage(), [p]);
	return p;
}
