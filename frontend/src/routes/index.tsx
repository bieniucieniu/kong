import { createFileRoute } from "@tanstack/react-router";
import { ChatInput } from "@/features/chat/components/chat-input";
import { useCreateChat } from "@/features/chat/lib/chat";
import { usePostApiAiChatNew } from "@/gen/api/default/default";

export const Route = createFileRoute("/")({
	component: RouteComponent,
});

function RouteComponent() {
	const n = Route.useNavigate();
	const state = useCreateChat("moc", {
		executeOnPrompt: false,
		onMessagePushed: () => {},
	});
	const m = usePostApiAiChatNew();

	return (
		<div className="h-full flex flex-col items-center justify-center gap-4 p-4">
			<div>
				<ChatInput
					onFocus={async () => {
						const a = await m.mutateAsync();
						n({
							to: `/chat/${a.data.id}`,
							viewTransition: true,
						});
					}}
					state={state}
					className="min-w-120 max-w-svw [view-transition-name:main-input]"
				/>
			</div>
		</div>
	);
}
