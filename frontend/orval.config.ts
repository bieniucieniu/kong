import { defineConfig } from "orval";

export default defineConfig({
  kong: {
    output: {
      mode: "split",
      target: "src/gen/api",
      schemas: "src/gen/models",
      client: "react-query",
      clean: true,
      override: {
        query: {
          shouldSplitQueryKey: true,
          version: 5,
        },
        fetch: {
          forceSuccessResponse: true,
        },
      },
    },
    input: {
      target: "http://localhost:5050/swagger/documentation.yaml",
    },
  },
});
