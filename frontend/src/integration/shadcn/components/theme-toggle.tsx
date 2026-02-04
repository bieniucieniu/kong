import { Check, Computer, type LucideProps, Moon, Sun } from "lucide-react";
import { createElement } from "react";
import {
	DropdownMenuItem,
	DropdownMenuSub,
	DropdownMenuSubContent,
	DropdownMenuSubTrigger,
} from "@/components/ui/dropdown-menu";
import {
	MenubarContent,
	MenubarMenu,
	MenubarRadioGroup,
	MenubarRadioItem,
	MenubarTrigger,
} from "@/components/ui/menubar";
import {
	Select,
	SelectContent,
	SelectGroup,
	SelectItem,
	SelectTrigger,
	SelectValue,
} from "@/components/ui/select";
import { cn } from "@/lib/utils";
import { type Theme, themes, useTheme } from "../theme";

const themeIcons: Record<Theme, (props: LucideProps) => React.ReactNode> = {
	dark: Moon,
	light: Sun,
	system: Computer,
};

function ThemeIcon(props: { className?: string }) {
	const [theme] = useTheme();
	return themes.map((it, i) => {
		const Icon = themeIcons[it];
		const className = i
			? cn(
					`absolute top-0 left-0 h-[1.2rem] w-[1.2rem] scale-0 rotate-90 transition-all`,
					{
						"scale-100 rotate-0": theme === it,
					},
				)
			: cn(`h-[1.2rem] w-[1.2rem] scale-100 rotate-0 transition-all`, {
					"scale-0 -rotate-90": theme !== it,
				});
		return (
			<Icon
				data-theme={theme}
				className={cn(className, props.className)}
				key={it}
			/>
		);
	});
}

export function ThemeModeToggleDropdown(props: {
	className?: string;
	children?: React.ReactNode | ((props: { theme: Theme }) => React.ReactNode);
}) {
	const [theme, setTheme] = useTheme();

	return (
		<Select value={theme} onValueChange={setTheme}>
			<SelectTrigger className={props.className}>
				<SelectValue>
					<div className="relative flex gap-2">
						<ThemeIcon />
						{typeof props.children === "function"
							? createElement(props.children, { theme })
							: props.children}
						<span className="sr-only">Toggle theme</span>
					</div>
				</SelectValue>
			</SelectTrigger>
			<SelectContent>
				<SelectGroup>
					{themes.map((it) => {
						const Icon = themeIcons[it];
						return (
							<SelectItem value={it} key={it} onClick={() => setTheme(it)}>
								<Icon /> {`theme.${it}`}
							</SelectItem>
						);
					})}
				</SelectGroup>
			</SelectContent>
		</Select>
	);
}
export function ThemeModeToggleDropdownSubItem(props: {
	className?: string;
	children?: React.ReactNode | ((theme: Theme) => React.ReactNode);
}) {
	const [theme, setTheme] = useTheme();
	return (
		<DropdownMenuSub>
			<DropdownMenuSubTrigger className={props.className}>
				<div className="flex gap-2 items-center">
					<div className="relative">
						<ThemeIcon className="h-3.5 w-3.5" />
					</div>
				</div>
			</DropdownMenuSubTrigger>
			<DropdownMenuSubContent className="min-w-30">
				{themes.map((it) => {
					const Icon = themeIcons[it];
					return (
						<DropdownMenuItem
							key={it}
							onSelect={() => setTheme(it)}
							className="cursor-pointer flex gap-3"
						>
							<Icon className="h-3.5 w-3.5" />
							<span>{`theme.${it}`}</span>
							<div className="flex-1" />
							<Check
								className={cn("opacity-0", {
									"opacity-100": theme === it,
								})}
							/>
						</DropdownMenuItem>
					);
				})}
			</DropdownMenuSubContent>
		</DropdownMenuSub>
	);
}

export function ThemeModeChangeMenubarMenu({
	className,
}: {
	className?: string;
}) {
	"use no memo";
	const [theme, setTheme] = useTheme();
	return (
		<MenubarMenu>
			<MenubarTrigger
				className={cn("min-w-26 flex justify-between", className)}
			>
				<span>Theme:</span> <span>{theme}</span>
			</MenubarTrigger>
			<MenubarContent className="min-w-24">
				<MenubarRadioGroup
					className="*:justify-end"
					value={theme}
					onValueChange={setTheme}
				>
					{themes.map((it) => (
						<MenubarRadioItem key={it} value={it}>
							{it}
						</MenubarRadioItem>
					))}
				</MenubarRadioGroup>
			</MenubarContent>
		</MenubarMenu>
	);
}
