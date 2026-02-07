import { QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import { createRootRoute, Outlet } from "@tanstack/react-router";
import { AppLayout } from "@/components/app-layout";
import { queryClient } from "@/integration/ts-query";

export const Route = createRootRoute({
	component: RootComponent,
});

function RootComponent() {
	return (
		<QueryClientProvider client={queryClient}>
			<ReactQueryDevtools initialIsOpen={false} />
			<AppLayout>
				<Outlet />
			</AppLayout>
		</QueryClientProvider>
	);
}
