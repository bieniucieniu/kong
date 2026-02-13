import { createSignal, type SignalState } from "./signal";
import { getStored, updateStored } from "./storage";

export function createStoredSignal<T>(
	key: string,
	state?: T,
	onSet?: (n: T) => void,
): SignalState<T | undefined>;
export function createStoredSignal<T>(
	key: string,
	state: T,
	onSet?: (n: T) => void,
): SignalState<T>;
export function createStoredSignal<T>(
	key: string,
	state: T,
	onSet?: (n: T) => void,
): SignalState<any> {
	return createSignal(getStored(key, state), (s) => {
		updateStored(key, s);
		onSet?.(s);
	});
}
