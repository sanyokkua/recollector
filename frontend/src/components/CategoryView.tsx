import {CategoryDto} from "../api/dto/categoryDto.ts";
import React, {FC, useState} from 'react';
import {
    Box,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Grid,
    TextField,
    Typography,
} from '@mui/material';

export type CategoryViewMode = 'view' | 'edit' | 'create';

interface CategoryViewProps {
    category?: CategoryDto | null;
    open: boolean;
    onClose: () => void;
    onSave: (updatedCategory: CategoryDto) => void;
    mode: CategoryViewMode; // Mode prop to control view behavior
}

const CategoryView: FC<CategoryViewProps> = ({category, open, onClose, onSave, mode}) => {
    if (!category) {
        category = {};
    }
    const [editableCategory, setEditableCategory] = useState<CategoryDto>(category);

    // Handle changes to the category name
    const handleNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setEditableCategory({...editableCategory, categoryName: event.target.value});
    };

    // Handle save or create action
    const handleSave = () => {
        onSave(editableCategory);
        onClose();
    };

    // Determine if the name field should be editable
    const isNameEditable = mode === 'edit' || mode === 'create';
    // Determine the title of the dialog based on the mode
    const dialogTitle = mode === 'view' ? 'View Category' : mode === 'edit' ? 'Edit Category' : 'Create New Category';
    // Determine the action button label based on the mode
    const actionButtonLabel = mode === 'create' ? 'Create' : 'Save';

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle>{dialogTitle}</DialogTitle>
            <DialogContent>
                <Box component="form" noValidate autoComplete="off" sx={{mt: 2}}>
                    <Grid container spacing={2}>
                        <Grid item xs={12}>
                            <TextField
                                label="Category Name"
                                value={editableCategory.categoryName || ''}
                                onChange={handleNameChange}
                                fullWidth
                                variant="outlined"
                                InputProps={{readOnly: !isNameEditable}}
                            />
                        </Grid>
                        {mode !== 'create' && (
                            <>
                                <Grid item xs={12}>
                                    <Typography variant="body1">
                                        Todo Items: {editableCategory.todoItems ?? 0}
                                    </Typography>
                                </Grid>
                                <Grid item xs={12}>
                                    <Typography variant="body1">
                                        In Progress Items: {editableCategory.inProgressItems ?? 0}
                                    </Typography>
                                </Grid>
                                <Grid item xs={12}>
                                    <Typography variant="body1">
                                        Finished Items: {editableCategory.finishedItems ?? 0}
                                    </Typography>
                                </Grid>
                            </>
                        )}
                    </Grid>
                </Box>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose} color="secondary">
                    Cancel
                </Button>
                {mode !== 'view' && (
                    <Button onClick={handleSave} color="primary" variant="contained">
                        {actionButtonLabel}
                    </Button>
                )}
            </DialogActions>
        </Dialog>
    );
};

export default CategoryView;
