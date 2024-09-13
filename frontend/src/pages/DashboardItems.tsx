import AddIcon                            from "@mui/icons-material/Add";
import { Box, Fab, SxProps }              from "@mui/material";
import Alert                              from "@mui/material/Alert";
import { FC, useEffect, useState }        from "react";
import { ItemDto }                        from "../api/dto/itemDto.ts";
import { CategoryViewMode }               from "../components/CategoryView.tsx";
import { GenericListViewItem }            from "../components/GenericListOfItems.tsx";
import GenericListView                    from "../components/GenericListView.tsx";
import ItemView                           from "../components/ItemView.tsx";
import { logger }                         from "../config/appConfig.ts";
import { appBarSetCustomState }           from "../store/features/appBar/appBarSlice.ts";
import {
    createItem,
    deleteItem,
    getAllItems,
    ItemGetRequest,
    setItemFilterItemName,
    setItemFilterPage,
    setItemSelectedItem,
    updateItem
}                                         from "../store/features/items/itemsSlice.ts";
import { useAppDispatch, useAppSelector } from "../store/hooks.ts";


const log = logger.getLogger("DashboardCategories");

// Styles
const containerStyle: SxProps = {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "flex-start",
    height: "100vh",
    padding: 1
    // Centering and adjusting the width based on the screen size
    // width: "100%", // Full width on small screens
    // maxWidth: "800px", // Set a max width for larger screens
    // margin: "0 auto", // Center it horizontally on larger screens
    // "@media (min-width: 768px)": {
    // For tablets and larger devices
    // maxWidth: "80%" // Takes 80% of the width for larger screens
    // }
};
const fabStyle: SxProps = {
    position: "fixed",
    bottom: 16,
    right: 16
};

// Helper Functions
const mapItemDtoToGenericItem = (item: ItemDto): GenericListViewItem => {
    const addText = `Status: ${ item.itemStatus }`;
    return {
        itemId: item.itemId,
        itemName: item.itemName,
        itemAdditionalText: addText
    };
};

const DashboardItems: FC = () => {
    const dispatch = useAppDispatch();

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
    const { userJwtToken } = useAppSelector((state) => state.globals);
    const { currentCategoryId, currentCategoryName } = useAppSelector((state) => state.globals);
    const { settings } = useAppSelector((state) => state.helper);

    fabStyle.backgroundColor = settings.itemFabColor;

    if (!currentCategoryId || !currentCategoryName) {
        throw new Error("Category ID and Name are required");
    }

    useEffect(() => {
        log.debug("Component mounted, fetching items");
        dispatch(appBarSetCustomState(`${ currentCategoryName }`));
        dispatch(getAllItems({ filter: filter, jwtToken: userJwtToken }));
    }, [dispatch, filter, currentCategoryId, currentCategoryName, error]);

    const [open, setOpen] = useState<boolean>(false);
    const [mode, setMode] = useState<CategoryViewMode>("view");
    const items: Array<GenericListViewItem> = allItems.map(mapItemDtoToGenericItem);

    const selectItem = (itemId: number | null | undefined) => {
        const chosenCat = allItems.find((c) => c.itemId === itemId) || null;
        dispatch(setItemSelectedItem(chosenCat));
    };

    // Handlers
    const handleViewClose = () => setOpen(false);
    const handleViewSave = async (updatedItem: ItemDto) => {
        log.debug(`Saving item in ${ mode } mode`, updatedItem);
        try {
            if (mode === "create") {
                await dispatch(createItem({
                                              itemDto: updatedItem,
                                              categoryId: currentCategoryId,
                                              jwtToken: userJwtToken
                                          })).unwrap();
                log.info("Item created successfully");
            } else if (mode === "edit") {
                await dispatch(updateItem({
                                              itemId: updatedItem.itemId ?? -1,
                                              categoryId: currentCategoryId,
                                              itemDto: updatedItem,
                                              jwtToken: userJwtToken
                                          })).unwrap();
                log.info(`Item ${ currentCategoryId } updated successfully`);
            }
            handleViewClose();
            dispatch(getAllItems({ filter: filter, jwtToken: userJwtToken }));
        } catch (error) {
            log.error(`Failed to save item in ${ mode } mode:`, error);
        }
    };
    const handleViewDelete = async (itemDto: ItemDto) => {
        log.debug(`Deleting item in ${ mode } mode`, itemDto);
        try {
            const req: ItemGetRequest = {
                categoryId: Number(currentCategoryId),
                itemId: itemDto.itemId ?? -1,
                jwtToken: userJwtToken
            };
            await dispatch(deleteItem(req)).unwrap();
            log.info("Category deleted successfully");
            handleViewClose();
            dispatch(getAllItems({ filter: filter, jwtToken: userJwtToken }));
        } catch (error) {
            log.error(`Failed to delete category in ${ mode } mode:`, error);
        }
    };
    const handleSearchChange = (searchText?: string | null) => {
        log.debug("Search input changed:", searchText);
        dispatch(setItemFilterItemName(searchText ?? ""));
    };
    const handlePageChange = (pageNumber: number | null | undefined) => {
        log.debug("Page changed to", pageNumber);
        dispatch(setItemFilterPage(pageNumber ?? 0));
    };
    const handleAddButtonClick = () => {
        log.debug("Add button clicked");
        dispatch(setItemSelectedItem(null));
        setMode("create");
        setOpen(true);
    };
    const handleItemClick = (itemId: number | null | undefined) => {
        log.debug("Item clicked id:", itemId);
        selectItem(itemId);
        setMode("view");
        setOpen(true);
    };
    const handleEditButtonClick = (itemId: number | null | undefined) => {
        log.debug("Edit button clicked for itemId:", itemId);
        selectItem(itemId);
        setMode("edit");
        setOpen(true);
    };

    return <Box sx={ { containerStyle } }>

        <ItemView mode={ mode } open={ open } categoryId={ currentCategoryId } item={ selectedItem }
                  onClose={ handleViewClose }
                  onSave={ handleViewSave }
                  onDelete={ handleViewDelete }/>

        { error && <Alert severity="warning">{ error }</Alert> }

        <GenericListView currentPage={ currentPage }
                         totalPages={ totalPages }
                         totalItems={ totalItems }
                         listOfItems={ items }
                         isLoading={ loading }
                         searchBarText={ filter?.itemName ?? "" }
                         onItemClicked={ handleItemClick }
                         onItemEditClicked={ handleEditButtonClick }
                         onPaginationItemClicked={ handlePageChange }
                         onSearchTextChanged={ handleSearchChange }

                         backgroundColor={ settings.itemBackgroundColor }
                         itemColor={ settings.itemItemColor }
        />

        <Fab color="success" aria-label="add" sx={ fabStyle } onClick={ handleAddButtonClick }>
            <AddIcon/>
        </Fab>
    </Box>;
};

export default DashboardItems;