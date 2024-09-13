import { logger } from "../config/appConfig.ts";


const log = logger.getLogger("BrowserStore");

export enum ItemType {
    JWT_TOKEN = "JWT_USER_TOKEN",
    REFRESH_TOKEN = "JWT_REFRESH_TOKEN",
    USER_EMAIL = "USER_EMAIL",
    CURRENT_CATEGORY_ID = "CURRENT_CATEGORY_ID",
    CURRENT_CATEGORY_NAME = "CURRENT_CATEGORY_NAME",
}

class BrowserStore {
    getData(itemType: ItemType): string | null {
        log.info(`Getting ${ itemType } from storage`);
        const value: string | null = localStorage.getItem(itemType);
        log.debug(`Retrieved ${ itemType }: ${ value }`);
        return value;
    }

    saveData(itemType: ItemType, dataValue: string): void {
        log.info(`Saving ${ itemType } to storage`);
        localStorage.setItem(itemType, dataValue);
        log.debug(`Saved ${ itemType }: ${ dataValue }`);
    }

    deleteData(itemType: ItemType): void {
        log.info(`Removing ${ itemType } from storage`);
        localStorage.removeItem(itemType);
        log.debug(`Removed ${ itemType } from storage`);
    }
}

const browserStore = new BrowserStore();

export const userEmailSaver = (email: string | undefined) => {
    if (email === undefined || email === null) {
        throw new Error("Email is undefined or empty");
    }
    return browserStore.saveData(ItemType.USER_EMAIL, email);
};
export const userEmailExtractor = () => {
    return browserStore.getData(ItemType.USER_EMAIL);
};
export const userJwtTokenSaver = (jwtToken: string | undefined) => {
    if (jwtToken === undefined || jwtToken === null) {
        throw new Error("JwtToken is undefined or empty");
    }
    return browserStore.saveData(ItemType.JWT_TOKEN, jwtToken);
};
export const userJwtTokenExtractor = () => {
    return browserStore.getData(ItemType.JWT_TOKEN);
};
export const userJwtRefreshTokenSaver = (jwtRefresh: string | undefined) => {
    if (jwtRefresh === undefined || jwtRefresh === null) {
        throw new Error("JwtRefresh is undefined or empty");
    }
    return browserStore.saveData(ItemType.REFRESH_TOKEN, jwtRefresh);
};
export const userJwtRefreshTokenExtractor = () => {
    return browserStore.getData(ItemType.REFRESH_TOKEN);
};
export const currentCategoryIdSaver = (currentCategoryId: number | undefined) => {
    if (currentCategoryId === undefined || currentCategoryId === null) {
        throw new Error("currentCategoryId is undefined or empty");
    }
    return browserStore.saveData(ItemType.CURRENT_CATEGORY_ID, String(currentCategoryId));
};
export const currentCategoryIdExtractor = (): number => {
    return Number(browserStore.getData(ItemType.CURRENT_CATEGORY_ID));
};

export const currentCategoryNameSaver = (currentCategoryName: string | undefined) => {
    if (currentCategoryName === undefined || currentCategoryName === null) {
        throw new Error("currentCategoryName is undefined or empty");
    }
    return browserStore.saveData(ItemType.CURRENT_CATEGORY_NAME, currentCategoryName);
};
export const currentCategoryNameExtractor = () => {
    return browserStore.getData(ItemType.CURRENT_CATEGORY_NAME);
};
export default BrowserStore;