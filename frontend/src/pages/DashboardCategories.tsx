import React, {FC, useCallback, useEffect, useState} from "react";
import {
    Alert,
    Box,
    CircularProgress,
    Fab,
    IconButton,
    List,
    ListItem,
    ListItemButton,
    ListItemText,
    Pagination,
    SxProps,
    TextField,
    Typography
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";
import {useAppDispatch, useAppSelector} from "../store/hooks";
import {appBarSetCustomState} from "../store/features/appBar/appBarSlice";
import CategoryView, {CategoryViewMode} from "../components/CategoryView";
import {CategoryDto} from "../api/dto/categoryDto";
import {
    createCategory,
    getAllCategories,
    setFilterCategoryName,
    setFilterPage,
    setSelectedCategory,
    updateCategory
} from "../store/features/categories/categoriesSlice";
import {logger} from "../config/appConfig";

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

    const [open, setOpen] = useState<boolean>(false);
    const [mode, setMode] = useState<CategoryViewMode>("view"); // 'view' | 'edit' | 'create'

    const showPagination = totalPages > 1;

    useEffect(() => {
        log.debug("Component mounted, fetching categories");
        dispatch(appBarSetCustomState("Categories"));
        dispatch(getAllCategories(filter));
    }, [dispatch, filter]);

    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);

    const setModeView = useCallback(() => setMode("view"), []);
    const setModeEdit = useCallback(() => setMode("edit"), []);
    const setModeCreate = useCallback(() => setMode("create"), []);

    const handleSave = async (updatedCategory: CategoryDto) => {
        log.debug(`Saving category in ${mode} mode`, updatedCategory);

        try {
            if (mode === "create") {
                await dispatch(createCategory(updatedCategory)).unwrap();
                log.info("Category created successfully");
            } else if (mode === "edit" && selectedCategory) {
                await dispatch(
                    updateCategory({
                        id: selectedCategory.categoryId ?? -1,
                        categoryDto: updatedCategory
                    })
                ).unwrap();
                log.info(`Category ${selectedCategory.categoryId} updated successfully`);
            }

            // Refresh categories after save
            dispatch(getAllCategories(filter));
            handleClose();
        } catch (error) {
            log.error(`Failed to save category in ${mode} mode:`, error);
        }
    };

    const handlePageChange = (event: React.ChangeEvent<unknown>, value: number) => {
        log.debug("Page changed to", value, event);
        dispatch(setFilterPage(value));
        dispatch(getAllCategories(filter));
    };

    const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const searchValue = event.target.value;
        log.debug("Search input changed:", searchValue);
        dispatch(setFilterCategoryName(searchValue));
        dispatch(getAllCategories(filter));
    };

    const handleAddButtonClick = () => {
        log.debug("Add button clicked");
        setModeCreate();
        handleOpen();
    };

    const handleItemClick = (categoryId?: number) => {
        log.debug("Category item clicked:", categoryId);
        const chosenCat = allCategories.find((c) => c.categoryId === categoryId) || null;
        dispatch(setSelectedCategory(chosenCat));
        setModeView();
        handleOpen();
    };

    const handleEditButtonClick = (event: React.MouseEvent, categoryId?: number) => {
        event.stopPropagation(); // Prevent the click from reaching the list item
        log.debug("Edit button clicked for category:", categoryId);
        const chosenCat = allCategories.find((c) => c.categoryId === categoryId) || null;
        dispatch(setSelectedCategory(chosenCat));
        setModeEdit();
        handleOpen();
    };

    return (
        <Box sx={containerStyle}>
            <CategoryView
                category={selectedCategory}
                open={open}
                onClose={handleClose}
                onSave={handleSave}
                mode={mode}
            />

            {error && <Alert severity="warning">{error}</Alert>}

            {loading ? (
                <CircularProgress/>
            ) : (
                <>
                    <TextField
                        id="category-search"
                        label="Search"
                        type="search"
                        variant="outlined"
                        onChange={handleSearchChange}
                        sx={{mb: 2}}
                    />
                    <Typography sx={{mb: 2}} variant="h6" component="div" align="center">
                        Categories. Total: {totalItems}
                    </Typography>
                    <List dense={false}>
                        {allCategories.map((cat) => (
                            <ListItem
                                key={cat.categoryId}
                                disablePadding
                                onClick={() => handleItemClick(cat.categoryId)}
                                secondaryAction={
                                    <IconButton
                                        edge="end"
                                        aria-label="edit"
                                        onClick={(event) => handleEditButtonClick(event, cat.categoryId)}
                                    >
                                        <EditIcon/>
                                    </IconButton>
                                }
                            >
                                <ListItemButton>
                                    <ListItemText
                                        primary={cat.categoryName}
                                        secondary={`${cat.todoItems ?? 0}/${
                                            (cat.finishedItems ?? 0) + (cat.inProgressItems ?? 0)
                                        }`}
                                    />
                                </ListItemButton>
                            </ListItem>
                        ))}
                    </List>

                    {showPagination && (
                        <Pagination
                            count={totalPages}
                            color="primary"
                            page={currentPage}
                            onChange={handlePageChange}
                            sx={{mt: 2}}
                        />
                    )}
                </>
            )}

            <Fab color="success" aria-label="add" sx={fabStyle} onClick={handleAddButtonClick}>
                <AddIcon/>
            </Fab>
        </Box>
    );
};

export default DashboardCategories;
