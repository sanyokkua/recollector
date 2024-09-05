import React, {FC, useEffect, useState} from "react";
import {
    Box,
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
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import {useAppDispatch, useAppSelector} from "../store/hooks.ts";
import {appBarSetCustomState} from "../store/features/appBar/appBarSlice.ts";
import CategoryView, {CategoryViewMode} from "../components/CategoryView.tsx";
import {CategoryDto} from "../api/dto/categoryDto.ts";
import {
    createCategory,
    fetchCategories,
    setCategoryName,
    setPage,
    setSelectedCategory
} from "../store/features/categories/categoriesSlice.ts";

// Styles
const containerStyle: SxProps = {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    height: '100vh',
    width: '100%',
    padding: 2
};

const fabStyle: SxProps = {
    position: 'fixed',
    bottom: 16,
    right: 16,
};

const DashboardCategories: FC = () => {
    const dispatch = useAppDispatch();
    // @ts-ignore
    const {filter, allCategories, selectedCategory, loading, error} = useAppSelector((state) => state.categories);

    const [open, setOpen] = useState<boolean>(false);
    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);

    const [mode, setMode] = useState<CategoryViewMode>("view"); // 'view' | 'edit' | 'create'
    // @ts-ignore
    const setModeView = () => setMode("view");
    // @ts-ignore
    const setModeEdit = () => setMode("edit");
    const setModeCreate = () => setMode("create");

    const handleSave = (updatedCategory: CategoryDto) => {
        switch (mode) {
            case "create": {
                try {
                    // Dispatch the createCategory action and wait for it to complete
                    dispatch(createCategory(updatedCategory)).unwrap(); // unwrap to catch any errors

                    // Dispatch fetchCategories to get the latest data from the server
                    dispatch(fetchCategories(filter)); // This ensures the state remains in sync with the server
                } catch (error) {
                    console.error('Failed to create category:', error);
                }
                break;
            }
            case "edit": {
                break;
            }
            case "view": {
                break;
            }
        }
    };

    useEffect(() => {
        dispatch(appBarSetCustomState("Categories"));
        fetchCategories(filter);
    }, [dispatch]);

    const handlePageChange = (event: React.ChangeEvent<unknown>, value: number) => {
        dispatch(setPage(value));
        console.log(event);
    };

    const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        dispatch(setCategoryName(event.target.value));
    };

    const handleAddButtonClick = () => {
        console.log('Add button clicked');
        setModeCreate();
        handleOpen();
    };

    const handleEditButtonClick = (categoryId: number | undefined) => {
        console.log(`Edit button clicked for category ${categoryId}`);
        const chosenCat = allCategories.find(c => c.categoryId === categoryId);
        dispatch(setSelectedCategory(chosenCat ?? null))
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
            <TextField
                id="category-search"
                label="Search"
                type="search"
                variant="outlined"
                onChange={handleSearchChange}
                sx={{mb: 2}}
            />
            <Typography sx={{mb: 2}} variant="h6" component="div" align="center">
                Categories
            </Typography>
            <List dense={false}>
                {allCategories.map(cat =>
                    <ListItem
                        disablePadding
                        secondaryAction={
                            <IconButton edge="end" aria-label="edit"
                                        onClick={() => handleEditButtonClick(cat.categoryId)}>
                                <EditIcon/>
                            </IconButton>
                        }
                    >
                        <ListItemButton>
                            <ListItemText
                                primary={cat.categoryName}
                                secondary={`${cat.todoItems ?? 0}/${((cat.finishedItems ?? 0) + (cat.inProgressItems ?? 0))}`}
                            />
                        </ListItemButton>
                    </ListItem>
                )}
            </List>
            <Pagination
                count={filter.size}
                color="primary"
                page={filter.page}
                onChange={handlePageChange}
                sx={{mt: 2}}
            />
            <Fab
                color="success"
                aria-label="add"
                sx={fabStyle}
                onClick={handleAddButtonClick}
            >
                <AddIcon/>
            </Fab>
        </Box>
    );
}

export default DashboardCategories;
