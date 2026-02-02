import { defineConfig } from "orval";

export default defineConfig({
	kong: {
		output: {
			mode: "tags-split",
			target: "src/gen/api",
			schemas: "src/gen/models",
			client: "react-query",
			override: {
				query: {
					shouldSplitQueryKey: true,
					version: 5,
				},
			},
		},
		input: {
			target: "http://localhost:8080/swagger/documentation.yaml",
		},
	},
});
