import { useMemo } from "react";
import type { MaybeArray } from "@/lib/helper-types";

export function ErrorBox({
	error,
}: {
	error: { message: string; reason?: MaybeArray<string> } | undefined | null;
	className?: string;
}) {
	const reason = useMemo(() => {
		if (!error) return null;
		const arr = Array.isArray(error.reason)
			? error.reason.length > 0
				? error.reason
				: null
			: error.reason != null
				? [error.reason]
				: null;
		return arr;
	}, [error]);
	if (!error) return null;
	return (
		<div>
			<p>{error.message}</p>
			{reason && (
				<ul className="list-disc">
					{reason.map((it) => (
						<li key={it}>{it}</li>
					))}
				</ul>
			)}
		</div>
	);
}
