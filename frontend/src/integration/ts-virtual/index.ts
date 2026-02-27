import type {
	InfiniteData,
	UseInfiniteQueryResult,
} from "@tanstack/react-query";
import {
	useVirtualizer,
	type VirtualItem,
	type Virtualizer,
} from "@tanstack/react-virtual";
import { useEffect, useMemo } from "react";
import { useRefCallback } from "@/lib/hooks/state/callback";

type UseInfiniteQueryVirtualizerResult<T, V extends Virtualizer<any, any>> = {
	joined: T[];
	get: (item: VirtualItem) => T | undefined;
	virtualizer: V;
};

export function useInfiniteQueryVirtualizer<
	Q extends UseInfiniteQueryResult<InfiniteData<any>>,
	T,
	TScrollElement extends Element,
	TItemElement extends Element,
>(
	q: Q,
	options: {
		joinFn: (d: Q["data"]) => T[];
		getScrollElement: () => TScrollElement | null;
		estimateSize?: () => number;
	},
): UseInfiniteQueryVirtualizerResult<
	T,
	Virtualizer<TScrollElement, TItemElement>
> {
	const { getScrollElement, estimateSize = () => 16, joinFn } = options;
	const joined = useMemo(() => joinFn(q.data), [q.data?.pages]);
	const v = useVirtualizer<TScrollElement, TItemElement>({
		count: joined.length,
		getScrollElement,
		estimateSize,
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

	const get = useRefCallback((item: VirtualItem) => {
		return joined[item.index];
	});

	return {
		get,
		joined,
		virtualizer: v,
	};
}
