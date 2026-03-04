import { PlusIcon } from "@phosphor-icons/react/dist/ssr";
import { Button } from "@/components/ui/button";
import { useSession } from "@/features/session";
import { usePostApiAiChatNew } from "@/gen/api/kong";
import { ChatInput } from "./prompt-input";

export interface NewChatProps {
	onNewChat?: (id: (string & {}) | "free") => void;
	className?: string;
}
export function NewChatMockInput({ onNewChat, className }: NewChatProps) {
	const m = usePostApiAiChatNew({
		mutation: {
			onSuccess: (a) => {
				onNewChat?.(a.data.id);
			},
		},
	});
	const s = useSession();

	return (
		<ChatInput
			id=""
			disabled={s.isLoading}
			onFocus={async () => {
				if (s.isLoading) return;

				if (!s.session) onNewChat?.("free");
				await m.mutateAsync();
			}}
			className={className}
		/>
	);
}

export function NewChatButton({
	onNewChat,
	className,
	content = (
		<>
			new <PlusIcon />
		</>
	),
}: NewChatProps & {
	content?: React.ReactNode;
}) {
	const m = usePostApiAiChatNew({
		mutation: {
			onSuccess: (a) => {
				onNewChat?.(a.data.id);
			},
		},
	});
	const s = useSession();

	return (
		<Button
			onClick={async () => {
				if (s.isLoading) return;

				if (!s.session) onNewChat?.("free");
				await m.mutateAsync();
			}}
			className={className}
		>
			{content}
		</Button>
	);
}
