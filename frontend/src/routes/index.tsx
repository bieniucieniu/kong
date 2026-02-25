import { createFileRoute } from "@tanstack/react-router";
import { ChatInput } from "@/features/chat/components/prompt-input";
import { useSession } from "@/features/session";
import { usePostApiAiChatNew } from "@/gen/api/kong";

export const Route = createFileRoute("/")({
	component: RouteComponent,
});

function RouteComponent() {
	const n = Route.useNavigate();
	const m = usePostApiAiChatNew();
	const s = useSession();

	return (
		<div className="h-full flex flex-col items-center justify-center gap-4 p-4">
			<div>
				<ChatInput
					id=""
					disabled={s.isLoading}
					onFocus={async () => {
						if (s.isLoading) return;

						if (!s.session)
							n({
								to: "/chat/$id",
								params: { id: "free" },
								viewTransition: true,
							});
						const a = await m.mutateAsync();
						n({
							to: `/chat/$id`,
							params: { id: a.data.id },
							viewTransition: true,
						});
					}}
					className="min-w-120 max-w-svw [view-transition-name:main-input]"
				/>
			</div>
		</div>
	);
}
