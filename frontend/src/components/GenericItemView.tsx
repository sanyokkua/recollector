import React, {FC, useCallback} from "react";
import {
    Box,
    CircularProgress,
    IconButton,
    List,
    ListItem,
    ListItemButton,
    ListItemText,
    Pagination,
    TextField,
    Typography
} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import {logger} from "../config/appConfig";

const log = logger.getLogger("GenericItemView");


export interface GenericViewItem {
    itemId?: number;
    itemName?: string;
    itemAdditionalText?: string;
}

export interface GenericItemViewProps {
    header: string;

    currentPage?: number;
    totalPages?: number;
    totalItems?: number;

    searchBarText: string;

    listOfItems?: Array<GenericViewItem>;
    isLoading?: boolean;

    onItemClicked: (itemId: number | null | undefined) => void;
    onItemEditClicked: (itemId: number | null | undefined) => void;
    onPaginationItemClicked: (pageNumber: number | null | undefined) => void;
    onSearchTextChanged: (searchText?: string | null) => void;
}

const GenericItemView: FC<GenericItemViewProps> = ({
                                                       header = "",
                                                       currentPage = 0,
                                                       totalPages = 0,
                                                       totalItems = 0,
                                                       listOfItems = [],
                                                       isLoading = false,
                                                       searchBarText = "",
                                                       onItemClicked,
                                                       onItemEditClicked,
                                                       onPaginationItemClicked,
                                                       onSearchTextChanged
                                                   }) => {
    log.debug(`Header: ${header}, Current Page: ${currentPage}, Total Pages: ${totalPages}, Total Items: ${totalItems}`);
    const showSearchAndPagination = totalPages > 1;

    // Handlers
    const handleItemClick = useCallback(
        (itemId?: number) => (event: React.MouseEvent) => {
            event.stopPropagation();
            log.debug(`handleItemClick, itemId: ${itemId}`);
            onItemClicked(itemId);
        },
        [onItemClicked]
    );
    const handleItemEditClick = useCallback(
        (itemId?: number) => (event: React.MouseEvent) => {
            event.stopPropagation();
            log.debug(`Edit clicked, itemId: ${itemId}`);
            onItemEditClicked(itemId);
        },
        [onItemEditClicked]
    );
    const handleSearchChange = useCallback(
        (event: React.ChangeEvent<HTMLInputElement>) => {
            const searchValue = event.target.value;
            log.debug(`handleSearchChange, value: ${searchValue}`);
            onSearchTextChanged(searchValue);
        }, [onSearchTextChanged]
    );
    const handlePageChange = useCallback(
        (event: React.ChangeEvent<unknown>, value: number) => {
            event.stopPropagation();
            log.debug(`Page changed, value: ${value}`);
            onPaginationItemClicked(value);
        }, [onPaginationItemClicked]
    );

    // UI components
    const renderIconButton = (item: GenericViewItem) => {
        return <IconButton edge="end" aria-label="edit" onClick={handleItemEditClick(item.itemId)}>
            <EditIcon/>
        </IconButton>;
    };
    const renderListItem = (item: GenericViewItem) => {
        return <ListItem key={item.itemId} disablePadding onClick={handleItemClick(item.itemId)}
                         secondaryAction={renderIconButton(item)}>
            <ListItemButton>
                <ListItemText primary={item?.itemName ?? ""} secondary={item?.itemAdditionalText ?? ""}/>
            </ListItemButton>
        </ListItem>;
    };
    const searchBar = <TextField sx={{mb: 2}} id="item-search" label="Search" type="search" variant="outlined"
                                 value={searchBarText}
                                 onChange={handleSearchChange}/>;
    const pagination = <Pagination sx={{mt: 2}} count={totalPages} page={currentPage} color="primary"
                                   onChange={handlePageChange}/>;
    const loadingView = <CircularProgress/>;
    const contentView = <>
        <Typography sx={{mb: 2}} variant="h6" component="div" align="center">{header}</Typography>
        {searchBar}
        <List dense={false}>{listOfItems.map(renderListItem)}</List>
        {showSearchAndPagination && pagination}
    </>;
    const currentView = isLoading ? loadingView : contentView;

    return (
        <Box>
            {currentView}
        </Box>
    );
};

export default GenericItemView;
