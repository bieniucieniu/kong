import { useCallback, useRef } from "react";

export function useUpdateCallback<Fn extends (...args: any) => any>(
	fn: Fn,
	deps: React.DependencyList = [],
): Fn {
	const ref = useRef<Fn>(fn);
	ref.current = fn;

	return useCallback(
		(...args: Parameters<Fn>) => ref.current(...args),
		// biome-ignore lint/correctness/useExhaustiveDependencies: This is a custom hook
		deps,
	) as Fn;
}
