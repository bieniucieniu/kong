import { createContext, createElement, use, useMemo } from "react";
import { proxy } from "valtio";
import { useProxy } from "valtio/utils";
import {
	type ChatPromptController,
	ChatPromptControllerProvider,
	globalChatPromptController,
	useChatPromptController,
} from "./prompt";

export interface ChatState {
	prompt: ChatPromptController;
}

export class ChatController {
	state: ChatState;

	constructor(init?: Partial<ChatState>) {
		this.state = proxy<ChatState>({
			prompt: init?.prompt ?? globalChatPromptController,
		});
	}
}

export const globalChatController = new ChatController();

//effect(() => globalChatController.syncWithStorage());

const context = createContext<ChatController>(globalChatController);

export function useChatController() {
	return use(context);
}
export function useChatState(c: ChatController = useChatController()) {
	return useProxy(c.state);
}

export function ChatControllerProvider({
	children,
	...props
}: {
	value: ChatController;
	children: React.ReactNode;
}) {
	return createElement(
		context.Provider,
		props,
		createElement(
			ChatPromptControllerProvider,
			//@ts-expect-error
			{ value: props.value.state.prompt },
			children,
		),
	);
}

/**
 * Create a new ChatController instance.
 *
 * @param fork - If true, the new ChatController will be a fork of the existing one.
 * @param deps - Dependencies of the new ChatController by default set to [fork]
 * @returns A new ChatController instance.
 */
export function useCreateChatController(
	fork: boolean | ChatController | Partial<ChatState> = false,
	deps: React.DependencyList = [fork],
) {
	const prev = fork
		? fork instanceof ChatController
			? fork.state
			: typeof fork === "object"
				? fork
				: use(context)?.state
		: {};

	prev.prompt ||= useChatPromptController();

	const p = useMemo(() => new ChatController(prev), deps);
	return p;
}
