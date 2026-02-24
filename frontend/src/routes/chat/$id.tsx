import { createFileRoute } from "@tanstack/react-router";
import { ChatInput } from "@/features/chat/components/prompt-input";
import { useCreateChatController } from "@/features/chat/state";
import { ChatPromptControllerProvider } from "@/features/chat/state/prompt";

export const Route = createFileRoute("/chat/$id")({
	component: RouteComponent,
});

function RouteComponent() {
	const state = useCreateChatController();

	return (
		<ChatPromptControllerProvider value={state.state.prompt}>
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
