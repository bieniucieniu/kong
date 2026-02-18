import { createFileRoute } from "@tanstack/react-router";
import { ChatInput } from "@/features/chat/components/chat-input";
import { ChatList } from "@/features/chat/components/chat-list";
import { ChatProvider, useCreateChat } from "@/features/chat/lib/chat";

export const Route = createFileRoute("/chat/")({
	component: RouteComponent,
	validateSearch: (s: unknown) => s as { prompt?: string; id?: string },
});

function RouteComponent() {
	const n = Route.useNavigate();
	const initial = Route.useSearch();
	const state = useCreateChat({ initial }, (id) => {
		console.log(id);
		n({
			search: { id },
		});
	});

	return (
		<ChatProvider state={state}>
			<div className="h-full flex flex-col relative items-center justify-end gap-4 p-4">
				<div className="flex-1 w-full">
					<ChatList className="h-[calc(100svh-11rem)]" />
				</div>
				<div className="bg-background absolute bottom-4 w-full max-w-180">
					<ChatInput
						autoFocus
						className="w-full max-w-180 [view-transition-name:main-input]"
					/>
				</div>
			</div>
		</ChatProvider>
	);
}
