import { ListIcon } from "@phosphor-icons/react/dist/ssr";
import type { LinkProps } from "@tanstack/react-router";
import { SidebarSessionList } from "@/features/chat/components/sidebar/session-list";

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
		content: SidebarSessionList,
		mobileLink: "/sessions",
	},
];
