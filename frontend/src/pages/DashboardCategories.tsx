import {FC, useCallback, useEffect, useState} from "react";
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
    setFilterCategoryName,
    setFilterPage,
    setSelectedCategory,
    updateCategory
} from "../store/features/categories/categoriesSlice";
import {logger} from "../config/appConfig";
import GenericItemView, {GenericViewItem} from "../components/GenericItemView.tsx";

const log = logger.getLogger("DashboardCategories");

// Styles
const containerStyle: SxProps = {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "flex-start",
    height: "100vh",
    width: "100%",
    padding: 1
};

const fabStyle: SxProps = {
    position: "fixed",
    bottom: 16,
    right: 16
};

const DashboardCategories: FC = () => {
    const dispatch = useAppDispatch();

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

    useEffect(() => {
        log.debug("Component mounted, fetching categories");
        dispatch(appBarSetCustomState("Categories"));
        dispatch(getAllCategories(filter));
    }, [dispatch, filter]);

    const [open, setOpen] = useState<boolean>(false);
    const [mode, setMode] = useState<CategoryViewMode>("view"); // 'view' | 'edit' | 'create'
    const setModeView = useCallback(() => setMode("view"), []);
    const setModeEdit = useCallback(() => setMode("edit"), []);
    const setModeCreate = useCallback(() => setMode("create"), []);

    const items: Array<GenericViewItem> = allCategories.map(item => {
        const addText = `${item.todoItems ?? 0}/${(item.finishedItems ?? 0) + (item.inProgressItems ?? 0)}`;
        return {
            itemId: item.categoryId,
            itemName: item.categoryName,
            itemAdditionalText: addText
        };
    });

    // Handlers
    const handleOpen = () => setOpen(true);
    const handleViewClose = () => setOpen(false);
    const handleViewSave = async (updatedCategory: CategoryDto) => {
        log.debug(`Saving category in ${mode} mode`, updatedCategory);

        try {
            if (mode === "create") {
                await dispatch(createCategory(updatedCategory)).unwrap();
                log.info("Category created successfully");
            } else if (mode === "edit" && selectedCategory) {
                await dispatch(updateCategory({
                    id: selectedCategory.categoryId ?? -1,
                    categoryDto: updatedCategory
                })).unwrap();
                log.info(`Category ${selectedCategory.categoryId} updated successfully`);
            }
            handleViewClose();
            dispatch(getAllCategories(filter));
        } catch (error) {
            log.error(`Failed to save category in ${mode} mode:`, error);
        }
    };
    const handleViewDelete = async (categoryDto: CategoryDto) => {
        log.debug(`Deleting category in ${mode} mode`, categoryDto);
        try {
            await dispatch(deleteCategory(categoryDto?.categoryId ?? -1)).unwrap();
            log.info("Category deleted successfully");
            handleViewClose();
            dispatch(getAllCategories(filter));
        } catch (error) {
            log.error(`Failed to delete category in ${mode} mode:`, error);
        }
    };

    const handleSearchChange = (searchText?: string | null) => {
        log.debug("Search input changed:", searchText);
        dispatch(setFilterCategoryName(searchText ?? ""));
        dispatch(getAllCategories(filter));
    };

    const handlePageChange = (pageNumber: number | null | undefined) => {
        log.debug("Page changed to", pageNumber);
        dispatch(setFilterPage(pageNumber ?? 0));
        dispatch(getAllCategories({
            ...filter,
            page: pageNumber ?? 0
        }));
    };

    const handleAddButtonClick = () => {
        log.debug("Add button clicked");
        dispatch(setSelectedCategory(null));
        setModeCreate();
        handleOpen();
    };

    const handleItemClick = (categoryId: number | null | undefined) => {
        log.debug("Category item clicked:", categoryId);
        const chosenCat = allCategories.find((c) => c.categoryId === categoryId) || null;
        dispatch(setSelectedCategory(chosenCat));
        setModeView();
        handleOpen();
    };

    const handleEditButtonClick = (categoryId: number | null | undefined) => {
        log.debug("Edit button clicked for category:", categoryId);
        const chosenCat = allCategories.find((c) => c.categoryId === categoryId) || null;
        dispatch(setSelectedCategory(chosenCat));
        setModeEdit();
        handleOpen();
    };

    return (
        <Box sx={containerStyle}>
            <CategoryView category={selectedCategory} open={open} onClose={handleViewClose} onSave={handleViewSave}
                          onDelete={handleViewDelete} mode={mode}/>

            {error && <Alert severity="warning">{error}</Alert>}

            <GenericItemView header={"Category"}
                             currentPage={currentPage}
                             totalPages={totalPages}
                             totalItems={totalItems}
                             listOfItems={items}
                             isLoading={loading}
                             searchBarText={filter?.categoryName ?? ""}
                             onItemClicked={handleItemClick}
                             onItemEditClicked={handleEditButtonClick}
                             onPaginationItemClicked={handlePageChange}
                             onSearchTextChanged={handleSearchChange}
            />

            <Fab color="success" aria-label="add" sx={fabStyle} onClick={handleAddButtonClick}>
                <AddIcon/>
            </Fab>
        </Box>
    );
};

export default DashboardCategories;
