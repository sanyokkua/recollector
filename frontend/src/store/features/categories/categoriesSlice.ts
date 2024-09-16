import { createAsyncThunk, createSlice, PayloadAction } from "@reduxjs/toolkit";
import CategoryApiClient                                from "../../../api/client/categoryApiClient";
import { parseErrorMessage }                            from "../../../api/client/utils";
import { CategoryDto, CategoryFilter }                  from "../../../api/dto/categoryDto";
import { FilterDirectionEnum }                          from "../../../api/dto/common";
import axiosClient, { logger }                          from "../../../config/appConfig";


const log = logger.getLogger("categorySlice");

export interface CategoryState {
    currentPage: number;
    itemsPerPage: number;
    totalItems: number;
    totalPages: number;
    filter: CategoryFilter;
    selectedCategory?: CategoryDto | null;
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

type CategoryRequest = {
    jwtToken: string;
    id?: number;
    categoryDto?: CategoryDto;
    filter?: CategoryFilter;
};

// Helper function to handle errors consistently
const handleError = (error: any, message: string) => {
    const msg = parseErrorMessage(error, message);
    log.warn(msg, error);
    return msg;
};

export const createCategory = createAsyncThunk("categories/create",
                                               async ({
                                                          jwtToken,
                                                          categoryDto
                                                      }: CategoryRequest, { rejectWithValue }) => {
                                                   log.info("Attempting to create category");
                                                   try {
                                                       const client = new CategoryApiClient(axiosClient, jwtToken);
                                                       return await client.createCategory(categoryDto!);
                                                   } catch (error: any) {
                                                       return rejectWithValue(handleError(error, "Failed to create category"));
                                                   }
                                               }
);

export const getAllCategories = createAsyncThunk("categories/fetchAll",
                                                 async ({ jwtToken, filter }: CategoryRequest, { rejectWithValue }) => {
                                                     log.info("Fetching all categories");
                                                     try {
                                                         const client = new CategoryApiClient(axiosClient, jwtToken);
                                                         return await client.getAllCategories(filter!);
                                                     } catch (error: any) {
                                                         return rejectWithValue(handleError(error, "Failed to fetch categories"));
                                                     }
                                                 }
);

export const getCategory = createAsyncThunk("categories/fetchById",
                                            async ({ jwtToken, id }: CategoryRequest, { rejectWithValue }) => {
                                                log.info(`Fetching category with id ${ id }`);
                                                try {
                                                    const client = new CategoryApiClient(axiosClient, jwtToken);
                                                    return await client.getCategory(id!);
                                                } catch (error: any) {
                                                    return rejectWithValue(handleError(error, `Failed to fetch category with id ${ id }`));
                                                }
                                            }
);

export const updateCategory = createAsyncThunk("categories/update",
                                               async ({
                                                          jwtToken,
                                                          id,
                                                          categoryDto
                                                      }: CategoryRequest, { rejectWithValue }) => {
                                                   log.info(`Updating category with id ${ id }`);
                                                   try {
                                                       const client = new CategoryApiClient(axiosClient, jwtToken);
                                                       return await client.updateCategory(id!, categoryDto!);
                                                   } catch (error: any) {
                                                       return rejectWithValue(handleError(error, `Failed to update category with id ${ id }`));
                                                   }
                                               }
);

export const deleteCategory = createAsyncThunk("categories/delete",
                                               async ({ jwtToken, id }: CategoryRequest, { rejectWithValue }) => {
                                                   log.info(`Deleting category with id ${ id }`);
                                                   try {
                                                       const client = new CategoryApiClient(axiosClient, jwtToken);
                                                       return await client.deleteCategory(id!);
                                                   } catch (error: any) {
                                                       return rejectWithValue(handleError(error, `Failed to delete category with id ${ id }`));
                                                   }
                                               }
);

export const categoriesSlice = createSlice({
                                               name: "categories",
                                               initialState,
                                               reducers: {
                                                   setCategoryFilterPage: (state, action: PayloadAction<number>) => {
                                                       state.filter.page = action.payload;
                                                       state.error = null;
                                                       log.debug(`Set filter page to ${ action.payload }`);
                                                   },
                                                   setCategoryFilterSize: (state, action: PayloadAction<number>) => {
                                                       state.filter.size = action.payload;
                                                       state.itemsPerPage = action.payload;
                                                       state.error = null;
                                                       log.debug(`Set filter size to ${ action.payload }`);
                                                   },
                                                   setCategoryFilterCategoryName: (state, action: PayloadAction<string>) => {
                                                       state.filter.categoryName = action.payload;
                                                       state.error = null;
                                                       log.debug(`Set filter category name to ${ action.payload }`);
                                                   },
                                                   setCategoryFilterDirection: (state, action: PayloadAction<FilterDirectionEnum>) => {
                                                       state.filter.direction = action.payload;
                                                       state.error = null;
                                                       log.debug(`Set filter direction to ${ action.payload }`);
                                                   },
                                                   setCategorySelectedCategory: (state, action: PayloadAction<CategoryDto | null>) => {
                                                       state.selectedCategory = action.payload;
                                                       state.error = null;
                                                       log.debug(`Selected category set to ${ JSON.stringify(action.payload) }`);
                                                   }
                                               },
                                               extraReducers: (builder) => {
                                                   builder
                                                       .addCase(createCategory.pending, (state) => {
                                                           state.loading = true;
                                                           state.error = null;
                                                           log.debug("Creating category...");
                                                       })
                                                       .addCase(createCategory.fulfilled, (state, action) => {
                                                           state.loading = false;
                                                           log.info("Category created successfully");
                                                           state.error = action.payload.error ?? null;
                                                       })
                                                       .addCase(createCategory.rejected, (state, action) => {
                                                           state.loading = false;
                                                           state.error = action.payload as string;
                                                           log.error("Failed to create category", action.payload);
                                                       })
                                                       .addCase(getAllCategories.pending, (state) => {
                                                           state.loading = true;
                                                           state.error = null;
                                                           log.debug("Fetching all categories...");
                                                       })
                                                       .addCase(getAllCategories.fulfilled, (state, action) => {
                                                           state.allCategories = action.payload.data ?? [];
                                                           state.currentPage = action.payload.meta?.pagination?.currentPage ?? 0;
                                                           state.totalPages = action.payload.meta?.pagination?.totalPages ?? 0;
                                                           state.totalItems = action.payload.meta?.pagination?.totalItems ?? 0;
                                                           state.loading = false;
                                                           log.info("Categories fetched successfully");
                                                           state.error = action.payload.error ?? null;
                                                       })
                                                       .addCase(getAllCategories.rejected, (state, action) => {
                                                           state.loading = false;
                                                           state.error = action.payload as string;
                                                           log.error("Failed to fetch categories", action.payload);
                                                       })
                                                       .addCase(getCategory.pending, (state) => {
                                                           state.loading = true;
                                                           state.error = null;
                                                           log.debug("Fetching category...");
                                                       })
                                                       .addCase(getCategory.fulfilled, (state, action) => {
                                                           state.selectedCategory = action.payload.data ?? null;
                                                           state.loading = false;
                                                           log.info("Category fetched successfully");
                                                           state.error = action.payload.error ?? null;
                                                       })
                                                       .addCase(getCategory.rejected, (state, action) => {
                                                           state.loading = false;
                                                           state.error = action.payload as string;
                                                           log.error("Failed to fetch category", action.payload);
                                                       })
                                                       .addCase(updateCategory.pending, (state) => {
                                                           state.loading = true;
                                                           state.error = null;
                                                           log.debug("Updating category...");
                                                       })
                                                       .addCase(updateCategory.fulfilled, (state, action) => {
                                                           state.loading = false;
                                                           log.info("Category updated successfully");
                                                           state.error = action.payload.error ?? null;
                                                       })
                                                       .addCase(updateCategory.rejected, (state, action) => {
                                                           state.loading = false;
                                                           state.error = action.payload as string;
                                                           log.error("Failed to update category", action.payload);
                                                       })
                                                       .addCase(deleteCategory.pending, (state) => {
                                                           state.loading = true;
                                                           state.error = null;
                                                           log.debug("Deleting category...");
                                                       })
                                                       .addCase(deleteCategory.fulfilled, (state, action) => {
                                                           state.loading = false;
                                                           log.info("Category deleted successfully");
                                                           state.error = action.payload.error ?? null;
                                                       })
                                                       .addCase(deleteCategory.rejected, (state, action) => {
                                                           state.loading = false;
                                                           state.error = action.payload as string;
                                                           log.error("Failed to delete category", action.payload);
                                                       });
                                               }
                                           });

// Export the actions generated by createSlice
export const {
    setCategoryFilterPage,
    setCategoryFilterSize,
    setCategoryFilterCategoryName,
    setCategoryFilterDirection,
    setCategorySelectedCategory
} = categoriesSlice.actions;

// Export the reducer to be used in the store
export default categoriesSlice.reducer;