import type { ChangeEventHandler } from "react";
import { useDebounceCallback } from "@/lib/hooks/debounce";
import { useRefCallback } from "@/lib/hooks/state/callback";
import { cn } from "@/lib/utils";
import { Input } from "./ui/input";

export interface DebouncedSearchInputProps
	extends React.ComponentProps<"input"> {
	type?: never;
	onDebouncedChange: ChangeEventHandler<HTMLInputElement, HTMLInputElement>;
	debounceDelay?: number;
}

export default function DebouncedInput({
	onDebouncedChange,
	debounceDelay: delay = 300,
	value,
	onChange,
	...props
}: DebouncedSearchInputProps) {
	onDebouncedChange = useDebounceCallback(onDebouncedChange, { delay });
	const onChangeHandler = useRefCallback(
		(event: React.ChangeEvent<HTMLInputElement>) => {
			onDebouncedChange(event);
			onChange?.(event);
		},
	);

	return <Input {...props} onChange={onChangeHandler} defaultValue={value} />;
}

export function DebouncedSidebarInput({
	className,
	...props
}: DebouncedSearchInputProps) {
	return (
		<DebouncedInput
			data-slot="sidebar-input"
			data-sidebar="input"
			className={cn("bg-background h-8 w-full shadow-none", className)}
			{...props}
		/>
	);
}
