import {
	useGetApiAuthUsersSession,
	usePostApiAuthUsersLogout,
} from "@/gen/api/kong";

export function useSession() {
	const q = useGetApiAuthUsersSession();

	return {
		session: q.data?.data,
		isLoading: q.isLoading,
		error: q.error,
	};
}

export function useSessionLogout() {
	const m = usePostApiAuthUsersLogout({
		mutation: {
			onSuccess: (_, __, ___, { client }) => client.clear(),
		},
	});

	return {
		logout: () => m.mutate(),
		isLoading: m.isPending,
		error: m.error,
	};
}
