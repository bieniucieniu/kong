import path from "node:path";
import tailwindcss from "@tailwindcss/vite";
import { tanstackRouter } from "@tanstack/router-plugin/vite";
import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";

// https://vite.dev/config/
export default defineConfig({
	optimizeDeps: { exclude: ["@powersync/web"] },
	plugins: [
		tanstackRouter({
			target: "react",
			autoCodeSplitting: true,
		}),
		react({
			babel: {
				plugins: ["babel-plugin-react-compiler"],
			},
		}),
		tailwindcss(),
	],
	build: {
		outDir: "../src/main/resources/frontend",
	},

	server: {
		proxy: {
			"/api": {
				target: "http://localhost:5050",
				changeOrigin: true,
				secure: false,
				timeout: 0,
				proxyTimeout: 0,
			},
			"/swagger": "http://localhost:5050",
		},
	},

	resolve: {
		alias: {
			"@": path.resolve(__dirname, "./src"),
		},
	},
});
