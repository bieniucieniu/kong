import {
	CheckIcon,
	ComputerTowerIcon,
	type IconProps,
	MoonIcon,
	SunIcon,
} from "@phosphor-icons/react";
import { CaretDownIcon } from "@phosphor-icons/react/dist/ssr";
import {
	DropdownMenu,
	DropdownMenuContent,
	DropdownMenuItem,
	DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
	MenubarContent,
	MenubarMenu,
	MenubarRadioGroup,
	MenubarRadioItem,
	MenubarTrigger,
} from "@/components/ui/menubar";
import { SidebarMenuButton, useSidebar } from "@/components/ui/sidebar";
import { cn } from "@/lib/utils";
import { type Theme, themes, useTheme } from "../theme";

const themeIcons: Record<Theme, (props: IconProps) => React.ReactNode> = {
	dark: MoonIcon,
	light: SunIcon,
	system: ComputerTowerIcon,
};

function ThemeIcon({ className }: { className?: string }) {
	const [theme] = useTheme();
	return themes.map((it, i) => {
		const Icon = themeIcons[it];
		const localClassName = i
			? cn(`absolute top-0 left-0 scale-0 rotate-90 transition-all`, {
					"scale-100 rotate-0": theme === it,
				})
			: cn(`scale-100 rotate-0 transition-all`, {
					"scale-0 -rotate-90": theme !== it,
				});
		return (
			<Icon
				data-theme={theme}
				className={cn(localClassName, className)}
				key={it}
			/>
		);
	});
}

export function SidebarThemeModeToggle({ className }: { className?: string }) {
	const [theme, setTheme] = useTheme();
	const { isMobile } = useSidebar();
	return (
		<DropdownMenu>
			<DropdownMenuTrigger
				className={className}
				render={
					<SidebarMenuButton
						size="lg"
						className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground md:h-8 md:p-0"
					>
						<div className="relative flex ml-1.5">
							<ThemeIcon className="w-5! h-5!" />
						</div>
						<div className="grid flex-1 text-left text-sm leading-tight">
							<span className="truncate font-medium">{theme}</span>
						</div>

						<CaretDownIcon className="ml-auto size-4" />
						{/* <div className="h-8 w-8 relative bg-red-500"> */}
						{/* 	<ThemeIcon className="h-8 w-8" /> */}
						{/* </div> */}
						{/* <div className="grid flex-1 text-left text-sm leading-tight"> */}
						{/* 	<span className="truncate font-medium">theme</span> */}
						{/* </div> */}
					</SidebarMenuButton>
				}
			/>

			<DropdownMenuContent
				className="min-w-56 rounded-lg"
				side={isMobile ? "bottom" : "right"}
				align="end"
				sideOffset={4}
			>
				{themes.map((it) => {
					const Icon = themeIcons[it];
					return (
						<DropdownMenuItem
							key={it}
							onClick={(e) => {
								e.preventDefault();
								setTheme(it);
							}}
							className="cursor-pointer flex gap-3"
						>
							<Icon className="h-3.5 w-3.5" />
							<span>{it}</span>
							<CheckIcon
								className={cn("opacity-0", {
									"opacity-100": theme === it,
								})}
							/>
						</DropdownMenuItem>
					);
				})}
			</DropdownMenuContent>
		</DropdownMenu>
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
