"use client";

import { Button } from "@base-ui/react";
import { CaretDownIcon, UserIcon } from "@phosphor-icons/react";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import {
	DropdownMenu,
	DropdownMenuContent,
	DropdownMenuItem,
	DropdownMenuLabel,
	DropdownMenuSeparator,
	DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { SidebarMenuButton, useSidebar } from "@/components/ui/sidebar";
import { useSession, useSessionLogout } from "@/features/session";
import {
	getGetApiAuthDiscordLoginUrl,
	getGetApiAuthGoogleLoginUrl,
} from "@/gen/api/kong";
import { cn } from "@/lib/utils";
import { Spinner } from "../ui/spinner";

function getInitials(name: string | undefined) {
	if (!name) return "??";
	const a = name.split(" ").slice(0, 2);
	if (a.length === 1) return a[0].slice(0, 2).toLocaleUpperCase();
	return a
		.map((n) => n[0])
		.join("")
		.toUpperCase();
}

export function SidebarUser({ className }: { className?: string }) {
	const { isMobile } = useSidebar();
	const { session: user, isLoading } = useSession();

	const badge = (
		<>
			<Avatar className="h-8 w-8">
				{user?.avatar && (
					<AvatarImage src={user?.avatar} alt={user?.username} />
				)}
				<AvatarFallback className="rounded-lg">
					{getInitials(user?.username)}
				</AvatarFallback>
			</Avatar>
			<div className="grid flex-1 text-left text-sm leading-tight">
				<span className="truncate font-medium">
					{user?.username ?? user?.id}
				</span>
			</div>
		</>
	);

	const m = useSessionLogout();
	return isLoading ? (
		<Spinner className={className} />
	) : (
		<DropdownMenu>
			<DropdownMenuTrigger
				render={
					<SidebarMenuButton
						size="lg"
						className={cn(
							"data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground md:h-8 md:p-0",
							className,
						)}
					>
						{user ? (
							<>
								{badge}
								<CaretDownIcon className="ml-auto size-4" />
							</>
						) : (
							<UserIcon className="size-4 m-auto" alt="login" />
						)}
					</SidebarMenuButton>
				}
			/>
			<DropdownMenuContent
				className="min-w-56 rounded-lg"
				side={isMobile ? "bottom" : "right"}
				align="end"
				sideOffset={4}
			>
				{user ? (
					<>
						<DropdownMenuLabel className="p-0 font-normal">
							<div className="flex items-center gap-2 px-1 py-1.5 text-left text-sm">
								{badge}
							</div>
						</DropdownMenuLabel>
						<DropdownMenuSeparator />
						<DropdownMenuItem
							render={
								<Button
									onClick={(e) => {
										e.preventDefault();
										m.logout();
									}}
								>
									Log out
								</Button>
							}
						/>
					</>
				) : (
					<>
						<DropdownMenuSeparator />
						<DropdownMenuItem
							render={
								<a href={getGetApiAuthGoogleLoginUrl()}>login via Google</a>
							}
						/>
						<DropdownMenuItem
							render={
								<a href={getGetApiAuthDiscordLoginUrl()}>login via Discord</a>
							}
						/>
					</>
				)}
			</DropdownMenuContent>
		</DropdownMenu>
	);
}
