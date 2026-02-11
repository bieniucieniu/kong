import { createFileRoute } from "@tanstack/react-router";
import { ChatInput } from "@/features/chat/components/chat-input";
import { useCreateChat } from "@/features/chat/lib/chat";

export const Route = createFileRoute("/")({
	component: RouteComponent,
});

function RouteComponent() {
	const n = Route.useNavigate();
	const state = useCreateChat(
		{
			executeOnPrompt: false,
			onMessagePushed: () => {},
		},
		"new",
	);

	return (
		<div className="h-full flex flex-col items-center justify-center gap-4 p-4">
			<div>
				<ChatInput
					onFocus={() =>
						n({
							to: "/chat/$id",
							params: { id: "new" },
							viewTransition: true,
						})
					}
					state={state}
					className="min-w-120 max-w-svw [view-transition-name:main-input]"
				/>
			</div>
		</div>
	);
}
