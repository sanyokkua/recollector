export type TokenExtractor = () => string | null;
export type TokenSaver = (token: string) => void;