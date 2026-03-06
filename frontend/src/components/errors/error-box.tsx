import type { MaybeArray } from "@/lib/helper-types";

const reasonCache = new WeakMap<object, string[]>();

function getReason(error: ErrorMessage) {
	let res: string[] | undefined | null = reasonCache.get(error);
	if (!res) {
		res = Array.isArray(error.reason)
			? error.reason.length > 0
				? error.reason
				: null
			: error.reason != null
				? [error.reason]
				: null;

		if (res) reasonCache.set(error, res);
	}
	return res;
}

interface ErrorMessage {
	message: string;
	reason?: MaybeArray<string> | undefined | null;
}

export function ErrorBox({
	error,
}: {
	error:
		| { message: string; reason?: MaybeArray<string> | undefined | null }
		| undefined
		| null;
	className?: string;
}) {
	"use no memo";
	if (!error) return null;

	const reason = getReason(error);
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
