import {
	Menubar,
	MenubarContent,
	MenubarItem,
	MenubarMenu,
	MenubarTrigger,
} from "@/components/ui/menubar";
import {
	getGetApiAuthDiscordLoginUrl,
	getGetApiAuthGoogleLoginUrl,
	useGetApiAuthUsersSession,
} from "@/gen/api/default/default";
import { ThemeModeChangeMenubarMenu } from "@/integration/shadcn/components/theme-toggle";

export function AppLayout({ children }: { children: React.ReactNode }) {
	return (
		<div className="h-svh flex flex-col">
			<nav className="w-svw flex items-end">
				<Menubar className="ml-auto">
					<SessionMenu />
					<ThemeModeChangeMenubarMenu />
				</Menubar>
			</nav>
			<div className="flex-1">{children}</div>
		</div>
	);
}

export function SessionMenu({ className }: { className?: string }) {
	const q = useGetApiAuthUsersSession();
	return (
		<MenubarMenu>
			<MenubarTrigger className={className}>
				{q.data?.data?.name ?? "Login"}
			</MenubarTrigger>
			<MenubarContent>
				{q.data?.data ? (
					<MenubarItem>Logout</MenubarItem>
				) : (
					<>
						<MenubarItem
							render={
								<a href={getGetApiAuthGoogleLoginUrl()}>login via Google</a>
							}
						/>
						<MenubarItem
							render={
								<a href={getGetApiAuthDiscordLoginUrl()}>login via Discord</a>
							}
						/>
					</>
				)}
			</MenubarContent>
		</MenubarMenu>
	);
}
