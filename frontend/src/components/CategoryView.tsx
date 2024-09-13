import {
    Box,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Grid,
    TextField,
    Typography
}                                                      from "@mui/material";
import React, { FC, useCallback, useEffect, useState } from "react";
import { CategoryDto }                                 from "../api/dto/categoryDto.ts";
import { logger }                                      from "../config/appConfig.ts";


const log = logger.getLogger("CategoryView");

interface ViewConfig {
    dialogTitle: string;
    actionButtonLabel: string;
    isNameEditable: boolean;
}

export type CategoryViewMode = "view" | "edit" | "create";

function getViewConfig(mode: CategoryViewMode): ViewConfig {
    switch (mode) {
        case "create":
            return {
                dialogTitle: "Create New Category",
                actionButtonLabel: "Create",
                isNameEditable: true
            };
        case "edit":
            return {
                dialogTitle: "Edit Category",
                actionButtonLabel: "Save",
                isNameEditable: true
            };
        default:
            return {
                dialogTitle: "View Category",
                actionButtonLabel: "",
                isNameEditable: false
            };
    }
}

interface CategoryViewProps {
    mode: CategoryViewMode;
    category?: CategoryDto | null;
    open: boolean;
    onClose: () => void;
    onSave: (updatedCategory: CategoryDto) => void;
    onDelete: (deleteCategory: CategoryDto) => void;
}

const CategoryView: FC<CategoryViewProps> = React.memo(({ category, open, onClose, onSave, onDelete, mode }) => {
    log.debug("CategoryView initialized", { mode, category });

    const initialCategoryState: CategoryDto = {
        categoryName: "",
        todoItems: 0,
        inProgressItems: 0,
        finishedItems: 0
    };

    const [categoryToProcess, setCategoryToProcess] = useState<CategoryDto>(
        mode === "create" ? initialCategoryState : { ...initialCategoryState, ...category }
    );

    const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);

    useEffect(() => {
        if (mode === "create") {
            setCategoryToProcess(initialCategoryState);
        } else if (category) {
            setCategoryToProcess({ ...initialCategoryState, ...category });
        }
    }, [category, mode]);

    const { dialogTitle, actionButtonLabel, isNameEditable } = getViewConfig(mode);

    const handleNameChange = useCallback(
        (event: React.ChangeEvent<HTMLInputElement>) => {
            setCategoryToProcess(prev => ({
                ...prev,
                categoryName: event.target.value
            }));
        }, []
    );

    const handleSave = useCallback(
        () => {
            if (categoryToProcess) {
                onSave(categoryToProcess);
            }
        }, [categoryToProcess, onSave]
    );

    const handleClose = useCallback(
        () => {
            onClose();
        }, [onClose]
    );

    const handleDelete = useCallback(
        () => {
            setShowDeleteConfirmation(true);
        }, []
    );

    const handleConfirmDelete = useCallback(
        () => {
            setShowDeleteConfirmation(false);
            onDelete(categoryToProcess);
        }, [categoryToProcess, onDelete]
    );

    const handleCancelDelete = useCallback(
        () => {
            setShowDeleteConfirmation(false);
        }, []
    );

    return (
        <>
            <Dialog open={ open } onClose={ handleClose } maxWidth="sm" fullWidth>
                <DialogTitle>{ dialogTitle }</DialogTitle>
                <DialogContent>
                    <Box component="form" noValidate autoComplete="off" sx={ { mt: 2 } }>
                        <Grid container spacing={ 2 }>
                            <Grid item xs={ 12 }>
                                <TextField
                                    fullWidth
                                    variant="outlined"
                                    label="Category Name"
                                    value={ categoryToProcess.categoryName ?? "" }
                                    onChange={ handleNameChange }
                                    InputProps={ { readOnly: !isNameEditable } }
                                />
                            </Grid>
                            { mode === "view" && (
                                <>
                                    <Grid item xs={ 12 }>
                                        <Typography variant="body1">
                                            Todo Items: { categoryToProcess.todoItems ?? 0 }
                                        </Typography>
                                    </Grid>
                                    <Grid item xs={ 12 }>
                                        <Typography variant="body1">
                                            In Progress Items: { categoryToProcess.inProgressItems ?? 0 }
                                        </Typography>
                                    </Grid>
                                    <Grid item xs={ 12 }>
                                        <Typography variant="body1">
                                            Finished Items: { categoryToProcess.finishedItems ?? 0 }
                                        </Typography>
                                    </Grid>
                                </>
                            ) }
                        </Grid>
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Grid container justifyContent="space-between" alignItems="center">
                        { mode === "edit" && (
                            <Grid item>
                                <Button onClick={ handleDelete } color="error" variant="outlined">Delete</Button>
                            </Grid>
                        ) }
                        <Grid item xs>
                            <Grid container justifyContent="flex-end" spacing={ 1 }>
                                <Grid item>
                                    <Button onClick={ handleClose } color="secondary">Cancel</Button>
                                </Grid>
                                { mode !== "view" && (
                                    <Grid item>
                                        <Button onClick={ handleSave } color="primary" variant="contained">
                                            { actionButtonLabel }
                                        </Button>
                                    </Grid>
                                ) }
                            </Grid>
                        </Grid>
                    </Grid>
                </DialogActions>
            </Dialog>

            <Dialog open={ showDeleteConfirmation } onClose={ handleCancelDelete } maxWidth="xs">
                <DialogTitle>Confirm Delete</DialogTitle>
                <DialogContent>
                    <Typography>Are you sure you want to delete this category?</Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={ handleCancelDelete } color="secondary">Cancel</Button>
                    <Button onClick={ handleConfirmDelete } color="error" variant="contained">Delete</Button>
                </DialogActions>
            </Dialog>
        </>
    );
});

export default CategoryView;
