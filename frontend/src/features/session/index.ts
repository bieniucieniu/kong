import { useNavigate } from "@tanstack/react-router";
import { toast } from "sonner";
import {
	useGetApiAuthUsersSession,
	usePostApiAuthUsersLogout,
} from "@/gen/api/kong";

export function useSession() {
	const q = useGetApiAuthUsersSession({
		query: {
			placeholderData: undefined,
		},
	});

	return {
		session: q.data?.data,
		isLoading: q.isLoading,
		error: q.error,
	};
}

export function useSessionLogout() {
	const n = useNavigate();
	const m = usePostApiAuthUsersLogout({
		mutation: {
			onSuccess: (_, __, ___, { client }) => {
				client.clear();
				localStorage.clear();
				n({ to: "/", reloadDocument: true });
			},
			onError: (e) => {
				toast.error(e.message);
			},
		},
	});

	return {
		logout: () => m.mutate(),
		isLoading: m.isPending,
		error: m.error,
	};
}
