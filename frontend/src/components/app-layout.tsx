import {
	Menubar,
	MenubarContent,
	MenubarItem,
	MenubarMenu,
	MenubarTrigger,
} from "@/components/ui/menubar";
import { ThemeModeChangeMenubarMenu } from "@/integration/shadcn/components/theme-toggle";

export function AppLayout({ children }: { children: React.ReactNode }) {
	return (
		<div className="h-svh flex flex-col">
			<nav className="w-svw flex items-end">
				<Menubar className="ml-auto">
					<MenubarMenu>
						<MenubarTrigger>Login</MenubarTrigger>
						<MenubarContent>
							{/* <MenubarItem */}
							{/* 	render={<a href="/api/google/login">login via Google</a>} */}
							{/* /> */}
							<MenubarItem
								render={<a href="/api/discord/login">login via Discord</a>}
							/>
						</MenubarContent>
					</MenubarMenu>
					<ThemeModeChangeMenubarMenu />
				</Menubar>
			</nav>
			<div className="flex-1">{children}</div>
		</div>
	);
}
