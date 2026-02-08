import { ChevronDown } from "lucide-react";
import { ButtonGroup } from "@/components/ui/button-group";
import { FieldError } from "@/components/ui/field";
import { InputGroupButton } from "@/components/ui/input-group";
import {
	Select,
	SelectContent,
	SelectItem,
	SelectTrigger,
	SelectValue,
} from "@/components/ui/select";
import { useChatModels } from "@/features/chat/lib/chat";
import type { ChatState } from "../lib/chat/state";

export function ModelSelect({
	chatState,
	className,
}: {
	chatState: ChatState;
	className?: string;
}) {
	const {
		model,
		models,
		provider,
		providers,
		status,
		error,
		selectModel,
		selectProvider,
	} = useChatModels(chatState);
	const disabled = status !== "success";
	return (
		<ButtonGroup>
			<Select
				disabled={disabled}
				defaultValue={provider}
				value={provider}
				onValueChange={selectProvider}
			>
				<SelectTrigger
					disabled={disabled}
					size="none"
					className="border-none"
					render={
						<InputGroupButton className={className} variant="ghost">
							<SelectValue placeholder="default">
								{(it) => it ?? provider ?? "default"}
							</SelectValue>
							<ChevronDown />
						</InputGroupButton>
					}
				/>
				<SelectContent>
					{providers.map((v) => (
						<SelectItem key={v.id} value={v.id}>
							{v.display}
						</SelectItem>
					))}
				</SelectContent>
			</Select>

			<Select
				disabled={disabled || provider === undefined}
				defaultValue={model}
				value={model}
				onValueChange={selectModel}
			>
				<SelectTrigger
					size="none"
					className="border-none"
					render={
						<InputGroupButton className={className} variant="ghost">
							<SelectValue placeholder="default">
								{(it) => it ?? model ?? "default"}
							</SelectValue>
							<ChevronDown />
						</InputGroupButton>
					}
				/>
				<SelectContent>
					{models.map((v) => (
						<SelectItem key={v.id} value={v.id}>
							{v.id}
						</SelectItem>
					))}
				</SelectContent>
			</Select>
			<FieldError errors={[error]} />
		</ButtonGroup>
	);
}
