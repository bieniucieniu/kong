import "./index.css";
import { QueryClientProvider } from "@tanstack/react-query";
import { createRouter, RouterProvider } from "@tanstack/react-router";
import { StrictMode } from "react";
import ReactDOM from "react-dom/client";
import { queryClient } from "./integration/ts-query";
import { routeTree } from "./routeTree.gen";

const router = createRouter({ routeTree, context: { client: queryClient } });

declare module "@tanstack/react-router" {
	interface Register {
		router: typeof router;
	}
}

//biome-ignore lint/style/noNonNullAssertion: This is a valid use case
const rootElement = document.getElementById("root")!;
if (!rootElement.innerHTML) {
	const root = ReactDOM.createRoot(rootElement);
	root.render(
		<StrictMode>
			<QueryClientProvider client={queryClient}>
				<RouterProvider router={router} />
			</QueryClientProvider>
		</StrictMode>,
	);
}
