import { useSyncExternalStore } from "react";
import { getStored, updateStored } from "@/lib/hooks/state/storage";

// inline impl
// export function tryParseJson<S>(str: string | null | undefined): S | undefined {
// 	try {
// 		if (str == null) return undefined;
// 		return JSON.parse(str);
// 	} catch (_) {
// 		return undefined;
// 	}
// }
// export function tryStringifyJson<S>(str: S): string | undefined {
// 	try {
// 		return JSON.stringify(str);
// 	} catch (_) {
// 		return undefined;
// 	}
// }
//
// export function updateStored<T>(
// 	key: string,
// 	value: T,
// 	storage: Storage = localStorage,
// ): T {
// 	const str = value == null ? null : tryStringifyJson(value);
// 	if (str == null) storage.removeItem(key);
// 	else storage.setItem(key, str);
// 	return value;
// }
// export function getStored<T>(
// 	gkey: string,
// 	defaultValue: T,
// 	storage?: Storage,
// ): T;
// export function getStored<T>(
// 	key: string,
// 	defaultValue?: undefined,
// 	storage?: Storage,
// ): T | undefined;
// export function getStored<T>(
// 	key: string,
// 	defaultValue: T | undefined,
// 	storage: Storage = localStorage,
// ): any {
// 	const value = storage.getItem(key);
// 	return tryParseJson(value) ?? updateStored(key, defaultValue, storage);
// }

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

function createThemeState(
	key: string = STORAGE_KEY,
	defaultState: Theme = DEFAULT_THEME,
	storage: Storage = localStorage,
) {
	let theme_state = getStored<Theme>(key, defaultState, storage);
	syncClientTheme(theme_state);
	const listeners = new Set<() => void>();
	const subscribe = (listener: () => void) => {
		listeners.add(listener);
		return () => void listeners.delete(listener);
	};
	const setTheme = (theme: Theme | null | undefined) => {
		theme ??= defaultState;
		theme_state = theme;
		updateStored(key, theme, storage);
		syncClientTheme(theme);
		for (const listener of listeners) {
			listener();
		}
	};

	return {
		subscribe,
		setTheme,
		getTheme: () => theme_state,
	};
}

const { subscribe, setTheme, getTheme } = createThemeState();

export const useTheme = () => {
	"use no memo";
	const s = useSyncExternalStore(subscribe, getTheme);
	return [s, setTheme] as const;
};
