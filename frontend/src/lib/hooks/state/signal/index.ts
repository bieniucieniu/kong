/**
 * A simple signal is a state that can be updated and listened to.
 * It is similar to a React state, but it is not tied to a component lifecycle.
 * It can be used to create a state that is shared across components.
 */
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
	listen(fn: () => void): () => boolean {
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
		return state;
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

/**
 * Effect is a function that can be used to create a side effect.
 * It will only run when any of the signals change.
 * @param signals The signals to listen to.
 * @param fn The function to run when any of the signals change.
 * @returns A function that can be used to unsubscribe from the signals.
 */
export function effect<const T extends SignalState<any>[]>(
	signals: T,
	fn: (args: { [K in keyof T]: T[K]["state"] }) => void,
	options?: { initial?: boolean },
): () => void {
	const exec = () => fn(signals.map((signal) => signal.state) as any);
	if (options?.initial) exec();
	const l = signals.map((signal) => signal.listen(exec));
	return () => l.map((unwatch) => unwatch());
}

effect([createSignal(0), createSignal("")], () => {});
