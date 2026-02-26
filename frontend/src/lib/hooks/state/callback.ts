import { useCallback, useRef } from "react";

/**
 * useful for components wraped in [memo] that takes a callback but don't
 * want to re-render when the callback changes
 * @param fn the callback to memoize
 * @param deps the dependencies of the callback if should cause a re-render
 * @returns a memoized version of the callback
 */
export function useRefCallback<T extends (...args: any[]) => any>(
	fn: T,
	deps: React.DependencyList = [],
): T {
	const ref = useRef<T>(fn);
	ref.current = fn;
	return useCallback(((...args) => ref.current(args)) as T, deps);
}
