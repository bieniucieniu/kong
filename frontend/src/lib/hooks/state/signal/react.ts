import { useCallback, useMemo, useRef, useSyncExternalStore } from "react";
import { createSignal, type Setter, type SignalState } from ".";
export function useSignal<T>(p: SignalState<T>): [state: T, setter: Setter<T>] {
	return [useSignalState(p), useCallback((v) => p.update(v), [p])];
}

export function useCreateSignal<T>(
	state: T,
	onSet?: (n: T) => void,
	deps: React.DependencyList = [],
): SignalState<T> {
	const ref = useRef(onSet);
	ref.current = onSet;
	return useMemo(() => createSignal(state, (s) => ref.current?.(s)), deps);
}

export function useSignalState<T, U>(
	o: SignalState<T>,
	selector: (v: T) => U,
): U;
export function useSignalState<T>(o: SignalState<T>): T;
export function useSignalState(
	o: SignalState<any>,
	selector?: (v: any) => any,
): any {
	return useSyncExternalStore(
		(fn) => o.subscribe(fn),
		() => (typeof selector === "function" ? selector(o.state) : o.state),
		() => (typeof selector === "function" ? selector(o.state) : o.state),
	);
}
