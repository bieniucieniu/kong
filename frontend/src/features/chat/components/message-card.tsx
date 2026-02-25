import { cva, type VariantProps } from "class-variance-authority";
import { useMemo } from "react";
import { Spinner } from "@/components/ui/spinner";
import type { ChatMessage } from "@/gen/models";
import { cn } from "@/lib/utils";

const messageCardVariants = cva(
	"focus-visible:border-ring focus-visible:ring-ring/50 aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive dark:aria-invalid:border-destructive/50 rounded-none border border-transparent bg-clip-padding text-xs font-medium focus-visible:ring-1 aria-invalid:ring-1 [&_svg:not([class*='size-'])]:size-4 inline-flex flex-col items-start justify-start whitespace-nowrap transition-all disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none shrink-0 [&_svg]:shrink-0 outline-none group/card p-2",
	{
		variants: {
			variant: {
				user: "bg-primary text-primary-foreground [a]:hover:bg-primary/80",
				agent:
					"bg-secondary text-secondary-foreground hover:bg-secondary/80 aria-expanded:bg-secondary aria-expanded:text-secondary-foreground",
				tool: "hover:bg-muted hover:text-foreground dark:hover:bg-muted/50 aria-expanded:bg-muted aria-expanded:text-foreground",
				destructive:
					"bg-destructive/10 hover:bg-destructive/20 focus-visible:ring-destructive/20 dark:focus-visible:ring-destructive/40 dark:bg-destructive/20 text-destructive focus-visible:border-destructive/40 dark:hover:bg-destructive/30",
			},
		},
		defaultVariants: {
			variant: "agent",
		},
	},
);

export function MessageCard({
	message,
	variant = message.role,
	className,
	...props
}: {
	message: ChatMessage;
} & React.HTMLProps<HTMLDivElement> &
	VariantProps<typeof messageCardVariants>) {
	const date = useMemo(
		() => message.createAt && new Date(message.createAt).toLocaleString(),
		[message.createAt],
	);
	return (
		<div className={cn(messageCardVariants({ variant, className }))} {...props}>
			<div className="grid grid-cols-[1fr_auto] w-full opacity-50 group-hover/card:opacity-100 transition-opacity pb-2">
				<div className="text-xs font-medium">{message.role}</div>
				<div className="text-xs">{date}</div>
			</div>
			{message.content.trim() ? (
				<p className="pl-2 max-w-full text-wrap">{message.content}</p>
			) : (
				<Spinner />
			)}
		</div>
	);
}
