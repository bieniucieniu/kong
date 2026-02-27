import { useInfiniteQuery } from "@tanstack/react-query";
import { Link } from "@tanstack/react-router";
import { useRef, useState } from "react";
import type { NavEntry } from "@/components/app-sidebar/data";
import { DebouncedSidebarInput } from "@/components/debounce-input";
import {
	Item,
	ItemActions,
	ItemContent,
	ItemDescription,
	ItemTitle,
} from "@/components/ui/item";
import {
	SidebarContent,
	SidebarGroup,
	SidebarGroupContent,
	SidebarHeader,
} from "@/components/ui/sidebar";
import { getApiAiSessions, getGetApiAiSessionsQueryKey } from "@/gen/api/kong";
import { useInfiniteQueryVirtualizer } from "@/integration/ts-virtual";

function useGetApiAiSessionsPaged(search?: string) {
	return useInfiniteQuery({
		queryKey: getGetApiAiSessionsQueryKey(search ? { search } : undefined),
		queryFn: async ({ pageParam }) => {
			const { data } = await getApiAiSessions(pageParam);
			return data;
		},
		initialPageParam: { offset: 0, count: 40 },
		getNextPageParam: (d, _) => {
			return d.end
				? null
				: {
						offset: d.offset + d.count,
						count: d.count,
					};
		},
	});
}

export function SidebarSessionList({ title }: NavEntry) {
	"use no memo";
	const [search, setSearch] = useState<string | undefined>();
	const parentRef = useRef<HTMLDivElement>(null);
	const q = useGetApiAiSessionsPaged(search?.trim() || undefined);
	const { get, virtualizer } = useInfiniteQueryVirtualizer(q, {
		joinFn: (d) => d?.pages.flatMap((p) => p.data) || [],
		getScrollElement: () => parentRef.current,
	});

	return (
		<>
			<SidebarHeader className="gap-3.5 border-b p-4">
				<div className="flex w-full items-center justify-between">
					<div className="text-foreground text-base font-medium">{title}</div>
				</div>
				<DebouncedSidebarInput
					placeholder="Type to search..."
					onDebouncedChange={(it) => setSearch(it.target.value)}
				/>
			</SidebarHeader>
			<SidebarContent>
				<SidebarGroup className="px-0">
					<SidebarGroupContent
						ref={parentRef}
						className="h-[calc(100svh-7.5rem)]"
						style={{
							overflow: "auto",
						}}
					>
						<ul
							className="max-w-240 w-full relative"
							style={{
								height: `${virtualizer.getTotalSize()}px`,
							}}
						>
							{virtualizer.getVirtualItems().map((item) => {
								const { key, start } = item;
								const d = get(item);

								if (!d) return null;
								return (
									<Item
										key={key}
										style={{
											transform: `translateY(${start}px)`,
										}}
									>
										{/* <ItemMedia variant="icon"> */}
										{/* 	<InfoIcon /> */}
										{/* </ItemMedia> */}
										<ItemContent>
											<ItemTitle>
												{d.name ?? d.id} {key}
											</ItemTitle>
											<ItemDescription>{d.updatedAt}</ItemDescription>
										</ItemContent>
										<ItemActions>
											<Link
												to={`/chat/$id`}
												params={{ id: d.id }}
												viewTransition
											>
												Open
											</Link>
										</ItemActions>
									</Item>
								);
							})}
						</ul>
					</SidebarGroupContent>
				</SidebarGroup>
			</SidebarContent>
		</>
	);
}
