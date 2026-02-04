import { Loader } from "lucide-react";
import { useEffect, useRef, useState } from "react";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useSignalState } from "@/lib/hooks/state/signal";
import { cn } from "@/lib/utils";
import { type ChatState, useIsChatMutating } from "../lib/chat";
import { ChatMessageCard } from "./chat-message-card";

export function ChatList({
	state,
	className,
}: {
	state: ChatState;
	className?: string;
}) {
	const chat = useSignalState(state.messages);
	const isLoading = useIsChatMutating();

	const ref = useRef<HTMLDivElement>(null);

	let last = chat.length > 0 ? chat[chat.length - 1] : undefined;
	last = last?.author !== "user" ? last : undefined;

	const [isAtBottom, setIsAtBottom] = useState(true);

	const messagesEndRef = useRef<HTMLDivElement>(null);
	const containerRef = useRef<HTMLDivElement>(null);

	const handleScroll = () => {
		if (!containerRef.current) return;
		const { scrollTop, scrollHeight, clientHeight } = containerRef.current;
		const distanceToBottom = scrollHeight - scrollTop - clientHeight;
		const isBottom = distanceToBottom < 5;
		setIsAtBottom(isBottom);
	};

	useEffect(() => {
		if (isAtBottom) {
			messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
		}
	}, [last, isAtBottom]);

	return (
		<ScrollArea
			className={cn("w-full", className)}
			onScroll={handleScroll}
			ref={ref}
		>
			<div className="h-full flex flex-col gap-4 py-4 items-start w-[min(100%,48rem)] mx-auto">
				{chat.map((item, i) => {
					const { prompt: message, author } = item;
					const key = `${author}-${i}-${message.slice(0, 10)}`;
					return (
						<ChatMessageCard
							className={cn({
								"self-end bg-accent text-accent-foreground": author === "user",
								"min-w-full": author !== "user",
							})}
							key={key}
							message={item}
						/>
					);
				})}
				<div ref={messagesEndRef} />
				{isLoading && (
					<div className="self-start">
						<Loader className="animate-spin" />{" "}
					</div>
				)}
			</div>
		</ScrollArea>
	);
}
