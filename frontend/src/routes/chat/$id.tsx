import type { QueryClient } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import { initChatQuery } from "@/features/chat";
import { MessageList } from "@/features/chat/components/message-list";
import { ChatInput } from "@/features/chat/components/prompt-input";
import {
	ChatControllerProvider,
	useCreateChatController,
} from "@/features/chat/state";

export const Route = createFileRoute("/chat/$id")({
	beforeLoad: (ctx) => {
		if (ctx.params.id === "free")
			initChatQuery(
				(ctx.context as { client: QueryClient }).client,
				ctx.params.id,
			);
	},
	component: RouteComponent,
});

function RouteComponent() {
	const id = Route.useParams().id;
	const state = useCreateChatController(false, [id]);

	return (
		<ChatControllerProvider value={state}>
			<div className="h-full flex flex-col relative items-center justify-end gap-4 p-4">
				<div className="flex-1 w-full">
					<MessageList
						id={id}
						className="h-[calc(100svh-11rem)] mx-auto w-full max-w-180 bg-red-500"
					/>
				</div>
				<div className="bg-background absolute bottom-4 w-full max-w-180">
					<ChatInput
						id={id}
						autoFocus
						className="w-full max-w-180 [view-transition-name:main-input]"
					/>
				</div>
			</div>
		</ChatControllerProvider>
	);
}
