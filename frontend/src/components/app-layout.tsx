import { Menubar } from "@/components/ui/menubar";
import { ThemeModeChangeMenubarMenu } from "@/integration/shadcn/components/theme-toggle";

export function AppLayout({ children }: { children: React.ReactNode }) {
	return (
		<div>
			<nav className="w-svw fixed flex items-end">
				<Menubar className="ml-auto">
					<ThemeModeChangeMenubarMenu />
				</Menubar>
			</nav>
			{children}
		</div>
	);
}
