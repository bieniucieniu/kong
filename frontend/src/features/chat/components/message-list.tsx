import { useVirtualizer } from "@tanstack/react-virtual";
import { useRef } from "react";
import { cn } from "@/lib/utils";
import { useChatQuery } from "..";
import type { ChatController } from "../state";
import { MessageCard } from "./message-card";

export function MessageList({
	id,
	className,
}: {
	controller?: ChatController;
	id: string;
	className?: string;
}) {
	const q = useChatQuery(id);
	const parentRef = useRef<HTMLDivElement>(null);
	const v = useVirtualizer({
		count: q.data?.data.length ?? 0,
		getScrollElement: () => parentRef.current,
		estimateSize: () => 35,
	});
	return (
		<div
			ref={parentRef}
			className={className}
			style={{
				overflow: "auto",
			}}
		>
			<div
				style={{
					height: `${v.getTotalSize()}px`,
					width: "100%",
					position: "relative",
				}}
			>
				{/* Only the visible items in the virtualizer, manually positioned to be in view */}
				{v.getVirtualItems().map(({ index, key, start }) => {
					const d = q.data?.data[index];
					if (!d) return null;
					return (
						<MessageCard
							ref={v.measureElement}
							data-index={index}
							className={cn("absolute top-0 w-[90%]", {
								"right-0": d.role === "user",
								"left-0": d.role === "agent",
							})}
							style={{
								transform: `translateY(${start}px)`,
							}}
							key={key}
							message={d}
						/>
					);
				})}
			</div>
		</div>
	);
}
