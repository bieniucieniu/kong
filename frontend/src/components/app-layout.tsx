import {
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
import { useIsMobile } from "@/lib/hooks/use-mobile";
import { AppSidebar } from "./app-sidebar";
import { FieldError } from "./ui/field";
import { SidebarInset, SidebarProvider, SidebarTrigger } from "./ui/sidebar";

//const sidebarOpen = createStoredSignal("--sidebar-open", false);
export function AppLayout({ children }: { children: React.ReactNode }) {
	return (
		<SidebarProvider
			open
			style={
				{
					"--sidebar-width": "350px",
				} as React.CSSProperties
			}
		>
			<AppSidebar />
			<SidebarInset>
				{useIsMobile() && (
					<SidebarTrigger className="absolute top-0 left-0 z-10" />
				)}
				{children}
			</SidebarInset>
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
