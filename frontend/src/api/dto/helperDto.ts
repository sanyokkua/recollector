export interface StatisticDto {
    totalNumberOfCategories: number;
    totalNumberOfItems: number;
    totalNumberOfItemsTodo: number;
    totalNumberOfItemsInProgress: number;
    totalNumberOfItemsFinished: number;
}

export interface SettingsDto {
    userEmail: string;
    categoryBackgroundColor: string;
    categoryItemColor: string;
    categoryFabColor: string;
    categoryPageSize: number;
    itemBackgroundColor: string;
    itemItemColor: string;
    itemFabColor: string;
    itemPageSize: number;
}