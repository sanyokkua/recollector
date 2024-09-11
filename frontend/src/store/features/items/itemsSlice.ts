import {createAsyncThunk, createSlice, PayloadAction} from "@reduxjs/toolkit";
import {ItemDto, ItemDtoItemStatusEnum, ItemFilter} from "../../../api/dto/itemDto.ts";
import axiosClient, {logger} from "../../../config/appConfig.ts";
import {FilterDirectionEnum, Response} from "../../../api/dto/common.ts";
import ItemApiClient from "../../../api/client/itemApiClient.ts";

const log = logger.getLogger("itemsSlice");

export interface ItemState {
    currentPage: number;
    itemsPerPage: number;
    totalItems: number;
    totalPages: number;

    filter: ItemFilter;
    selectedItem?: ItemDto | null;
    allItems: ItemDto[];
    loading: boolean;
    error: string | null;
}

const initialState: ItemState = {
    currentPage: 0,
    itemsPerPage: 10,
    totalItems: 0,
    totalPages: 0,
    filter: {
        page: 0,
        size: 10,
        itemName: "",
        itemStatus: ItemDtoItemStatusEnum.ALL,
        direction: FilterDirectionEnum.ASC
    },
    allItems: [],
    selectedItem: null,
    loading: false,
    error: null
};

export type ItemCreateRequest = {
    categoryId: number;
    itemDto: ItemDto;
    jwtToken: string;
}

export type ItemUpdateRequest = {
    itemId: number;
    categoryId: number;
    itemDto: ItemDto;
    jwtToken: string;
}

export type ItemGetRequest = {
    itemId: number;
    categoryId: number;
    jwtToken: string;
}

export type GetItemsRequest = {
    filter: ItemFilter;
    jwtToken: string;
}

// Helper function to handle errors consistently
const handleError = (error: any, message: string) => {
    const errorMessage = error?.message || message;
    log.warn(errorMessage, error);
    return errorMessage;
};

export const createItem = createAsyncThunk("items/create",
    async (itemRequest: ItemCreateRequest, {rejectWithValue}) => {
        log.info("createAsyncThunk will try to call createItem API");
        log.debug(`createAsyncThunk, incoming parameters: newItem -> ${JSON.stringify(itemRequest)}`);
        try {
            const client = new ItemApiClient(axiosClient, itemRequest.jwtToken);
            return await client.createItem(itemRequest.categoryId, itemRequest.itemDto);
        } catch (error: any) {
            return rejectWithValue(handleError(error, "Failed to create item"));
        }
    }
);

export const getAllItems = createAsyncThunk("items/fetchAll",
    async (filter: GetItemsRequest, {rejectWithValue}) => {
        log.info("getAllItems will try to fetch items");
        log.debug(`getAllItems, incoming filter: ${JSON.stringify(filter)}`);
        try {
            const client = new ItemApiClient(axiosClient, filter.jwtToken);
            return await client.getAllItems(filter.filter.categoryId ?? -1, filter.filter);
        } catch (error: any) {
            return rejectWithValue(handleError(error, "Failed to get items"));
        }
    }
);

export const getItem = createAsyncThunk("items/fetchById",
    async (itemGetReq: ItemGetRequest, {rejectWithValue}) => {
        log.info(`getItem will try to fetch item with id ${itemGetReq.itemId} for category ${itemGetReq.categoryId}`);
        try {
            const client = new ItemApiClient(axiosClient, itemGetReq.jwtToken);
            return await client.getItem(itemGetReq.categoryId, itemGetReq.itemId);
        } catch (error: any) {
            return rejectWithValue(handleError(error, "Failed to get item"));
        }
    }
);

export const updateItem = createAsyncThunk("items/update",
    async (itemUpdateReq: ItemUpdateRequest, {rejectWithValue}) => {
        log.info(`updateItem will try to update item with id ${itemUpdateReq.itemId}`);
        log.debug(`updateItem, incoming parameters: id -> ${itemUpdateReq.itemId}, itemDto -> ${JSON.stringify(itemUpdateReq)}`);
        try {
            const client = new ItemApiClient(axiosClient, itemUpdateReq.jwtToken);
            return await client.updateItem(itemUpdateReq.categoryId, itemUpdateReq.itemId, itemUpdateReq.itemDto);
        } catch (error: any) {
            return rejectWithValue(handleError(error, "Failed to update item"));
        }
    }
);

export const deleteItem = createAsyncThunk("items/delete",
    async (itemGetReq: ItemGetRequest, {rejectWithValue}) => {
        log.info(`deleteItem will try to delete item with id ${itemGetReq.itemId} for category: ${itemGetReq.categoryId}`);
        log.debug(`deleteItem, incoming id: ${itemGetReq.itemId}`);
        try {
            const client = new ItemApiClient(axiosClient, itemGetReq.jwtToken);
            return await client.deleteItem(itemGetReq.categoryId, itemGetReq.itemId);
        } catch (error: any) {
            return rejectWithValue(handleError(error, "Failed to delete item"));
        }
    }
);


export const itemsSlice = createSlice({
    name: "items",
    initialState,
    reducers: {
        setItemFilterPage: (state: ItemState, action: PayloadAction<number>) => {
            log.debug(`itemsSlice.reducers.setFilterPage. current state: ${JSON.stringify(state)}, action: ${JSON.stringify(action.payload)}`);
            state.filter.page = action.payload;
            state.error = "";
        },
        setItemFilterSize: (state: ItemState, action: PayloadAction<number>) => {
            log.debug(`itemsSlice.reducers.setFilterSize. current state: ${JSON.stringify(state)}, action: ${JSON.stringify(action.payload)}`);
            state.filter.size = action.payload;
            state.itemsPerPage = action.payload;
            state.error = "";
        },
        setItemFilterItemName: (state: ItemState, action: PayloadAction<string>) => {
            log.debug(`itemsSlice.reducers.setFilterItemName. current state: ${JSON.stringify(state)}, action: ${JSON.stringify(action.payload)}`);
            state.filter.itemName = action.payload;
            state.error = "";
        },
        setItemFilterDirection: (state: ItemState, action: PayloadAction<FilterDirectionEnum>) => {
            log.debug(`itemsSlice.reducers.setFilterDirection. current state: ${JSON.stringify(state)}, action: ${JSON.stringify(action.payload)}`);
            state.filter.direction = action.payload;
            state.error = "";
        },
        setItemFilterStatus: (state: ItemState, action: PayloadAction<ItemDtoItemStatusEnum>) => {
            log.debug(`itemsSlice.reducers.setFilterDirection. current state: ${JSON.stringify(state)}, action: ${JSON.stringify(action.payload)}`);
            state.filter.itemStatus = action.payload;
            state.error = "";
        },
        setItemFilterCategoryId: (state: ItemState, action: PayloadAction<number>) => {
            log.debug(`itemsSlice.reducers.setFilterDirection. current state: ${JSON.stringify(state)}, action: ${JSON.stringify(action.payload)}`);
            state.filter.categoryId = action.payload;
            state.error = "";
        },
        setItemSelectedItem: (state: ItemState, action: PayloadAction<ItemDto | null>) => {
            log.debug(`itemsSlice.reducers.setSelectedItem. current state: ${JSON.stringify(state)}, action: ${JSON.stringify(action.payload)}`);
            state.selectedItem = action.payload;
            state.error = "";
        }
    },
    extraReducers: (builder) => {
        builder
            .addCase(createItem.pending, (state: ItemState) => {
                log.debug("createItem.pending: Creating item...");
                state.loading = true;
                state.error = "";
            })
            .addCase(createItem.fulfilled, (state: ItemState, action: PayloadAction<Response<ItemDto>>) => {
                log.info("createItem.fulfilled: item created successfully");
                state.loading = false;
                state.error = action.payload.error ?? null;
                log.debug(`createItem.fulfilled. Created item: ${JSON.stringify(action.payload)}`);
            })
            .addCase(createItem.rejected, (state: ItemState, action) => {
                log.error("createItem.rejected: Failed to create item", action.payload);
                state.loading = false;
                state.error = action.payload as string;
            })

            .addCase(getAllItems.pending, (state: ItemState) => {
                log.debug("getAllItems.pending: Fetching all items...");
                state.loading = true;
                state.error = null;
            })
            .addCase(getAllItems.fulfilled, (state: ItemState, action: PayloadAction<Response<ItemDto[]>>) => {
                log.info("getAllItems.fulfilled: items fetched successfully");
                state.allItems = action.payload.data ?? [];
                state.currentPage = action.payload.meta?.pagination?.currentPage ?? 0;
                state.totalPages = action.payload.meta?.pagination?.totalPages ?? 0;
                state.totalItems = action.payload.meta?.pagination?.totalItems ?? 0;

                state.loading = false;
                state.error = action.payload.error ?? null;
                log.debug(`getAllItems.fulfilled. items: ${JSON.stringify(action.payload.data)}`);
            })
            .addCase(getAllItems.rejected, (state: ItemState, action) => {
                log.error("getAllItems.rejected: Failed to fetch items", action.payload);
                state.loading = false;
                state.error = action.payload as string;
            })

            .addCase(getItem.pending, (state: ItemState) => {
                log.debug("getItem.pending: Fetching item...");
                state.loading = true;
                state.error = "";
            })
            .addCase(getItem.fulfilled, (state: ItemState, action: PayloadAction<Response<ItemDto>>) => {
                log.info("getItem.fulfilled: item fetched successfully");
                log.debug(`getItem.fulfilled. Action: ${JSON.stringify(action)}`);
                state.selectedItem = action.payload.data ?? null;
                state.loading = false;
                state.error = action.payload.error ?? null;
            })
            .addCase(getItem.rejected, (state: ItemState, action) => {
                log.error("getItem.rejected: Failed to fetch item", action.payload);
                state.loading = false;
                state.error = action.payload as string;
            })

            .addCase(updateItem.pending, (state: ItemState) => {
                log.debug("updateItem.pending: Updating item...");
                state.loading = true;
                state.error = "";
            })
            .addCase(updateItem.fulfilled, (state: ItemState, action: PayloadAction<Response<ItemDto>>) => {
                log.info("updateItem.fulfilled: item updated successfully");
                log.debug(`updateItem.fulfilled. Updated item: ${JSON.stringify(action.payload)}`);
                state.loading = false;
                state.error = action.payload.error ?? null;
            })
            .addCase(updateItem.rejected, (state: ItemState, action) => {
                log.error("updateItem.rejected: Failed to update item", action.payload);
                state.loading = false;
                state.error = action.payload as string;
            })

            .addCase(deleteItem.pending, (state: ItemState) => {
                log.debug("deleteItem.pending: Deleting item...");
                state.loading = true;
                state.error = "";
            })
            .addCase(deleteItem.fulfilled, (state: ItemState, action: PayloadAction<Response<string>>) => {
                log.info("deleteItem.fulfilled: item deleted successfully");
                state.loading = false;
                state.error = action.payload.error ?? null;
                log.debug(`deleteItem.fulfilled. Action: ${JSON.stringify(action)}`);
            })
            .addCase(deleteItem.rejected, (state: ItemState, action) => {
                log.error("deleteItem.rejected: Failed to delete item", action.payload);
                state.loading = false;
                state.error = action.payload as string;
            });
    }
});

// Export the actions generated by createSlice
export const {
    setItemFilterPage,
    setItemFilterSize,
    setItemFilterItemName,
    setItemFilterDirection,
    setItemSelectedItem,
    setItemFilterCategoryId
} = itemsSlice.actions;

// Export the reducer to be used in the store
export default itemsSlice.reducer;
