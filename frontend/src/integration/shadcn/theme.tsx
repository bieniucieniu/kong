import { useSyncExternalStore } from "react";

// inline impl
function tryParseJson<S>(str: string): S | undefined {
	try {
		return JSON.parse(str);
	} catch (_) {
		return undefined;
	}
}
function tryStringifyJson<S>(str: S): string | undefined {
	try {
		return JSON.stringify(str);
	} catch (_) {
		return undefined;
	}
}

export function getStoredState<S>(
	key: string,
	storage: Storage = localStorage,
): S | undefined {
	const str = storage.getItem(key);
	if (str == null) return undefined;
	const v = tryParseJson<S>(str);
	if (v != null) return v;
	return undefined;
}
export function updateStoredState<T>(
	key: string,
	state: T,
	storage: Storage = localStorage,
) {
	const v = tryStringifyJson(state);
	if (v != null) storage.setItem(key, v);
	else storage.removeItem(key);
}
export type Theme = "dark" | "light" | "system";
export const themes = ["dark", "light", "system"] as const satisfies Theme[];

const STORAGE_KEY = "--ui-theme";
export const DEFAULT_THEME: Theme = "system";

export const syncClientTheme = (theme: Theme) => {
	const root = window.document.documentElement;

	root.classList.remove("light", "dark");

	if (theme === "system") {
		theme = window.matchMedia("(prefers-color-scheme: dark)").matches
			? "dark"
			: "light";
	}

	root.classList.add(theme);
};

let theme_state = getStoredState<Theme>(STORAGE_KEY) ?? DEFAULT_THEME;
syncClientTheme(theme_state);
const listeners = new Set<() => void>();
const subscribe = (listener: () => void) => {
	listeners.add(listener);
	return () => void listeners.delete(listener);
};
const setTheme = (theme: Theme | null | undefined) => {
	theme ??= DEFAULT_THEME;
	theme_state = theme;
	updateStoredState(STORAGE_KEY, theme);
	syncClientTheme(theme);
	for (const listener of listeners) {
		listener();
	}
};

export const useTheme = () => {
	"use no memo";
	const s = useSyncExternalStore(subscribe, () => theme_state);
	return [s, setTheme] as const;
};
