import { FilterDirectionEnum } from "./common.ts";


export interface CategoryDto {
    categoryId?: number;
    categoryName?: string;
    todoItems?: number;
    inProgressItems?: number;
    finishedItems?: number;
}

export interface CategoryFilter {
    page?: number;
    size?: number;
    categoryName?: string;
    direction?: FilterDirectionEnum;
}


