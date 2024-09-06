import {createAsyncThunk, createSlice, PayloadAction} from "@reduxjs/toolkit";
import {CategoryDto, CategoryFilter} from "../../../api/dto/categoryDto.ts";
import {FilterDirectionEnum, Response} from "../../../api/dto/common.ts";
import {categoryApiClient} from "../../../api";
import {logger} from "../../../config/appConfig.ts";

const log = logger.getLogger("categorySlice");

export interface CategoryState {
    currentPage: number;
    itemsPerPage: number;
    totalItems: number;
    totalPages: number;
    filter: CategoryFilter;
    selectedCategory: CategoryDto | null;
    allCategories: CategoryDto[];
    loading: boolean;
    error: string | null;
}

const initialState: CategoryState = {
    currentPage: 0,
    itemsPerPage: 10,
    totalItems: 0,
    totalPages: 0,
    filter: {
        page: 0,
        size: 10,
        categoryName: "",
        direction: FilterDirectionEnum.ASC
    },
    allCategories: [],
    selectedCategory: null,
    loading: false,
    error: null
};

export const createCategory = createAsyncThunk("categories/create",
    async (newCategory: Omit<CategoryDto, "categoryId">, {rejectWithValue}) => {
        log.info("createAsyncThunk will try to call createCategory API");
        log.debug(`createAsyncThunk, incoming parameters: newCategory -> ${JSON.stringify(newCategory)}`);
        try {
            return await categoryApiClient.createCategory(newCategory);
        } catch (error) {
            log.warn("createAsyncThunk failed to call createCategory API", error);
            return rejectWithValue("Failed to create category");
        }
    }
);

export const getAllCategories = createAsyncThunk("categories/fetchAll",
    async (filter: CategoryFilter, {rejectWithValue}) => {
        log.info("getAllCategories will try to fetch categories");
        log.debug(`getAllCategories, incoming filter: ${JSON.stringify(filter)}`);
        try {
            return await categoryApiClient.getAllCategories(filter);
        } catch (error) {
            log.warn("Failed to fetch categories", error);
            return rejectWithValue("Failed to get categories");
        }
    }
);

export const getCategory = createAsyncThunk("categories/fetchById",
    async (id: number, {rejectWithValue}) => {
        log.info(`getCategory will try to fetch category with id ${id}`);
        log.debug(`getCategory, incoming id: ${id}`);
        try {
            return await categoryApiClient.getCategory(id);
        } catch (error) {
            log.warn(`Failed to fetch category with id ${id}`, error);
            return rejectWithValue("Failed to get category");
        }
    }
);

export const updateCategory = createAsyncThunk("categories/update",
    async ({id, categoryDto}: { id: number; categoryDto: CategoryDto }, {rejectWithValue}) => {
        log.info(`updateCategory will try to update category with id ${id}`);
        log.debug(`updateCategory, incoming parameters: id -> ${id}, categoryDto -> ${JSON.stringify(categoryDto)}`);
        try {
            return await categoryApiClient.updateCategory(id, categoryDto);
        } catch (error) {
            log.warn(`Failed to update category with id ${id}`, error);
            return rejectWithValue("Failed to update category");
        }
    }
);

export const deleteCategory = createAsyncThunk("categories/delete",
    async (id: number, {rejectWithValue}) => {
        log.info(`deleteCategory will try to delete category with id ${id}`);
        log.debug(`deleteCategory, incoming id: ${id}`);
        try {
            return await categoryApiClient.deleteCategory(id);
        } catch (error) {
            log.warn(`Failed to delete category with id ${id}`, error);
            return rejectWithValue("Failed to delete category");
        }
    }
);

export const categoriesSlice = createSlice({
    name: "categories",
    initialState,
    reducers: {
        setFilterPage: (state, action: PayloadAction<number>) => {
            log.debug(`categoriesSlice.reducers.setFilterPage. current state: ${JSON.stringify(state)}, action: ${JSON.stringify(action.payload)}`);
            state.filter.page = action.payload;
        },
        setFilterSize: (state, action: PayloadAction<number>) => {
            log.debug(`categoriesSlice.reducers.setFilterSize. current state: ${JSON.stringify(state)}, action: ${JSON.stringify(action.payload)}`);
            state.filter.size = action.payload;
        },
        setFilterCategoryName: (state, action: PayloadAction<string>) => {
            log.debug(`categoriesSlice.reducers.setFilterCategoryName. current state: ${JSON.stringify(state)}, action: ${JSON.stringify(action.payload)}`);
            state.filter.categoryName = action.payload;
        },
        setFilterDirection: (state, action: PayloadAction<FilterDirectionEnum>) => {
            log.debug(`categoriesSlice.reducers.setFilterDirection. current state: ${JSON.stringify(state)}, action: ${JSON.stringify(action.payload)}`);
            state.filter.direction = action.payload;
        },
        setSelectedCategory: (state, action: PayloadAction<CategoryDto | null>) => {
            log.debug(`categoriesSlice.reducers.setSelectedCategory. current state: ${JSON.stringify(state)}, action: ${JSON.stringify(action.payload)}`);
            state.selectedCategory = action.payload;
        }
    },
    extraReducers: (builder) => {
        builder
            .addCase(createCategory.pending, (state: CategoryState) => {
                log.debug("createCategory.pending: Creating category...");
                state.loading = true;
            })
            .addCase(createCategory.fulfilled, (state: CategoryState, action: PayloadAction<Response<CategoryDto>>) => {
                log.info("createCategory.fulfilled: Category created successfully");
                state.loading = false;
                state.error = action.payload.error ?? null;
                log.debug(`createCategory.fulfilled. Created category: ${JSON.stringify(action.payload)}`);
            })
            .addCase(createCategory.rejected, (state: CategoryState, action) => {
                log.error("createCategory.rejected: Failed to create category", action.payload);
                state.loading = false;
                state.error = action.payload as string;
            })

            .addCase(getAllCategories.pending, (state: CategoryState) => {
                log.debug("getAllCategories.pending: Fetching all categories...");
                state.loading = true;
                state.error = null;
            })
            .addCase(getAllCategories.fulfilled, (state: CategoryState, action: PayloadAction<Response<CategoryDto[]>>) => {
                log.info("getAllCategories.fulfilled: Categories fetched successfully");
                state.allCategories = action.payload.data ?? [];
                state.currentPage = action.payload.meta?.pagination?.currentPage ?? 0;
                state.itemsPerPage = action.payload.meta?.pagination?.itemsPerPage ?? 0;
                state.totalPages = action.payload.meta?.pagination?.totalPages ?? 0;
                state.totalItems = action.payload.meta?.pagination?.totalItems ?? 0;
                state.filter.page = state.currentPage;
                state.filter.size = state.itemsPerPage;

                state.loading = false;
                state.error = action.payload.error ?? null;
                log.debug(`getAllCategories.fulfilled. Categories: ${JSON.stringify(action.payload.data)}`);
            })
            .addCase(getAllCategories.rejected, (state: CategoryState, action) => {
                log.error("getAllCategories.rejected: Failed to fetch categories", action.payload);
                state.loading = false;
                state.error = action.payload as string;
            })

            .addCase(getCategory.pending, (state: CategoryState) => {
                log.debug("getCategory.pending: Fetching category...");
                state.loading = true;
            })
            .addCase(getCategory.fulfilled, (state: CategoryState, action: PayloadAction<Response<CategoryDto>>) => {
                log.info("getCategory.fulfilled: Category fetched successfully");
                log.debug(`getCategory.fulfilled. Action: ${JSON.stringify(action)}`);
                state.selectedCategory = action.payload.data ?? null;
                state.loading = false;
                state.error = action.payload.error ?? null;
            })
            .addCase(getCategory.rejected, (state: CategoryState, action) => {
                log.error("getCategory.rejected: Failed to fetch category", action.payload);
                state.loading = false;
                state.error = action.payload as string;
            })

            .addCase(updateCategory.pending, (state: CategoryState) => {
                log.debug("updateCategory.pending: Updating category...");
                state.loading = true;
            })
            .addCase(updateCategory.fulfilled, (state: CategoryState, action: PayloadAction<Response<CategoryDto>>) => {
                log.info("updateCategory.fulfilled: Category updated successfully");
                log.debug(`updateCategory.fulfilled. Updated category: ${JSON.stringify(action.payload)}`);
                state.loading = false;
                state.error = action.payload.error ?? null;
            })
            .addCase(updateCategory.rejected, (state: CategoryState, action) => {
                log.error("updateCategory.rejected: Failed to update category", action.payload);
                state.loading = false;
                state.error = action.payload as string;
            })

            .addCase(deleteCategory.pending, (state: CategoryState) => {
                log.debug("deleteCategory.pending: Deleting category...");
                state.loading = true;
            })
            .addCase(deleteCategory.fulfilled, (state: CategoryState, action: PayloadAction<Response<string>>) => {
                log.info("deleteCategory.fulfilled: Category deleted successfully");
                state.loading = false;
                state.error = action.payload.error ?? null;
                log.debug(`deleteCategory.fulfilled. Action: ${JSON.stringify(action)}`);
            })
            .addCase(deleteCategory.rejected, (state: CategoryState, action) => {
                log.error("deleteCategory.rejected: Failed to delete category", action.payload);
                state.loading = false;
                state.error = action.payload as string;
            });
    }
});

// Export the actions generated by createSlice
export const {
    setFilterPage,
    setFilterSize,
    setFilterCategoryName,
    setFilterDirection,
    setSelectedCategory
} = categoriesSlice.actions;

// Export the reducer to be used in the store
export default categoriesSlice.reducer;
