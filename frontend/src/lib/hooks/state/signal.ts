import { useCallback, useRef, useState, useSyncExternalStore } from "react";

class SignalState<T> {
	private listeners: Set<() => void>;
	state: T;
	onSet: Set<(n: T) => void>;
	constructor(state: T, onSet?: (n: T) => void) {
		this.state = state;
		this.listeners = new Set();
		this.onSet = new Set();
		if (onSet) this.onSet.add(onSet);
	}
	subscribe(fn: () => void): () => boolean {
		this.listeners.add(fn);
		return () => this.listeners.delete(fn);
	}
	register(fn: () => void): () => boolean {
		this.onSet.add(fn);
		return () => this.onSet.delete(fn);
	}

	dispach(): void {
		dispatch(this.onSet, this.state);
		dispatch(this.listeners);
	}

	update<U extends T>(state: U): U {
		if (this.state !== state) {
			this.state = state;
			this.dispach();
		}
		return this.state as U;
	}
}
export type Setter<T> = <U extends T>(v: U) => U;
export type { SignalState };

export function createSignal<T>(
	state: T,
	onSet?: (n: T) => void,
): SignalState<T> {
	return new SignalState(state, onSet);
}

export function useSignal<T>(p: SignalState<T>): [state: T, setter: Setter<T>] {
	return [useSignalState(p), useCallback((v) => p.update(v), [p])];
}

export function useCreateSignal<T>(
	state: T,
	onSet?: (n: T) => void,
): SignalState<T> {
	const ref = useRef(onSet);
	ref.current = onSet;
	return useState(() => createSignal(state, (s) => ref.current?.(s)))[0];
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

function dispatch<Fn extends (...arg: any[]) => any>(
	entries: Iterable<Fn>,
	...args: Parameters<Fn>
) {
	for (const fn of entries)
		try {
			fn?.(...args);
		} catch (e) {
			console.error(e);
		}
}
