import { Menubar } from "@/components/ui/menubar";
import { ThemeModeChangeMenubarMenu } from "@/integration/shadcn/components/theme-toggle";

export function AppLayout({ children }: { children: React.ReactNode }) {
	return (
		<div className="h-svh flex flex-col">
			<nav className="w-svw flex items-end">
				<Menubar className="ml-auto">
					<ThemeModeChangeMenubarMenu />
				</Menubar>
			</nav>
			<div className="flex-1">{children}</div>
		</div>
	);
}
