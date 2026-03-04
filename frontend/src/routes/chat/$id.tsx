import type { QueryClient } from "@tanstack/react-query";
import { createFileRoute, redirect } from "@tanstack/react-router";
import { initChatQuery } from "@/features/chat";
import { ChatHeader } from "@/features/chat/components/chat-header";
import { MessageList } from "@/features/chat/components/message-list";
import { ChatInput } from "@/features/chat/components/prompt-input";
import {
	ChatControllerProvider,
	useCreateChatController,
} from "@/features/chat/state";
import { getGetApiAiChatIdQueryOptions } from "@/gen/api/kong";

export const Route = createFileRoute("/chat/$id")({
	beforeLoad: async (ctx) => {
		const client = (ctx.context as { client: QueryClient }).client;
		if (ctx.params.id === "free") initChatQuery(client, ctx.params.id);
		else
			try {
				await client.ensureQueryData(
					getGetApiAiChatIdQueryOptions(ctx.params.id),
				);
			} catch (_) {
				throw redirect({ to: "/" });
			}
	},
	component: RouteComponent,
});

function RouteComponent() {
	const id = Route.useParams().id;
	const state = useCreateChatController(false, [id]);

	return (
		<ChatControllerProvider value={state}>
			<div className="h-full flex flex-col relative items-center justify-end gap-4 px-4">
				<ChatHeader id={id} className="absolute top-0 left-0 z-10" />
				<MessageList
					id={id}
					className="h-[calc(100svh-1px)] mx-auto w-full flex justify-center"
					paddingStart={50}
					paddingEnd={200}
				/>
				<div className="absolute bottom-4 w-full inset-x-0 px-2">
					<ChatInput
						id={id}
						autoFocus
						className="w-full bg-background/80 dark:bg-background/80 has-disabled:bg-background/80 dark:has-disabled:bg-background/80 backdrop-blur-xs mx-auto lg:max-w-180 [view-transition-name:main-input]"
					/>
				</div>
			</div>
		</ChatControllerProvider>
	);
}
