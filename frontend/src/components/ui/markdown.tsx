import type { ComponentProps } from "react";
import ReactMarkdown from "react-markdown";
import { cn } from "@/lib/utils";
export type MarkdownProps = ComponentProps<typeof ReactMarkdown>;

const cnMarkdown = cn(
	"flex flex-col gap-2 px-4",
	"[&_ol]:list-decimal [&_ul]:list-disc [&_ul]:p-4 [&_ol]:px-6 [&_ol]:py-4",
	"",
);
export function MarkdownCard({
	className,
	...props
}: MarkdownProps & {
	className?: string;
}) {
	return (
		<div className={cn(cnMarkdown, className)}>
			<ReactMarkdown {...props} />
		</div>
	);
}
