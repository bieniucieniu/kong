import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import { createRootRoute, Outlet } from "@tanstack/react-router";
import { AppLayout } from "@/components/app-layout";
import { TooltipProvider } from "@/components/ui/tooltip";

export const Route = createRootRoute({
	component: RootComponent,
});

function RootComponent() {
	return (
		<>
			<ReactQueryDevtools initialIsOpen={false} position="right" />
			<TooltipProvider>
				<AppLayout>
					<Outlet />
				</AppLayout>
			</TooltipProvider>
		</>
	);
}
