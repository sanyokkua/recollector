export enum FilterDirectionEnum {
    ASC = "ASC",
    DESC = "DESC"
}

export interface PaginationInfo {
    currentPage?: number;
    itemsPerPage?: number;
    totalPages?: number;
    totalItems?: number;
    sortField?: string;
    sortDirection?: string;
}

export interface MetaInfo {
    pagination?: PaginationInfo;
}

export interface Response<T> {
    statusCode: number;
    statusMessage: string;
    data?: T;
    meta?: MetaInfo;
    error?: string;
}