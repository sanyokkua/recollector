import {FC, useCallback, useEffect, useState} from "react";
import {Box, Fab, SxProps} from "@mui/material";
import {useAppDispatch, useAppSelector} from "../store/hooks.ts";
import {appBarSetCustomState} from "../store/features/appBar/appBarSlice.ts";
import {GenericListViewItem} from "../components/GenericListOfItems.tsx";
import {logger} from "../config/appConfig.ts";
import {ItemDto} from "../api/dto/itemDto.ts";
import {useParams} from "react-router-dom";
import {CategoryViewMode} from "../components/CategoryView.tsx";
import {
    createItem,
    deleteItem,
    getAllItems,
    ItemCreateRequest,
    ItemGetRequest,
    ItemUpdateRequest,
    setFilterCategoryId,
    setFilterItemName,
    setFilterPage,
    setSelectedItem,
    updateItem
} from "../store/features/items/itemsSlice.ts";
import {setSelectedCategory} from "../store/features/categories/categoriesSlice.ts";
import GenericListView from "../components/GenericListView.tsx";
import AddIcon from "@mui/icons-material/Add";
import Alert from "@mui/material/Alert";
import ItemView from "../components/ItemView.tsx";

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
const mapItemDtoToGenericItem = (item: ItemDto): GenericListViewItem => {
    const addText = `Status: ${item.itemStatus}`;
    return {
        itemId: item.itemId,
        itemName: item.itemName,
        itemAdditionalText: addText
    };
};

const DashboardItems: FC = () => {
    const dispatch = useAppDispatch();

    const {id} = useParams();
    const {
        filter,
        allItems,
        selectedItem,
        loading,
        error,
        currentPage,
        totalItems,
        totalPages
    } = useAppSelector((state) => state.items);

    const {
        currentCategoryId,
        currentCategoryName
    } = useAppSelector((state) => state.globals);

    if (!currentCategoryId || !currentCategoryName) {
        throw new Error("Category ID and Name are required");
    }

    useEffect(() => {
        log.debug("Component mounted, fetching items");
        dispatch(setFilterCategoryId(Number(id)));
        dispatch(appBarSetCustomState(`${currentCategoryName}`));
        dispatch(getAllItems(filter));
    }, [dispatch, filter, id]);

    const [open, setOpen] = useState<boolean>(false);
    const [mode, setMode] = useState<CategoryViewMode>("view"); // 'view' | 'edit' | 'create'
    const setModeView = useCallback(() => setMode("view"), []);
    const setModeEdit = useCallback(() => setMode("edit"), []);
    const setModeCreate = useCallback(() => setMode("create"), []);

    const items: Array<GenericListViewItem> = allItems.map(mapItemDtoToGenericItem);

    // Handlers
    const handleOpen = () => setOpen(true);
    const handleViewClose = () => setOpen(false);
    const handleViewSave = async (updatedItem: ItemDto) => {
        log.debug(`Saving item in ${mode} mode`, updatedItem);

        try {
            if (mode === "create") {
                const req: ItemCreateRequest = {
                    itemDto: updatedItem,
                    categoryId: Number(id)
                };
                await dispatch(createItem(req)).unwrap();
                log.info("Item created successfully");
            } else if (mode === "edit") {
                const itemUpdateReq: ItemUpdateRequest = {
                    itemId: updatedItem.itemId ?? -1,
                    categoryId: Number(id),
                    itemDto: updatedItem
                };
                await dispatch(updateItem(itemUpdateReq)).unwrap();
                log.info(`Item ${currentCategoryId} updated successfully`);
            }
            handleViewClose();
            dispatch(getAllItems(filter));
        } catch (error) {
            log.error(`Failed to save item in ${mode} mode:`, error);
        }
    };
    const handleViewDelete = async (itemDto: ItemDto) => {
        log.debug(`Deleting item in ${mode} mode`, itemDto);
        try {
            const req: ItemGetRequest = {
                categoryId: Number(id),
                itemId: itemDto.itemId ?? -1
            };
            await dispatch(deleteItem(req)).unwrap();
            log.info("Category deleted successfully");
            handleViewClose();
            dispatch(getAllItems(filter));
        } catch (error) {
            log.error(`Failed to delete category in ${mode} mode:`, error);
        }
    };
    const handleSearchChange = (searchText?: string | null) => {
        log.debug("Search input changed:", searchText);
        dispatch(setFilterItemName(searchText ?? ""));
        dispatch(getAllItems(filter));
    };
    const handlePageChange = (pageNumber: number | null | undefined) => {
        log.debug("Page changed to", pageNumber);
        dispatch(setFilterPage(pageNumber ?? 0));
        dispatch(getAllItems({
            ...filter,
            page: pageNumber ?? 0
        }));
    };
    const handleAddButtonClick = () => {
        log.debug("Add button clicked");
        dispatch(setSelectedItem(null));
        setModeCreate();
        handleOpen();
    };
    const handleItemClick = (categoryId: number | null | undefined) => {
        log.debug("Category item clicked:", categoryId);
        const chosenCat = allItems.find((c) => c.categoryId === categoryId) || null;
        dispatch(setSelectedItem(chosenCat));
        setModeView();
        handleOpen();
    };
    const handleEditButtonClick = (categoryId: number | null | undefined) => {
        log.debug("Edit button clicked for category:", categoryId);
        const chosenCat = allItems.find((c) => c.categoryId === categoryId) || null;
        dispatch(setSelectedCategory(chosenCat));
        setModeEdit();
        handleOpen();
    };

    return <Box sx={{containerStyle}}>

        <ItemView item={selectedItem} categoryId={currentCategoryId} open={open} onClose={handleViewClose}
                  onSave={handleViewSave}
                  onDelete={handleViewDelete} mode={mode}/>

        {error && <Alert severity="warning">{error}</Alert>}

        <GenericListView header={currentCategoryName}
                         currentPage={currentPage}
                         totalPages={totalPages}
                         totalItems={totalItems}
                         listOfItems={items}
                         isLoading={loading}
                         backgroundColor={"#ede7f6"}
                         itemsBackgroundColor={""}
                         searchBarText={filter?.itemName ?? "#d1c4e9"}
                         onItemClicked={handleItemClick}
                         onItemEditClicked={handleEditButtonClick}
                         onPaginationItemClicked={handlePageChange}
                         onSearchTextChanged={handleSearchChange}
        />

        <Fab color="success" aria-label="add" sx={fabStyle} onClick={handleAddButtonClick}>
            <AddIcon/>
        </Fab>

    </Box>;
};

export default DashboardItems;