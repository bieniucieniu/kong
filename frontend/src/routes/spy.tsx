import {
	type AbstractPowerSyncDatabase,
	type PowerSyncBackendConnector,
	type PowerSyncCredentials,
	PowerSyncDatabase,
} from "@powersync/web";
import { powerSyncCollectionOptions } from "@tanstack/powersync-db-collection";
import { createCollection, useLiveQuery } from "@tanstack/react-db";
import { createFileRoute } from "@tanstack/react-router";
import { AppSchema } from "@/gen-ps";

export const db = new PowerSyncDatabase({
	schema: AppSchema,
	database: {
		dbFilename: "powersync.db",
	},
});
export class Connector implements PowerSyncBackendConnector {
	async fetchCredentials(): Promise<PowerSyncCredentials | null> {
		console.log({
			endpoint: "http://localhost:8080",
			token: import.meta.env.VITE_PS_TOKEN,
		});
		return {
			endpoint: "http://localhost:8080",
			token: import.meta.env.VITE_PS_TOKEN,
		};
	}

	async uploadData(db: AbstractPowerSyncDatabase) {
		const b = await db.getCrudBatch();
		console.log(b);
		if (!b?.crud.length) return;
		b?.complete();
	}
}
db.connect(new Connector());

const chatMessageCollection = createCollection(
	powerSyncCollectionOptions({
		database: db,
		table: AppSchema.props.chat_message,
	}),
);
export const Route = createFileRoute("/spy")({
	component: RouteComponent,
});

function RouteComponent() {
	const { data, status } = useLiveQuery((q) =>
		q.from({ messages: chatMessageCollection }).select(({ messages }) => ({
			id: messages.id,
			content: messages.content,
		})),
	);
	return (
		<div>
			{status}
			<ul>
				{data.map(({ id, content }) => (
					<li key={id}>
						`${id}: ${content}`
					</li>
				))}
			</ul>
		</div>
	);
}
