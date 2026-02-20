import { useMemo, useRef } from "react";

export function useCollect(
	reader: ReadableStreamDefaultReader<Uint8Array<ArrayBuffer>> | undefined,
	onData: (data: string) => void,
) {
	const onDataRef = useRef(onData);
	onDataRef.current = onData;

	useMemo(() => collect(reader, onDataRef.current), [reader]);
}
export async function collect(
	reader: ReadableStreamDefaultReader<Uint8Array<ArrayBuffer>> | undefined,
	onData: (data: string) => void,
) {
	if (!reader) return;
	const decoder = new TextDecoder();
	while (true) {
		const { done, value } = await reader.read();
		if (done) break;

		const chunk = decoder.decode(value, { stream: true });

		onData(chunk);
	}
}
