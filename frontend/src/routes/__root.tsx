import { QueryClientProvider } from "@tanstack/react-query";
import { createRootRoute, Outlet } from "@tanstack/react-router";
import { queryClient } from "@/integration/ts-query";

export const Route = createRootRoute({
	component: RootComponent,
});

function RootComponent() {
	return (
		<QueryClientProvider client={queryClient}>
			<noscript>You need to enable JavaScript to run this app.</noscript>
			<Outlet />
		</QueryClientProvider>
	);
}
