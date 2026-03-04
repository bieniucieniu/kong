import { cn } from "@/lib/utils";
import { useChatSessionQuery } from "..";

export function ChatHeader({
	className,
	id,
}: {
	id: string;
	className?: string;
}) {
	const q = useChatSessionQuery(id);
	return (
		<header
			className={cn("bg-background/80 backdrop-blur-xs border px-2", className)}
		>
			{q.data?.data.name} {q.data?.data.id}
		</header>
	);
}
