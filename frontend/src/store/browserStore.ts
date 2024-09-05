import log from "loglevel";


export enum ItemType {
    JWT_TOKEN = "JWT_USER_TOKEN", REFRESH_TOKEN = "JWT_REFRESH_TOKEN"
}

class BrowserStore {
    getData(itemType: ItemType): string | null {
        log.info(`Getting ${itemType} from storage`);
        const value: string | null = localStorage.getItem(itemType);
        log.debug(`Retrieved ${itemType}: ${value}`);
        return value;
    }

    saveData(itemType: ItemType, dataValue: string): void {
        log.info(`Saving ${itemType} to storage`);
        localStorage.setItem(itemType, dataValue);
        log.debug(`Saved ${itemType}: ${dataValue}`);
    }

    deleteData(itemType: ItemType): void {
        log.info(`Removing ${itemType} from storage`);
        localStorage.removeItem(itemType);
        log.debug(`Removed ${itemType} from storage`);
    }
}

const browserStore = new BrowserStore();

export const jwtTokenExtractor = () => {
    return browserStore.getData(ItemType.JWT_TOKEN);
}

export const jwtTokenSaver = (token: string) => {
    return browserStore.saveData(ItemType.JWT_TOKEN, token);
}

export default BrowserStore;