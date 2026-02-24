import { createFileRoute } from "@tanstack/react-router";
import { useMemo } from "react";
import { ChatInput } from "@/features/chat/components/prompt-input";
import {
	ChatPromptControllerProvider,
	useCreateChatPromptController,
} from "@/features/chat/state/prompt";

export const Route = createFileRoute("/chat/free")({
	component: RouteComponent,
	validateSearch: (s: unknown) => s as { prompt?: string },
});

function RouteComponent() {
	const initial = Route.useSearch();
	const state = useCreateChatPromptController(
		useMemo(
			() => ({
				prompt: initial.prompt ?? "",
			}),
			[initial],
		),
	);

	return (
		<ChatPromptControllerProvider value={state}>
			<div className="h-full flex flex-col relative items-center justify-end gap-4 p-4">
				<div className="flex-1 w-full">
					{/* <ChatList className="h-[calc(100svh-11rem)]" /> */}
				</div>
				<div className="bg-background absolute bottom-4 w-full max-w-180">
					<ChatInput
						autoFocus
						className="w-full max-w-180 [view-transition-name:main-input]"
					/>
				</div>
			</div>
		</ChatPromptControllerProvider>
	);
}
