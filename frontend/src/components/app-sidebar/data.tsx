import { ListIcon } from "@phosphor-icons/react/dist/ssr";
import type { LinkProps } from "@tanstack/react-router";
import {
	SidebarContent,
	SidebarGroup,
	SidebarGroupContent,
	SidebarHeader,
	SidebarInput,
} from "../ui/sidebar";

export type NavEntry = {
	id: string;
	title: string;
	mobileLink: LinkProps["to"];
	icon: React.ComponentType<React.SVGProps<SVGSVGElement>>;
	content: (entry: NavEntry) => React.ReactNode;
};

export const data: NavEntry[] = [
	{
		id: "sessions",
		title: "sessions",
		icon: ListIcon,
		content: (entry) => {
			return (
				<>
					<SidebarHeader className="gap-3.5 border-b p-4">
						<div className="flex w-full items-center justify-between">
							<div className="text-foreground text-base font-medium">
								{entry.title}
							</div>
						</div>
						<SidebarInput placeholder="Type to search..." />
					</SidebarHeader>
					<SidebarContent>
						<SidebarGroup className="px-0">
							<SidebarGroupContent></SidebarGroupContent>
						</SidebarGroup>
					</SidebarContent>
				</>
			);
		},
		mobileLink: "/sessions",
	},
];
