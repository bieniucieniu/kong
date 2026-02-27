import { useRef } from "react";
import { useRefCallback } from "./state/callback";

export interface UseDebounceOptions {
	delay?: number;
}
export function useDebounceCallback<Args extends any[]>(
	onDebouncedChange: (...args: Args) => void,
	{ delay = 300 }: UseDebounceOptions,
): (...args: Args) => void {
	const ref = useRef<ReturnType<typeof setTimeout>>(null);

	const setInputValue = useRefCallback((...args: Args) => {
		if (ref.current) clearTimeout(ref.current);
		ref.current = setTimeout(() => onDebouncedChange(...args), delay);
	});

	return setInputValue;
}
