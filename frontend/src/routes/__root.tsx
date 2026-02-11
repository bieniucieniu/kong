import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import { createRootRoute, Outlet } from "@tanstack/react-router";
import { AppLayout } from "@/components/app-layout";

export const Route = createRootRoute({
	component: RootComponent,
});

function RootComponent() {
	return (
		<>
			<ReactQueryDevtools initialIsOpen={false} />
			<AppLayout>
				<Outlet />
			</AppLayout>
		</>
	);
}
