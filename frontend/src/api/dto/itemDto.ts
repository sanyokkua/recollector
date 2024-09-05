import {FilterDirectionEnum} from "./common.ts";

export interface ItemDto {
    itemId?: number;
    categoryId?: number;
    itemName?: string;
    itemStatus?: ItemDtoItemStatusEnum;
    itemNotes?: string;
}

export enum ItemDtoItemStatusEnum {
    FINISHED = 'FINISHED',
    IN_PROGRESS = 'IN_PROGRESS',
    TODO_LATER = 'TODO_LATER'
}

export interface ItemFilter {
    page?: number;
    size?: number;
    categoryId?: number;
    itemName?: string;
    itemStatus?: string;
    direction?: FilterDirectionEnum;
}

