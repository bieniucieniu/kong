export type Maybe<T> = T | undefined | null;
export type MaybePromise<T> = T | Promise<T>;
export type MaybeArray<T> = T | T[];
export type AnyString<T extends string> = T | (string & {});
export type MaybeArrayElement<T> = T extends (infer U)[] | Array<infer U>
	? U
	: T;
