import { CaretDownIcon } from "@phosphor-icons/react";
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
	useGetApiAiProvidersDefault,
} from "@/gen/api/kong";
import {
	type ChatPromptController,
	useChatPromptController,
	useChatPromptState,
} from "../state/prompt";

export interface ModelSelectProps {
	controller?: ChatPromptController;
	className?: string;
}

export function ModelSelect({
	controller = useChatPromptController(),
	className,
}: ModelSelectProps) {
	const s = useChatPromptState(controller);

	const onProviderChange = (p: string | null) => (s.provider = p || undefined);
	const onModelChange = (m: string | null) => (s.model = m || undefined);

	const providers = useGetApiAiProviders();
	const defaultProvider = useGetApiAiProvidersDefault();

	const provider = s.provider || defaultProvider.data?.data.id;
	const opt = {
		query: { enabled: !!provider },
	};

	const models = useGetApiAiModelsProviderId(provider ?? "", opt);
	const defaultModel = useGetApiAiModelsProviderIdDefault(provider ?? "", opt);

	const model = s.model || defaultModel.data?.data.id;

	const isLoading =
		providers.isLoading || models.isLoading || defaultModel.isLoading;

	const errors = [providers.error, models.error, defaultModel.error];

	const disabled = isLoading;

	return (
		<ButtonGroup>
			<Select
				disabled={disabled}
				defaultValue={defaultProvider.data?.data.id}
				value={provider}
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
									provider ??
									defaultModel.data?.data.provider.display ??
									"default"
								}
							/>
							<CaretDownIcon />
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
				disabled={disabled || provider === undefined}
				defaultValue={defaultModel.data?.data.id}
				value={model}
				onValueChange={onModelChange}
			>
				<SelectTrigger
					size="none"
					className="border-none"
					render={
						<InputGroupButton className={className} variant="ghost">
							<SelectValue placeholder={model ?? "default"} />
							<CaretDownIcon />
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
