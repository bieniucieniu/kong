import { Link } from "@tanstack/react-router";
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
} from "@/components/ui/sidebar";
import { SidebarUser } from "@/features/session/components/sidebar-user";
import { SidebarThemeModeToggle } from "@/integration/shadcn/components/theme-toggle";
import { cn } from "@/lib/utils";
import icon from "/icon.svg?url";
import { DropdownMenuGroup } from "../ui/dropdown-menu";
import { data } from "./data";

export default function AppMobileSidebar({
	className,
	...props
}: React.ComponentProps<typeof Sidebar>) {
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
											render={
												<Link
													activeProps={{ "data-active": true }}
													to={item.mobileLink}
												>
													{item.title}
												</Link>
											}
										/>
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
		</Sidebar>
	);
}
