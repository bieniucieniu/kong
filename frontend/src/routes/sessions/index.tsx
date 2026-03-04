import { createFileRoute } from "@tanstack/react-router";
import { SessionList } from "@/features/chat/components/session-list";

export const Route = createFileRoute("/sessions/")({
	component: RouteComponent,
});

function RouteComponent() {
	return <SessionList className="h-svh pt-4" />;
}
