import {
	getPostApiAiChatIdWithJsonUrl,
	getPostApiAiChatWithJsonUrl,
} from "@/gen/api/default/default";
import type { ChatPrompt, ChatPromptsList } from "@/gen/models";
import { collect } from "../shared/collect";

export async function fetchAiChat(
	id: string,
	chat: ChatPrompt,
	onCollect: (d: string) => void,
) {
	const res = await fetch(getPostApiAiChatIdWithJsonUrl(id), {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(chat),
	});
	if (res.status >= 400) throw new Error(res.statusText);
	const body = [204, 205, 304].includes(res.status) ? null : res.body;

	const contentType = res.headers.get("Content-Type");
	if (contentType?.includes("text/event-stream")) {
		const reader = body?.getReader();
		await collect(reader, onCollect);
	} else {
		onCollect(await res.text());
	}
}
export async function fetchAiChatFree(
	chat: ChatPromptsList,
	onCollect: (d: string) => void,
) {
	const res = await fetch(getPostApiAiChatWithJsonUrl(), {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(chat),
	});
	if (res.status >= 400) throw new Error(res.statusText);
	const body = [204, 205, 304].includes(res.status) ? null : res.body;

	const contentType = res.headers.get("Content-Type");
	if (contentType?.includes("text/event-stream")) {
		const reader = body?.getReader();
		await collect(reader, onCollect);
	} else {
		onCollect(await res.text());
	}
}
