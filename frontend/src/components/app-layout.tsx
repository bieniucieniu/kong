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
	usePostApiAuthUsersLogout,
} from "@/gen/api/default/default";
import { ThemeModeChangeMenubarMenu } from "@/integration/shadcn/components/theme-toggle";
import { FieldError } from "./ui/field";

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
	const m = usePostApiAuthUsersLogout({
		mutation: {
			onSuccess: (_, __, ___, { client }) => client.clear(),
		},
	});
	return (
		<MenubarMenu>
			<MenubarTrigger className={className}>
				{q.data?.data ? (q.data.data.name ?? "unknown") : "Login"}
			</MenubarTrigger>
			<MenubarContent>
				{q.data?.data ? (
					<>
						<MenubarItem onClick={() => m.mutate()}>Logout</MenubarItem>
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
