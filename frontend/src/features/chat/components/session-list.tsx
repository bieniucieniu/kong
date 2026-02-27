import { useInfiniteQuery } from "@tanstack/react-query";
import { Link } from "@tanstack/react-router";
import { useVirtualizer } from "@tanstack/react-virtual";
import { useEffect, useMemo, useRef, useState } from "react";
import type { NavEntry } from "@/components/app-sidebar/data";
import DebouncedInput from "@/components/debounce-input";
import {
	Item,
	ItemContent,
	ItemDescription,
	ItemTitle,
} from "@/components/ui/item";
import { getApiAiSessions, getGetApiAiSessionsQueryKey } from "@/gen/api/kong";

function useGetApiAiSessionsPaged(search?: string) {
	return useInfiniteQuery({
		queryKey: getGetApiAiSessionsQueryKey(search ? { search } : undefined),
		queryFn: async ({ pageParam }) => {
			const { data } = await getApiAiSessions(pageParam);
			return data;
		},
		initialPageParam: { offset: 0, count: 40 },
		getNextPageParam: (d) =>
			(d.end ?? d.data.length < d.count)
				? undefined
				: {
						offset: d.offset + d.count,
						count: d.count,
					},
	});
}

export function SessionList({ title }: NavEntry) {
	"use no memo";
	const [search, setSearch] = useState<string | undefined>();
	const parentRef = useRef<HTMLDivElement>(null);
	const q = useGetApiAiSessionsPaged(search?.trim() || undefined);
	const joined = useMemo(
		() => q.data?.pages.flatMap((it) => it.data) ?? [],
		[q.data?.pages],
	);
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
		<>
			<div className="gap-3.5 border-b p-4">
				<div className="flex w-full items-center justify-between">
					<div className="text-foreground text-base font-medium">{title}</div>
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
		</>
	);
}
