import {FC, useEffect, useState} from "react";
import {Alert, Box, Fab, SxProps} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import {useAppDispatch, useAppSelector} from "../store/hooks";
import {appBarSetCustomState} from "../store/features/appBar/appBarSlice";
import CategoryView, {CategoryViewMode} from "../components/CategoryView";
import {CategoryDto} from "../api/dto/categoryDto";
import {
    createCategory,
    deleteCategory,
    getAllCategories,
    setCategoryFilterCategoryName,
    setCategoryFilterPage,
    setCategorySelectedCategory,
    updateCategory
} from "../store/features/categories/categoriesSlice";
import {logger} from "../config/appConfig";
import GenericListView from "../components/GenericListView.tsx";
import {GenericListViewItem} from "../components/GenericListOfItems.tsx";
import {useNavigate} from "react-router-dom";
import {setCurrentCategoryId, setCurrentCategoryName} from "../store/features/global/globalSlice.ts";

const log = logger.getLogger("DashboardCategories");


// Styles
const containerStyle: SxProps = {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "flex-start",
    height: "100vh",
    padding: 1,
    // Centering and adjusting the width based on the screen size
    width: "100%", // Full width on small screens
    maxWidth: "800px", // Set a max width for larger screens
    margin: "0 auto", // Center it horizontally on larger screens
    "@media (min-width: 768px)": {
        // For tablets and larger devices
        maxWidth: "80%" // Takes 80% of the width for larger screens
    },
    backgroundColor: "#e1f5fe"
};
const fabStyle: SxProps = {
    position: "fixed",
    bottom: 16,
    right: 16
};

// Helper Functions
const mapCategoryDtoToGenericItem = (item: CategoryDto): GenericListViewItem => {
    const addText = `Todo: ${item.todoItems ?? 0}. In Progress: ${(item.inProgressItems ?? 0)}. Finished: ${(item.finishedItems ?? 0)}`;
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
    const {userJwtToken} = useAppSelector((state) => state.globals);

    useEffect(() => {
        log.debug("Component mounted, fetching categories");
        dispatch(appBarSetCustomState("Categories"));
        dispatch(getAllCategories({filter: filter, jwtToken: userJwtToken}));
    }, [dispatch, filter]);

    const [open, setOpen] = useState<boolean>(false);
    const [mode, setMode] = useState<CategoryViewMode>("view");

    const items: Array<GenericListViewItem> = allCategories.map(mapCategoryDtoToGenericItem);

    const selectItem = (categoryId: number | null | undefined) => {
        const chosenCat = allCategories.find((c) => c.categoryId === categoryId) || null;
        if (!chosenCat || !chosenCat.categoryId || !chosenCat.categoryName) {
            throw new Error("Selected category doesn't have ID or Name");
        }

        dispatch(setCategorySelectedCategory(chosenCat));
        dispatch(setCurrentCategoryId(chosenCat.categoryId));
        dispatch(setCurrentCategoryName(chosenCat.categoryName));
    };

    // Handlers
    const handleClose = () => setOpen(false);
    const handleViewSave = async (updatedCategory: CategoryDto) => {
        log.debug(`Saving category in ${mode} mode`, updatedCategory);
        try {
            if (mode === "create") {
                await dispatch(createCategory({categoryDto: updatedCategory, jwtToken: userJwtToken})).unwrap();
                log.info("Category created successfully");
            } else if (mode === "edit" && selectedCategory) {
                await dispatch(updateCategory({
                    id: selectedCategory.categoryId ?? -1,
                    categoryDto: updatedCategory,
                    jwtToken: userJwtToken
                })).unwrap();
                log.info(`Category ${selectedCategory.categoryId} updated successfully`);
            }
            handleClose();
            dispatch(getAllCategories({filter: filter, jwtToken: userJwtToken}));
        } catch (error) {
            log.error(`Failed to save category in ${mode} mode:`, error);
        }
    };
    const handleViewDelete = async (categoryDto: CategoryDto) => {
        log.debug(`Deleting category in ${mode} mode`, categoryDto);
        try {
            await dispatch(deleteCategory({id: categoryDto?.categoryId ?? -1, jwtToken: userJwtToken})).unwrap();
            log.info("Category deleted successfully");
            handleClose();
            dispatch(getAllCategories({filter: filter, jwtToken: userJwtToken}));
        } catch (error) {
            log.error(`Failed to delete category in ${mode} mode:`, error);
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

    return <Box sx={{containerStyle}}>

        <CategoryView mode={mode} open={open} category={selectedCategory}
                      onClose={handleClose}
                      onSave={handleViewSave}
                      onDelete={handleViewDelete}/>

        {error && <Alert severity="warning">{error}</Alert>}

        <GenericListView currentPage={currentPage}
                         totalPages={totalPages}
                         totalItems={totalItems}
                         listOfItems={items}
                         isLoading={loading}
                         searchBarText={filter?.categoryName ?? ""}
                         onItemClicked={handleItemClick}
                         onItemEditClicked={handleEditButtonClick}
                         onPaginationItemClicked={handlePageChange}
                         onSearchTextChanged={handleSearchChange}
                         backgroundColor={"#f9fbe7"}
                         itemsBackgroundColor={"#f0f4c3"}
        />

        <Fab color="success" aria-label="add" sx={fabStyle} onClick={handleAddButtonClick}>
            <AddIcon/>
        </Fab>
    </Box>;
};

export default DashboardCategories;
