import { lazy } from "react";
import { type Sidebar, useSidebar } from "@/components/ui/sidebar";

const AppMobileSidebar = lazy(() => import("./mobile"));
const AppDesktopSidebar = lazy(() => import("./desktop"));
export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
	const { isMobile } = useSidebar();
	return isMobile ? (
		<AppMobileSidebar {...props} />
	) : (
		<AppDesktopSidebar {...props} />
	);
}
