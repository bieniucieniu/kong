import { createFileRoute } from "@tanstack/react-router";
import { ChatInput } from "@/features/chat/components/chat-input";
import { ChatList } from "@/features/chat/components/chat-list";
import { useCreateChat } from "@/features/chat/lib/chat";

export const Route = createFileRoute("/")({
	component: RouteComponent,
});

function RouteComponent() {
	const state = useCreateChat();

	return (
		<div className="h-full flex flex-col items-center justify-end gap-4 p-4">
			<div className="flex-1 w-full">
				<ChatList state={state} className="h-[calc(100svh-13rem)]" />
			</div>
			<ChatInput state={state} className="max-w-100" />
		</div>
	);
}
