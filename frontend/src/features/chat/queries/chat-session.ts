import { useInfiniteQuery } from "@tanstack/react-query";
import { getApiAiChatAll, getGetApiAiChatAllQueryKey } from "@/gen/api/kong";

export function useGetApiAiSessionsPaged(search?: string) {
	return useInfiniteQuery({
		queryKey: getGetApiAiChatAllQueryKey(search ? { search } : undefined),
		queryFn: async ({ pageParam }) => {
			const { data } = await getApiAiChatAll(pageParam);
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
