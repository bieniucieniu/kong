import {
	experimental_streamedQuery,
	useMutation,
	useQuery,
} from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { ComponentExample } from "@/components/component-example";
import { Button } from "@/components/ui/button";
import {
	InputGroup,
	InputGroupButton,
	InputGroupInput,
	InputGroupText,
} from "@/components/ui/input-group";

export const Route = createFileRoute("/")({
	component: RouteComponent,
});

function RouteComponent() {
	const [response, setResponse] = useState("");

	// Define the mutation
	const m = useMutation({
		mutationFn: async (prompt: string) => {
			setResponse(""); // Reset previous text

			const res = await fetch("/ai/chat", {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify({ message: prompt }),
			});

			if (!res.ok || !res.body) throw new Error("Network error");

			// 1. Get the Reader
			const reader = res.body.getReader();
			const decoder = new TextDecoder();

			// 2. Loop through the stream
			while (true) {
				const { done, value } = await reader.read();
				if (done) break;

				// 3. Decode chunk and update state
				const chunk = decoder.decode(value, { stream: true });

				// Use functional state update to ensure we append correctly
				setResponse((prev) => prev + chunk);
			}
		},
		onError: (error) => {
			console.error("Streaming failed:", error);
		},
	});
	const [prompt, setPrompt] = useState("");
	return (
		<div>
			<InputGroup className="max-w-[400px]">
				<InputGroupInput
					onChange={(e) => setPrompt(e.target.value)}
					value={prompt}
				/>
				<InputGroupButton onClick={() => m.mutate(prompt)}>
					{m.status}
				</InputGroupButton>
				<InputGroupText>{m.error?.message}</InputGroupText>
			</InputGroup>
			<span className="">{response}</span>
			<ComponentExample />
		</div>
	);
}
