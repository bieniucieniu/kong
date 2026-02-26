import { Link } from "@tanstack/react-router";
import { createElement, useState } from "react";
import {
	Sidebar,
	SidebarContent,
	SidebarFooter,
	SidebarGroup,
	SidebarGroupContent,
	SidebarHeader,
	SidebarMenu,
	SidebarMenuButton,
	SidebarMenuItem,
	useSidebar,
} from "@/components/ui/sidebar";
import { SidebarThemeModeToggle } from "@/integration/shadcn/components/theme-toggle";
import { cn } from "@/lib/utils";
import icon from "/icon.svg?url";
import { DropdownMenuGroup } from "../ui/dropdown-menu";
import { SidebarUser } from "./app-user";
import { data, type NavEntry } from "./data";

export default function AppDesktopSidebar({
	className,
	...props
}: React.ComponentProps<typeof Sidebar>) {
	const [activeItem, setActiveItem] = useState<NavEntry>(data[0]);
	const { setOpen } = useSidebar();

	return (
		<Sidebar
			collapsible="icon"
			className={cn(
				"overflow-hidden *:data-[sidebar=sidebar]:flex-row",
				className,
			)}
			{...props}
		>
			<Sidebar
				collapsible="none"
				className="w-[calc(var(--sidebar-width-icon)+1px)]! border-r"
			>
				<SidebarHeader>
					<SidebarMenu>
						<SidebarMenuItem>
							<SidebarMenuButton
								size="lg"
								className="md:h-8 md:p-0"
								render={() => (
									<Link to="/">
										<div className="bg-sidebar-primary text-sidebar-primary-foreground flex aspect-square size-8 items-center justify-center rounded-lg">
											<img src={icon} alt="kong" />
										</div>
									</Link>
								)}
							/>
						</SidebarMenuItem>
					</SidebarMenu>
				</SidebarHeader>
				<SidebarContent>
					<SidebarGroup>
						<SidebarGroupContent className="px-1.5 md:px-0">
							<SidebarMenu>
								{data.map((item) => (
									<SidebarMenuItem key={item.title}>
										<SidebarMenuButton
											tooltip={{
												children: item.title,
												hidden: false,
											}}
											onClick={() => {
												setActiveItem(item);
												setOpen(true);
											}}
											isActive={activeItem?.title === item.title}
											className="px-2.5 md:px-2"
										>
											<item.icon />
											<span>{item.title}</span>
										</SidebarMenuButton>
									</SidebarMenuItem>
								))}
							</SidebarMenu>
						</SidebarGroupContent>
					</SidebarGroup>
				</SidebarContent>
				<SidebarFooter>
					<DropdownMenuGroup>
						<SidebarMenu>
							<SidebarMenuItem>
								<SidebarThemeModeToggle />
							</SidebarMenuItem>
							<SidebarMenuItem>
								<SidebarUser />
							</SidebarMenuItem>
						</SidebarMenu>
					</DropdownMenuGroup>
				</SidebarFooter>
			</Sidebar>
			<Sidebar collapsible="none" className="flex-1 flex">
				{activeItem && createElement(activeItem.content, activeItem)}
			</Sidebar>
		</Sidebar>
	);
}
