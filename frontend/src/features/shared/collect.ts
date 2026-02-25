import { useEffect, useRef } from "react";

export function useCollect(
	reader: ReadableStreamDefaultReader<Uint8Array<ArrayBuffer>> | undefined,
	onData: (data: string) => void,
) {
	const onDataRef = useRef(onData);
	onDataRef.current = onData;

	useEffect(() => {
		(async () => {
			for await (const chunk of collect(reader)) {
				onDataRef.current(chunk);
			}
		})();
	}, [reader]);
}
export async function* collect(
	reader: ReadableStreamDefaultReader<Uint8Array<ArrayBuffer>> | undefined,
) {
	if (!reader) return;
	const decoder = new TextDecoder();
	while (true) {
		const { done, value } = await reader.read();
		if (done) break;

		const chunk = decoder.decode(value, { stream: true });

		yield chunk;
	}
}
