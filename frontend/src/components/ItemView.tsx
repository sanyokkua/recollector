import React, {FC, useCallback, useEffect, useState} from "react";
import {
    Box,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Grid,
    MenuItem,
    TextField,
    Typography
} from "@mui/material";
import {ItemDto, ItemDtoItemStatusEnum} from "../api/dto/itemDto.ts";
import {logger} from "../config/appConfig.ts";
import {useAppDispatch, useAppSelector} from "../store/hooks.ts";
import {getItemStatuses} from "../store/features/helper/helperSlice.ts";

const log = logger.getLogger("ItemView");

interface ViewConfig {
    dialogTitle: string;
    actionButtonLabel: string;
    isNameEditable: boolean;
    isStatusEditable: boolean;
    isNoteEditable: boolean;
}

export type ItemViewMode = "view" | "edit" | "create";

function getViewConfig(mode: ItemViewMode): ViewConfig {
    switch (mode) {
        case "create":
            return {
                dialogTitle: "Create New Item",
                actionButtonLabel: "Create",
                isNameEditable: true,
                isStatusEditable: true,
                isNoteEditable: true
            };
        case "edit":
            return {
                dialogTitle: "Edit Item",
                actionButtonLabel: "Save",
                isNameEditable: true,
                isStatusEditable: true,
                isNoteEditable: true
            };
        default:
            return {
                dialogTitle: "View Item",
                actionButtonLabel: "",
                isNameEditable: false,
                isStatusEditable: false,
                isNoteEditable: false
            };
    }
}

interface ItemViewProps {
    mode: ItemViewMode;
    item?: ItemDto | null;
    categoryId: number;
    open: boolean;
    onClose: () => void;
    onSave: (updatedItem: ItemDto) => void;
    onDelete: (deleteItem: ItemDto) => void;
}

const ItemView: FC<ItemViewProps> = React.memo(({item, open, onClose, onSave, onDelete, mode, categoryId}) => {
        log.debug(`View is created in mode: ${mode}`);
        log.debug(`Item received: ${JSON.stringify(item)}`);
    const dispatch = useAppDispatch();

    const {userJwtToken} = useAppSelector((state) => state.globals);
    const {statuses} = useAppSelector((state) => state.helper);

    useEffect(() => {
        log.debug("Component mounted, fetching statuses");
        dispatch(getItemStatuses(userJwtToken));
    }, [dispatch]);

        // Initial state of item
        const initialItemState: ItemDto = {
            categoryId: categoryId,
            itemName: "",
            itemStatus: ItemDtoItemStatusEnum.TODO_LATER,
            itemNotes: ""
        };

        const [itemToProcess, setItemToProcess] = useState<ItemDto>(
            mode === "create" ? initialItemState : {...initialItemState, ...item}
        );

        const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);

        useEffect(() => {
            if (mode === "create") {
                setItemToProcess(initialItemState);
            } else if (item) {
                setItemToProcess({...initialItemState, ...item});
            }
        }, [item, mode]);

    const {dialogTitle, actionButtonLabel, isNameEditable, isStatusEditable, isNoteEditable} =
            getViewConfig(mode);

        const handleNameTextChanged = useCallback(
            (event: React.ChangeEvent<HTMLInputElement>) => {
                setItemToProcess((prev) => ({
                    ...prev,
                    itemName: event.target.value
                }));
            },
            []
        );

        const handleStatusChange = useCallback(
            (event: React.ChangeEvent<HTMLInputElement>) => {
                setItemToProcess((prev) => ({
                    ...prev,
                    itemStatus: event.target.value as ItemDtoItemStatusEnum
                }));
            },
            []
        );

        const handleNotesChange = useCallback(
            (event: React.ChangeEvent<HTMLInputElement>) => {
                setItemToProcess((prev) => ({
                    ...prev,
                    itemNotes: event.target.value
                }));
            },
            []
        );

        const handleOnSaveClicked = useCallback(() => {
            if (itemToProcess) {
                onSave(itemToProcess);
            }
        }, [itemToProcess, onSave]);

        const handleOnCloseClicked = useCallback(() => {
            onClose();
        }, [onClose]);

        const handleDeleteClick = useCallback(() => {
            setShowDeleteConfirmation(true);
        }, []);

        const handleConfirmDelete = useCallback(() => {
            setShowDeleteConfirmation(false);
            onDelete(itemToProcess);
        }, [itemToProcess, onDelete]);

        const handleCancelDelete = useCallback(() => {
            setShowDeleteConfirmation(false);
        }, []);

    const mappedStatuses = statuses.map(status => {
        return <MenuItem key={status} value={status}>{status}</MenuItem>;
    });

    // Render item data depending on mode
    const renderItemData = () => {
        if (mode === "view") {
            // Display item details as plain text for view mode
            return (
                <Box sx={{mt: 2}}>
                    <Typography variant="subtitle1"><strong>Item Name:</strong></Typography>
                    <Typography>{itemToProcess.itemName ?? "-"}</Typography>

                    <Typography variant="subtitle1" sx={{mt: 2}}><strong>Item Status:</strong></Typography>
                    <Typography>{itemToProcess.itemStatus}</Typography>

                    <Typography variant="subtitle1" sx={{mt: 2}}><strong>Item Notes:</strong></Typography>
                    <Typography>{itemToProcess.itemNotes ?? "-"}</Typography>
                </Box>
            );
        }

        // Edit or create mode: show form fields
        return (
            <Box component="form" noValidate autoComplete="off" sx={{mt: 2}}>
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <TextField
                            fullWidth
                            variant="outlined"
                            label="Item Name"
                            value={itemToProcess.itemName ?? ""}
                            onChange={handleNameTextChanged}
                            InputProps={{readOnly: !isNameEditable}}
                        />
                    </Grid>
                    <Grid item xs={12}>
                        <TextField
                            select
                            fullWidth
                            variant="outlined"
                            label="Item Status"
                            value={itemToProcess.itemStatus ?? ""}
                            onChange={handleStatusChange}
                            InputProps={{readOnly: !isStatusEditable}}
                        >
                            {mappedStatuses}
                        </TextField>
                    </Grid>
                    <Grid item xs={12}>
                        <TextField
                            fullWidth
                            variant="outlined"
                            label="Item Notes"
                            multiline
                            rows={4}
                            value={itemToProcess.itemNotes ?? ""}
                            onChange={handleNotesChange}
                            InputProps={{readOnly: !isNoteEditable}}
                        />
                    </Grid>
                </Grid>
            </Box>
        );
    };

    return (
        <>
            <Dialog open={open} onClose={handleOnCloseClicked} maxWidth="sm" fullWidth>
                <DialogTitle>{dialogTitle}</DialogTitle>

                <DialogContent>{renderItemData()}</DialogContent>

                    <DialogActions>
                        <Grid container justifyContent="space-between" alignItems="center">
                            {mode === "edit" && (
                                <Grid item>
                                    <Button onClick={handleDeleteClick} color="error" variant="outlined">
                                        Delete
                                    </Button>
                                </Grid>
                            )}
                            <Grid item xs>
                                <Grid container justifyContent={"flex-end"} spacing={1}>
                                    <Grid item>
                                        <Button onClick={handleOnCloseClicked} color="secondary">
                                            Cancel
                                        </Button>
                                    </Grid>
                                    {mode !== "view" && (
                                        <Grid item>
                                            <Button onClick={handleOnSaveClicked} color="primary" variant="contained">
                                                {actionButtonLabel}
                                            </Button>
                                        </Grid>
                                    )}
                                </Grid>
                            </Grid>
                        </Grid>
                    </DialogActions>
                </Dialog>

                <Dialog open={showDeleteConfirmation} onClose={handleCancelDelete} maxWidth="xs">
                    <DialogTitle>Confirm Delete</DialogTitle>

                    <DialogContent>
                        <Typography>Are you sure you want to delete this item?</Typography>
                    </DialogContent>

                    <DialogActions>
                        <Button onClick={handleCancelDelete} color="secondary">
                            Cancel
                        </Button>
                        <Button onClick={handleConfirmDelete} color="error" variant="contained">
                            Delete
                        </Button>
                    </DialogActions>
                </Dialog>
            </>
        );
    }
);

export default ItemView;
