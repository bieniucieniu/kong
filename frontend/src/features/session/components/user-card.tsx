import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Spinner } from "@/components/ui/spinner";
import { useSession, useSessionLogout } from "@/features/session";
import {
	getGetApiAuthDiscordLoginUrl,
	getGetApiAuthGoogleLoginUrl,
} from "@/gen/api/kong";
import { cn } from "@/lib/utils";

function getInitials(name: string | undefined) {
	if (!name) return "??";
	const a = name.split(" ").slice(0, 2);
	if (a.length === 1) return a[0].slice(0, 2).toLocaleUpperCase();
	return a
		.map((n) => n[0])
		.join("")
		.toUpperCase();
}

export function UserCard({ className }: { className?: string }) {
	const { session: user, isLoading } = useSession();

	const m = useSessionLogout();

	if (isLoading)
		return (
			<div className={cn("flex items-center justify-center", className)}>
				<Spinner />
			</div>
		);
	return (
		<Card className={className}>
			<CardHeader>
				{user && (
					<>
						<Avatar className="h-8 w-8">
							{user.avatar && (
								<AvatarImage src={user.avatar} alt={user.username} />
							)}
							<AvatarFallback className="rounded-lg">
								{getInitials(user.username)}
							</AvatarFallback>
						</Avatar>
						<div className="grid flex-1 text-left text-sm leading-tight">
							<span className="truncate font-medium">
								{user.username ?? user.id}
							</span>
						</div>
					</>
				)}
			</CardHeader>
			<CardContent className="min-w-56 rounded-lg">
				{user ? (
					<Button
						onClick={(e) => {
							e.preventDefault();
							m.logout();
						}}
					>
						Log out
					</Button>
				) : (
					<>
						<Button
							render={
								<a href={getGetApiAuthGoogleLoginUrl()}>login via Google</a>
							}
						/>
						<Button
							className="bg-[#5865f2] text-white [a]:hover:bg-[#5865f2]/80"
							render={
								<a href={getGetApiAuthDiscordLoginUrl()}>login via Discord</a>
							}
						/>
					</>
				)}
			</CardContent>
		</Card>
	);
}
