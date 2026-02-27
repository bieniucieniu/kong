import { createSignal } from "@/lib/hooks/state/signal";
import { useSignalState } from "@/lib/hooks/state/signal/react";

const MOBILE_BREAKPOINT = 768;

const isMobileSignal = createSignal<boolean | undefined>(undefined);

if (typeof window !== "undefined") {
	const mql = window.matchMedia(`(max-width: ${MOBILE_BREAKPOINT - 1}px)`);
	const onChange = () => {
		isMobileSignal.update(window.innerWidth < MOBILE_BREAKPOINT);
	};
	mql.addEventListener("change", onChange);
	onChange();
}

export function useIsMobile() {
	return !!useSignalState(isMobileSignal);
}
