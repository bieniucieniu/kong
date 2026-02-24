export function tryParseJson<S>(str: string | null | undefined): S | undefined {
	try {
		if (str == null) return undefined;
		return JSON.parse(str);
	} catch (_) {
		return undefined;
	}
}
export function tryStringifyJson<S>(str: S): string | undefined {
	try {
		return JSON.stringify(str);
	} catch (_) {
		return undefined;
	}
}

export function updateStored<T>(
	key: string,
	value: T,
	storage: Storage = localStorage,
): T {
	const str = value == null ? null : tryStringifyJson(value);
	if (str == null) storage.removeItem(key);
	else storage.setItem(key, str);
	return value;
}
export function getStored<T>(
	gkey: string,
	defaultValue: T,
	storage?: Storage,
): T;
export function getStored<T>(
	key: string,
	defaultValue?: undefined,
	storage?: Storage,
): T | undefined;
export function getStored<T>(
	key: string,
	defaultValue: T | undefined,
	storage: Storage = localStorage,
): any {
	const value = storage.getItem(key);
	return tryParseJson(value) ?? updateStored(key, defaultValue, storage);
}
