import { createFileRoute } from "@tanstack/react-router";
import { NewChatMockInput } from "@/features/chat/components/new-chat";

export const Route = createFileRoute("/")({
	component: RouteComponent,
});

function RouteComponent() {
	const n = Route.useNavigate();

	return (
		<div className="h-full flex flex-col items-center justify-center gap-4 p-4">
			<div>
				<NewChatMockInput
					onNewChat={(id) => {
						n({
							to: `/chat/$id`,
							params: { id },
							viewTransition: true,
						});
					}}
					className="min-w-120 max-w-svw [view-transition-name:main-input]"
				/>
			</div>
		</div>
	);
}
