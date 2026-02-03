import { useState, useSyncExternalStore } from "react";

class SignalState<T> {
	private listeners: Set<() => void>;
	state: T;
	constructor(state: T) {
		this.state = state;
		this.listeners = new Set();
	}
	subscribe(fn: () => void): () => boolean {
		this.listeners.add(fn);
		return () => this.listeners.delete(fn);
	}
	dispach(): void {
		dispatch(this.listeners);
	}

	update<U extends T>(state: U) {
		if (this.state !== state) {
			this.state = state;
			this.dispach();
		}
	}
}
export type Setter<T> = <U extends T>(v: U) => void;
export type { SignalState };

export function createSignal<T>(state: T): SignalState<T> {
	return new SignalState(state);
}

export function useSignal<T>(p: SignalState<T>): [state: T, setter: Setter<T>] {
	return [useSignalState(p), p.update];
}

export function useCreateSignal<T>(state: T): SignalState<T> {
	return useState(() => createSignal(state))[0];
}

export function useSignalState<T>(o: SignalState<T>): T {
	return useSyncExternalStore(
		(fn) => o.subscribe(fn),
		() => o.state,
		() => o.state,
	);
}

function dispatch<Fn extends (...arg: any[]) => any>(
	entries: Iterable<Fn>,
	...args: Parameters<Fn>
) {
	for (const fn of entries) fn?.(...args);
}
