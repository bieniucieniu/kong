export function updateStored<T>(
	key: string,
	value: T,
	storage: Storage = localStorage,
): T {
	if (value == null) storage.removeItem(key);
	else storage.setItem(key, JSON.stringify(value));
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
	if (value != null)
		try {
			return JSON.parse(value);
		} catch (_) {}
	return updateStored(key, defaultValue, storage);
}
