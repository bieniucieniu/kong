import { Link, useNavigate } from "@tanstack/react-router";
import { useVirtualizer } from "@tanstack/react-virtual";
import { useEffect, useMemo, useRef, useState } from "react";
import DebouncedInput from "@/components/debounce-input";
import {
	Item,
	ItemContent,
	ItemDescription,
	ItemTitle,
} from "@/components/ui/item";
import { useGetApiAiSessionsPaged } from "../queries/chat-session";
import { NewChatButton } from "./new-chat";

interface SessionListProps {
	className?: string;
}
export function SessionList({ className }: SessionListProps) {
	"use no memo";
	const n = useNavigate();
	const [search, setSearch] = useState<string | undefined>();
	const q = useGetApiAiSessionsPaged(search?.trim() || undefined);
	const joined = useMemo(
		() => q.data?.pages.flatMap((it) => it.data) ?? [],
		[q.data?.pages],
	);

	const parentRef = useRef<HTMLDivElement>(null);
	const v = useVirtualizer({
		count: joined.length,
		getScrollElement: () => parentRef.current,
		estimateSize: () => 35,
	});

	useEffect(() => {
		const items = v.getVirtualItems();
		const last = items.length > 0 ? items[items.length - 1] : undefined;
		if (!q.isSuccess || !last) return;

		if (
			last.index >= joined.length - 1 &&
			q.hasNextPage &&
			!q.isFetchingNextPage
		)
			q.fetchNextPage();
	}, [q.hasNextPage, joined.length, v.getVirtualItems(), q.isFetchingNextPage]);

	return (
		<div className={className}>
			<div className="flex flex-col gap-2 border-b p-4">
				<div className="flex w-full items-center justify-between">
					<div className="text-foreground text-base font-medium">sessions</div>
					<NewChatButton
						onNewChat={(id) => {
							n({
								to: `/chat/$id`,
								params: { id },
								viewTransition: true,
							});
						}}
					/>
				</div>
				<DebouncedInput
					placeholder="Type to search..."
					onDebouncedChange={(it) => setSearch(it.target.value)}
				/>
			</div>
			<div>
				<div
					ref={parentRef}
					className="h-[calc(100svh-7.5rem)]"
					style={{
						overflow: "auto",
					}}
				>
					<ul
						className="max-w-240 w-full relative"
						style={{
							height: `${v.getTotalSize()}px`,
						}}
					>
						{v.getVirtualItems().map(({ key, start, index }) => {
							const d = joined[index];
							if (!d) return null;
							return (
								<Item
									ref={v.measureElement}
									data-index={index}
									className={"absolute top-0"}
									key={key}
									style={{
										transform: `translateY(${start}px)`,
									}}
									render={(props) => (
										<Link
											to={`/chat/$id`}
											activeProps={{ className: "bg-muted" }}
											params={{ id: d.id }}
											viewTransition
											{...props}
										/>
									)}
								>
									{/* <ItemMedia variant="icon"> */}
									{/* 	<InfoIcon /> */}
									{/* </ItemMedia> */}
									<ItemContent>
										<ItemTitle className="text-nowrap text-ellipsis">
											{d.name ?? <span className="text-muted italic">new</span>}
										</ItemTitle>
										<ItemDescription>{d.updatedAt}</ItemDescription>
									</ItemContent>
								</Item>
							);
						})}
					</ul>
				</div>
			</div>
		</div>
	);
}
