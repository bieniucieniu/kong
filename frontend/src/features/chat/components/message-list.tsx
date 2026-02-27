import { useVirtualizer } from "@tanstack/react-virtual";
import { useEffect, useRef } from "react";
import { cn } from "@/lib/utils";
import { useChatQuery } from "..";
import { MessageCard } from "./message-card";

export function MessageList({
	id,
	className,
	paddingEnd,
	paddingStart,
}: {
	id: string;
	className?: string;
	paddingEnd?: number;
	paddingStart?: number;
}) {
	"use no memo";
	const q = useChatQuery(id);
	const parentRef = useRef<HTMLDivElement>(null);
	const v = useVirtualizer({
		count: q.data?.data.length ?? 0,
		getScrollElement: () => parentRef.current,
		estimateSize: () => 35,
		paddingEnd,
		paddingStart,
	});

	const last = q.data?.data[q.data?.data.length - 1];
	useEffect(() => {
		if (q.data?.data)
			v.scrollToIndex(q.data?.data.length - 1, { behavior: "smooth" });
	}, [last]);

	return (
		<div
			ref={parentRef}
			className={className}
			style={{
				overflow: "auto",
			}}
		>
			<div
				className="max-w-240 w-full relative"
				style={{
					height: `${v.getTotalSize()}px`,
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
								"left-0": d.role !== "user",
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
