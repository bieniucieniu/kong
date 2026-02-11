import { createFileRoute } from "@tanstack/react-router";
import { ChatInput } from "@/features/chat/components/chat-input";
import { ChatList } from "@/features/chat/components/chat-list";
import { ChatProvider, useCreateChat } from "@/features/chat/lib/chat";

export const Route = createFileRoute("/chat/$id")({
	component: RouteComponent,
	validateSearch: (s: unknown) => s as { prompt?: string },
});

function RouteComponent() {
	const { id } = Route.useParams();
	const initial = Route.useSearch();
	const state = useCreateChat({ initial }, id);

	return (
		<ChatProvider state={state}>
			<div className="h-full flex flex-col items-center justify-end gap-4 p-4">
				<div className="flex-1 w-full">
					<ChatList className="h-[calc(100svh-13rem)]" />
				</div>
				<ChatInput
					autoFocus
					className="max-w-100 [view-transition-name:main-input]"
				/>
			</div>
		</ChatProvider>
	);
}
