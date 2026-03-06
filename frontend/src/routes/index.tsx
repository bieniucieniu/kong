import { createFileRoute } from "@tanstack/react-router";
import { NewChatMockInput } from "@/features/chat/components/new-chat";
import { useSession } from "@/features/session";
import { UserCard } from "@/features/session/components/user-card";

export const Route = createFileRoute("/")({
	component: RouteComponent,
});

function RouteComponent() {
	const n = Route.useNavigate();
	const s = useSession();

	return (
		<div className="h-full flex flex-col items-center justify-center gap-4 p-4">
			<div>
				{s.session ? (
					<NewChatMockInput
						onNewChat={(id) => {
							n({
								to: `/chat/$id`,
								params: { id },
								viewTransition: true,
							});
						}}
						className="min-w-[90svw] lg:min-w-120 max-w-svw [view-transition-name:main-input]"
					/>
				) : (
					<UserCard />
				)}
			</div>
		</div>
	);
}
