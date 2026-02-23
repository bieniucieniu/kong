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
import {
	useGetApiAiModelsProviderId,
	useGetApiAiModelsProviderIdDefault,
	useGetApiAiProviders,
} from "@/gen/api/default/default";
import {
	type ChatPromptController,
	useChatPrompt,
	useChatPromptController,
} from "../state";

export interface ModelSelectProps {
	controller?: ChatPromptController;
	className?: string;
}

export function ModelSelect({
	controller = useChatPromptController(),
	className,
}: ModelSelectProps) {
	const s = useChatPrompt(controller);

	const onProviderChange = (p: string | null) => (s.provider = p || undefined);
	const onModelChange = (m: string | null) => (s.model = m || undefined);

	const opt = {
		query: { enabled: !!s.provider },
	};

	const providers = useGetApiAiProviders();
	const models = useGetApiAiModelsProviderId(s.provider ?? "", opt);
	const defaultModel = useGetApiAiModelsProviderIdDefault(
		s.provider ?? "",
		opt,
	);

	const isLoading =
		providers.isLoading || models.isLoading || defaultModel.isLoading;

	const errors = [providers.error, models.error, defaultModel.error];

	const disabled = isLoading;

	return (
		<ButtonGroup>
			<Select
				disabled={disabled}
				defaultValue={defaultModel.data?.data.provider.id}
				value={s.provider}
				onValueChange={onProviderChange}
			>
				<SelectTrigger
					disabled={disabled}
					size="none"
					className="border-none"
					render={
						<InputGroupButton className={className} variant="ghost">
							<SelectValue
								placeholder={
									s.provider ??
									defaultModel.data?.data.provider.display ??
									"default"
								}
							/>
							<ChevronDown />
						</InputGroupButton>
					}
				/>
				<SelectContent>
					{providers.data?.data.map((v) => (
						<SelectItem key={v.id} value={v.id}>
							{v.display}
						</SelectItem>
					))}
				</SelectContent>
			</Select>

			<Select
				disabled={disabled || s.provider === undefined}
				defaultValue={defaultModel.data?.data.id}
				value={s.model}
				onValueChange={onModelChange}
			>
				<SelectTrigger
					size="none"
					className="border-none"
					render={
						<InputGroupButton className={className} variant="ghost">
							<SelectValue placeholder={s.model ?? "default"} />
							<ChevronDown />
						</InputGroupButton>
					}
				/>
				<SelectContent>
					{models.data?.data.map((v) => (
						<SelectItem key={v.id} value={v.id}>
							{v.id}
						</SelectItem>
					))}
				</SelectContent>
			</Select>
			<FieldError errors={errors} />
		</ButtonGroup>
	);
}
