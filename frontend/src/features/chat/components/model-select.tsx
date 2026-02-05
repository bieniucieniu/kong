import { ChevronDown } from "lucide-react";
import { FieldError } from "@/components/ui/field";
import { InputGroupButton } from "@/components/ui/input-group";
import {
	MenubarContent,
	MenubarMenu,
	MenubarRadioGroup,
	MenubarRadioItem,
	MenubarTrigger,
} from "@/components/ui/menubar";
import {
	Select,
	SelectContent,
	SelectItem,
	SelectTrigger,
	SelectValue,
} from "@/components/ui/select";
import { useChatModels } from "@/features/chat/lib/chat";
import { cn } from "@/lib/utils";
import type { ChatState } from "../lib/chat/state";

export function ModelSelect({
	chatState,
	className,
}: {
	chatState: ChatState;
	className?: string;
}) {
	const { model, models, status, error, selectModel } =
		useChatModels(chatState);
	const disabled = status !== "success";
	return (
		<>
			<Select defaultValue={model} value={model} onValueChange={selectModel}>
				<SelectTrigger
					disabled={disabled}
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
		</>
	);
}

export function ModelMenubarMenu({
	chatState,
	className,
}: {
	chatState: ChatState;
	className?: string;
}) {
	const { model, models, status, error, selectModel } =
		useChatModels(chatState);
	const disabled = status !== "success";
	return (
		<MenubarMenu>
			<MenubarTrigger
				disabled={disabled}
				className={cn("min-w-26 flex justify-between", className)}
				render={
					<InputGroupButton variant={error && "destructive"}>
						{error?.message ?? model}
						<ChevronDown />
					</InputGroupButton>
				}
			/>
			<MenubarContent>
				<MenubarRadioGroup
					className="*:justify-end"
					value={model}
					onValueChange={selectModel}
				>
					{models.map((it) => (
						<MenubarRadioItem key={it.id} value={it.id}>
							{it.provider.id?.toString()} {it.id}
						</MenubarRadioItem>
					))}
				</MenubarRadioGroup>
			</MenubarContent>
		</MenubarMenu>
	);
}
