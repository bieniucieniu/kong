import { useInfiniteQuery } from "@tanstack/react-query";
import { useRef, useState } from "react";
import type { NavEntry } from "@/components/app-sidebar/data";
import {
	SidebarContent,
	SidebarGroup,
	SidebarGroupContent,
	SidebarHeader,
	SidebarInput,
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
	const [search, setSearch] = useState<string | undefined>();
	const parentRef = useRef<HTMLDivElement>(null);
	const q = useGetApiAiSessionsPaged();
	const { items, get, virtualizer } = useInfiniteQueryVirtualizer(q, {
		joinFn: (d) => d?.pages.flatMap((p) => p.data) || [],
		getScrollElement: () => parentRef.current,
	});

	return (
		<>
			<SidebarHeader className="gap-3.5 border-b p-4">
				<div className="flex w-full items-center justify-between">
					<div className="text-foreground text-base font-medium">{title}</div>
				</div>
				<SidebarInput value={search} placeholder="Type to search..." />
			</SidebarHeader>
			<SidebarContent>
				<SidebarGroup className="px-0">
					<SidebarGroupContent>
						<div
							ref={(it) => {
								parentRef.current = it;
							}}
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
								{items.map((item) => {
									const { key, start } = item;
									const d = get(item);
									if (!d) return null;
									return (
										<li
											key={key}
											className="absolute top-0 w-[90%]"
											style={{
												transform: `translateY(${start}px)`,
											}}
										>
											{d.name ?? d.id}
										</li>
									);
								})}
							</ul>
						</div>
					</SidebarGroupContent>
				</SidebarGroup>
			</SidebarContent>
		</>
	);
}
