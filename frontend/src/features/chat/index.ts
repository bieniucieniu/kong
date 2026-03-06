import {
	mutationOptions,
	type QueryClient,
	useMutation,
} from "@tanstack/react-query";
import {
	type getApiAiChatIdMessagesResponseSuccess,
	getGetApiAiChatAllQueryKey,
	getGetApiAiChatIdMessagesQueryKey,
	getGetApiAiChatIdQueryKey,
	getGetApiAiChatIdQueryOptions,
	getPostApiAiChatFreeUrl,
	getPostApiAiChatIdUrl,
	useGetApiAiChatId,
	useGetApiAiChatIdMessages,
} from "@/gen/api/kong";
import type {
	ChatMessage,
	ChatPrompt,
	ChatPromptsList,
	ChatSession,
} from "@/gen/models";

export async function* fetchAiChat(id: string, chat: ChatPrompt) {
	const res = await fetch(getPostApiAiChatIdUrl(id), {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(chat),
	});
	if (res.status >= 400) throw new Error(res.statusText);
	const body = [204, 205, 304].includes(res.status) ? null : res.body;

	const contentType = res.headers.get("Content-Type");
	if (contentType?.includes("text/event-stream")) {
		const reader = body?.getReader();
		if (!reader) return;
		const decoder = new TextDecoder();
		while (true) {
			const { done, value } = await reader.read();
			if (done) break;

			const chunk = decoder.decode(value, { stream: true });

			yield chunk;
		}
	} else {
		yield await res.text();
	}
}
export async function* fetchFreeAiChat(chat: ChatPromptsList) {
	const res = await fetch(getPostApiAiChatFreeUrl(), {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(chat),
	});
	if (res.status >= 400) throw new Error(res.statusText);
	const body = [204, 205, 304].includes(res.status) ? null : res.body;

	const contentType = res.headers.get("Content-Type");
	if (contentType?.includes("text/event-stream")) {
		const reader = body?.getReader();
		if (!reader) return;
		const decoder = new TextDecoder();
		while (true) {
			const { done, value } = await reader.read();
			if (done) break;

			const chunk = decoder.decode(value, { stream: true });

			yield chunk;
		}
	} else {
		yield await res.text();
	}
}

export function getChatMutationOptions(session: ChatSession) {
	return mutationOptions({
		mutationKey: ["chat", session.id],
		mutationFn: async (p: ChatPrompt, ctx) => {
			const { data } = await ctx.client.ensureQueryData(
				getGetApiAiChatIdQueryOptions(session.id),
			);
			const a = ctx.client
				.getQueryCache()
				.find<getApiAiChatIdMessagesResponseSuccess>({
					queryKey: getGetApiAiChatIdMessagesQueryKey(session.id),
					exact: true,
				});
			const last: ChatMessage = {
				id: "mock",
				content: "",
				role: "agent",
				createdAt: new Date().toISOString(),
			};

			if (a?.isActive()) await a?.promise;

			if (a?.state.data) {
				a.state.data.data.push(
					{
						id: "mock",
						content: p.message,
						role: "user",
						createdAt: new Date().toISOString(),
					},
					last,
				);

				a.setState({
					status: "pending",
					error: null,
					fetchStatus: "fetching",
					data: { ...a.state.data },
				});
			}
			const gen =
				session.id === "free"
					? fetchFreeAiChat({
							messages: a?.state.data
								? a?.state.data.data.slice(0, -1)
								: [
										{
											id: "mock",
											content: p.message,
											role: "user",
											createdAt: new Date().toISOString(),
										},
									],
						})
					: fetchAiChat(session.id, p);

			//invalidate queries, api sets default name base on first user message if name is not set
			if (!data.name) {
				ctx.client.invalidateQueries({
					queryKey: getGetApiAiChatIdQueryKey(session.id),
				});
				ctx.client.invalidateQueries({
					queryKey: getGetApiAiChatAllQueryKey(),
				});
			}
			let acc = "";
			for await (const chunk of gen) {
				acc += chunk;
				last.content = acc;
				if (a?.state.data) a.setData(a.state.data);
			}
			return last;
		},
	});
}

export function initChatQuery(qc: QueryClient, id: string) {
	const a =
		qc.getQueryCache().find<getApiAiChatIdMessagesResponseSuccess>({
			queryKey: getGetApiAiChatIdMessagesQueryKey(id),
			exact: true,
		})?.state.data ??
		qc.setQueryData<getApiAiChatIdMessagesResponseSuccess>(
			getGetApiAiChatIdMessagesQueryKey(id),
			{
				data: [],
				status: 200,
				headers: new Headers(),
			} satisfies getApiAiChatIdMessagesResponseSuccess,
		);

	return a;
}
export const useChatQuery = useGetApiAiChatIdMessages;
export function useChatMutation(
	...args: Parameters<typeof getChatMutationOptions>
) {
	return useMutation(getChatMutationOptions(...args));
}
export function useFreeChatMutation() {
	return useMutation(getChatMutationOptions({ id: "free" }));
}
export const useChatSessionQuery = useGetApiAiChatId;
