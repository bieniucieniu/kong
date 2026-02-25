import {
	Menubar,
	MenubarContent,
	MenubarItem,
	MenubarMenu,
	MenubarTrigger,
} from "@/components/ui/menubar";
import { useSession, useSessionLogout } from "@/features/session";
import {
	getGetApiAuthDiscordLoginUrl,
	getGetApiAuthGoogleLoginUrl,
} from "@/gen/api/kong";
import { ThemeModeChangeMenubarMenu } from "@/integration/shadcn/components/theme-toggle";
import { useSignal } from "@/lib/hooks/state/signal";
import { createStoredSignal } from "@/lib/hooks/state/stored-signal";
import { AppSidebar } from "./app-sidebar";
import { FieldError } from "./ui/field";
import { SidebarProvider, SidebarTrigger } from "./ui/sidebar";

const sidebarOpen = createStoredSignal("--sidebar-open", false);
export function AppLayout({ children }: { children: React.ReactNode }) {
	const [open, setOpen] = useSignal(sidebarOpen);
	return (
		<SidebarProvider
			open={open}
			onOpenChange={setOpen}
			className="h-svh flex flex-col"
		>
			<AppSidebar />
			<Menubar>
				<SidebarTrigger />
				<SessionMenu className="ml-auto" />
				<ThemeModeChangeMenubarMenu />
			</Menubar>

			{children}
		</SidebarProvider>
	);
}

export function SessionMenu({ className }: { className?: string }) {
	const q = useSession();
	const m = useSessionLogout();
	return (
		<MenubarMenu>
			<MenubarTrigger className={className}>
				{q.session ? (q.session?.username ?? "unknown") : "Login"}
			</MenubarTrigger>
			<MenubarContent>
				{q.session ? (
					<>
						<MenubarItem onClick={() => m.logout()}>Logout</MenubarItem>
						<FieldError errors={[m.error]} />
					</>
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
