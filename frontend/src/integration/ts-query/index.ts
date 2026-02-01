import { keepPreviousData, QueryClient } from "@tanstack/react-query";

export const queryClient = new QueryClient({
	defaultOptions: {
		queries: {
			staleTime: 60_000,
			retry: import.meta.env.DEV ? 0 : 3,
			placeholderData: keepPreviousData,
		},
	},
});
