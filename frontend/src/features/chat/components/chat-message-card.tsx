import { ClipboardListIcon } from "lucide-react";
import { Button } from "@/components/ui/button";
import { ButtonGroup } from "@/components/ui/button-group";
import { Markdown } from "@/components/ui/markdown";
import type { ChatPromptMessagesItem } from "@/gen/models";
import { cn } from "@/lib/utils";

export function ChatMessageCard({
	message: { prompt, author },
	className,
}: {
	className?: string;
	message: ChatPromptMessagesItem;
}) {
	return (
		<div className={cn("relative px-2 py-1 min-w-48", className)}>
			<div className={cn("w-full flex justify-between items-center gap-2")}>
				<strong>{author}</strong>
				<ButtonGroup>
					<Button title="copy markdown" size="icon-sm" variant="ghost">
						<ClipboardListIcon />
					</Button>
				</ButtonGroup>
			</div>
			<Markdown>{prompt}</Markdown>
		</div>
	);
}
