import { useVirtualizer } from "@tanstack/react-virtual";
import { useRef } from "react";
import { type ChatController, useChatState } from "../state";

export function MessageList({ controller }: { controller?: ChatController }) {
	const s = useChatState(controller);
	const parentRef = useRef<HTMLDivElement>(null);
	const v = useVirtualizer({
		count: s.messages.length,
		getScrollElement: () => parentRef.current,
		estimateSize: () => 35,
	});
	return (
		<div
			ref={parentRef}
			style={{
				height: `400px`,
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
				{v.getVirtualItems().map((virtualItem) => (
					<div
						key={virtualItem.key}
						style={{
							position: "absolute",
							top: 0,
							left: 0,
							width: "100%",
							height: `${virtualItem.size}px`,
							transform: `translateY(${virtualItem.start}px)`,
						}}
					>
						Row {virtualItem.index}
					</div>
				))}
			</div>
		</div>
	);
}
