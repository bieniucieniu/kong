import { useVirtualizer } from "@tanstack/react-virtual";
import { useEffect, useRef } from "react";
import { useGetApiAiSessions } from "@/gen/api/kong";

export function SessionList({ className }: { className?: string }) {
	const q = useGetApiAiSessions();
	const parentRef = useRef<HTMLDivElement>(null);
	const v = useVirtualizer({
		count: q.data?.data.length ?? 0,
		getScrollElement: () => parentRef.current,
		estimateSize: () => 35,
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
					const d = q.data?.data?.[index];
					if (!d) return null;
					return (
						<div
							key={d.id}
							className="absolute top-0 w-[90%]"
							style={{
								transform: `translateY(${start}px)`,
							}}
						>
							{d.name ?? d.id}
						</div>
					);
				})}
			</div>
		</div>
	);
}
