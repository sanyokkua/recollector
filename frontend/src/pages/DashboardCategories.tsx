import AddIcon                                          from "@mui/icons-material/Add";
import { Alert, Box, Fab, Snackbar, SxProps }           from "@mui/material";
import { FC, useEffect, useState }                      from "react";
import { useNavigate }                                  from "react-router-dom";
import { CategoryDto }                                  from "../api/dto/categoryDto";
import CategoryView, { CategoryViewMode }               from "../components/CategoryView";
import { GenericListViewItem }                          from "../components/GenericListOfItems.tsx";
import GenericListView                                  from "../components/GenericListView.tsx";
import { logger }                                       from "../config/appConfig";
import { appBarSetCustomState }                         from "../store/features/appBar/appBarSlice";
import {
    createCategory,
    deleteCategory,
    getAllCategories,
    setCategoryFilterCategoryName,
    setCategoryFilterPage,
    setCategorySelectedCategory,
    updateCategory
}                                                       from "../store/features/categories/categoriesSlice";
import { setCurrentCategoryId, setCurrentCategoryName } from "../store/features/global/globalSlice.ts";
import { setItemFilterCategoryId }                      from "../store/features/items/itemsSlice.ts";
import { useAppDispatch, useAppSelector }               from "../store/hooks";


const log = logger.getLogger("DashboardCategories");

// Styles
const containerStyle: SxProps = {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "flex-start",
    height: "100vh",
    padding: 1
};
const fabStyle: SxProps = {
    position: "fixed",
    bottom: 16,
    right: 16
};

// Helper Functions
const mapCategoryDtoToGenericItem = (item: CategoryDto): GenericListViewItem => {
    const addText = `Todo: ${ item.todoItems ?? 0 }. In Progress: ${ (item.inProgressItems ?? 0) }. Finished: ${ (item.finishedItems ?? 0) }`;
    return {
        itemId: item.categoryId,
        itemName: item.categoryName,
        itemAdditionalText: addText
    };
};

const DashboardCategories: FC = () => {
    const dispatch = useAppDispatch();
    const navigate = useNavigate();

    const {
        filter,
        allCategories,
        selectedCategory,
        loading,
        error,
        currentPage,
        totalItems,
        totalPages
    } = useAppSelector((state) => state.categories);
    const { settings } = useAppSelector((state) => state.helper);

    useEffect(() => {
        log.debug("Component mounted, fetching categories");
        dispatch(appBarSetCustomState(""));
        dispatch(getAllCategories({ filter: filter }));
    }, [dispatch, filter]);

    const [open, setOpen] = useState<boolean>(false);
    const [openErr, setOpenErr] = useState<boolean>(false);
    const [mode, setMode] = useState<CategoryViewMode>("view");

    useEffect(() => {
        if (error && error.trim().length > 0) {
            setOpenErr(true);
        }
    }, [error]);

    fabStyle.backgroundColor = settings.categoryFabColor;

    const items: Array<GenericListViewItem> = allCategories.map(mapCategoryDtoToGenericItem);

    const selectItem = (categoryId: number | null | undefined) => {
        const chosenCat = allCategories.find((c) => c.categoryId === categoryId) || null;
        if (!chosenCat?.categoryId || !chosenCat?.categoryName) {
            throw new Error("Selected category doesn't have ID or Name");
        }

        dispatch(setCategorySelectedCategory(chosenCat));
        dispatch(setCurrentCategoryId(chosenCat.categoryId));
        dispatch(setCurrentCategoryName(chosenCat.categoryName));
        dispatch(setItemFilterCategoryId(chosenCat.categoryId));
    };

    // Handlers
    const handleClose = () => setOpen(false);
    const handleCloseErr = () => setOpenErr(false);
    const handleViewSave = async (updatedCategory: CategoryDto) => {
        log.debug(`Saving category in ${ mode } mode`, updatedCategory);
        try {
            if (mode === "create") {
                await dispatch(createCategory({ categoryDto: updatedCategory })).unwrap();
                log.info("Category created successfully");
            } else if (mode === "edit" && selectedCategory) {
                await dispatch(updateCategory({
                                                  id: selectedCategory.categoryId ?? -1,
                                                  categoryDto: updatedCategory
                                              })).unwrap();
                log.info(`Category ${ selectedCategory.categoryId } updated successfully`);
            }
            handleClose();
            dispatch(getAllCategories({ filter: filter }));
        } catch (error) {
            log.error(`Failed to save category in ${ mode } mode:`, error);
        }
    };
    const handleViewDelete = async (categoryDto: CategoryDto) => {
        log.debug(`Deleting category in ${ mode } mode`, categoryDto);
        try {
            await dispatch(deleteCategory({ id: categoryDto?.categoryId ?? -1 })).unwrap();
            log.info("Category deleted successfully");
            handleClose();
            dispatch(getAllCategories({ filter: filter }));
        } catch (error) {
            log.error(`Failed to delete category in ${ mode } mode:`, error);
        }
    };
    const handleAddButtonClick = () => {
        log.debug("Add button clicked");
        dispatch(setCategorySelectedCategory(null));
        setMode("create");
        setOpen(true);
    };
    const handleSearchChange = (searchText?: string | null) => {
        log.debug("Search input changed:", searchText);
        dispatch(setCategoryFilterCategoryName(searchText ?? ""));
    };
    const handlePageChange = (pageNumber: number | null | undefined) => {
        log.debug("Page changed to", pageNumber);
        dispatch(setCategoryFilterPage(pageNumber ?? 0));
    };

    const handleItemClick = (categoryId: number | null | undefined) => {
        log.debug("Category item clicked:", categoryId);
        selectItem(categoryId);
        navigate(`/dashboard/items`);
    };
    const handleEditButtonClick = (categoryId: number | null | undefined) => {
        log.debug("Edit button clicked for category:", categoryId);
        selectItem(categoryId);
        setMode("edit");
        setOpen(true);
    };

    return <Box sx={ { containerStyle } }>

        <CategoryView mode={ mode } open={ open } category={ selectedCategory }
                      onClose={ handleClose }
                      onSave={ handleViewSave }
                      onDelete={ handleViewDelete }/>

        { error && <Snackbar open={ openErr } autoHideDuration={ 6000 } onClose={ handleClose }>
            <Alert severity="warning" variant="filled" sx={ { width: "100%" } }
                   onClose={ handleCloseErr }>{ error }</Alert>
        </Snackbar> }

        <GenericListView currentPage={ currentPage }
                         totalPages={ totalPages }
                         totalItems={ totalItems }
                         listOfItems={ items }
                         isLoading={ loading }
                         searchBarText={ filter?.categoryName ?? "" }
                         onItemClicked={ handleItemClick }
                         onItemEditClicked={ handleEditButtonClick }
                         onPaginationItemClicked={ handlePageChange }
                         onSearchTextChanged={ handleSearchChange }

                         backgroundColor={ settings.categoryBackgroundColor }
                         itemColor={ settings.categoryItemColor }
        />

        <Fab aria-label="add" sx={ fabStyle } onClick={ handleAddButtonClick }>
            <AddIcon/>
        </Fab>
    </Box>;
};

export default DashboardCategories;
