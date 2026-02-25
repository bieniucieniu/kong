import { createSignal, type SignalState } from "./signal";
import { getStored, updateStored } from "./storage";

export function createStoredSignal<T>(
	key: string,
	state?: T,
	storage?: Storage,
): SignalState<T | undefined>;
export function createStoredSignal<T>(
	key: string,
	state: T,
	storage?: Storage,
): SignalState<T>;
export function createStoredSignal<T>(
	key: string,
	state: T,
	storage: Storage = localStorage,
): SignalState<any> {
	return createSignal(getStored(key, state), (s) =>
		updateStored(key, s, storage),
	);
}
