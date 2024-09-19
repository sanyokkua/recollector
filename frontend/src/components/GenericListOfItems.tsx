import EditIcon                   from "@mui/icons-material/Edit";
import {
    Box,
    CircularProgress,
    Divider,
    IconButton,
    List,
    ListItem,
    ListItemButton,
    ListItemText,
    Pagination,
    Paper,
    Skeleton,
    SxProps
}                                 from "@mui/material";
import React, { FC, useCallback } from "react";
import { logger }                 from "../config/appConfig";


const log = logger.getLogger("GenericListView");

const boxStyle: SxProps = {
    display: "flex",
    flexDirection: "column",
    height: "100vh",
    padding: 1
};

export interface GenericListViewItem {
    itemId?: number;
    itemName?: string;
    itemAdditionalText?: string;
}

export interface GenericListViewProps {
    currentPage?: number;
    totalPages?: number;
    totalItems?: number;
    listOfItems?: Array<GenericListViewItem>;
    isLoading?: boolean;
    itemColor: string;
    onItemClicked: (itemId: number | null | undefined) => void;
    onItemEditClicked: (itemId: number | null | undefined) => void;
    onPaginationItemClicked: (pageNumber: number | null | undefined) => void;
}

const GenericListOfItems: FC<GenericListViewProps> = ({
                                                          currentPage = 0,
                                                          totalPages = 0,
                                                          totalItems = 0,
                                                          listOfItems = [],
                                                          isLoading = false,
                                                          onItemClicked,
                                                          onItemEditClicked,
                                                          onPaginationItemClicked,
                                                          itemColor
                                                      }) => {
    log.debug(`Current Page: ${ currentPage }, Total Pages: ${ totalPages }, Total Items: ${ totalItems }`);

    const showPagination = totalPages > 1;

    // Handlers
    const handleItemClick = useCallback((itemId?: number) => (event: React.MouseEvent) => {
        event.stopPropagation();
        log.debug(`handleItemClick, itemId: ${ itemId }`);
        onItemClicked(itemId);
    }, [onItemClicked]);

    const handleItemEditClick = useCallback((itemId?: number) => (event: React.MouseEvent) => {
        event.stopPropagation();
        log.debug(`Edit clicked, itemId: ${ itemId }`);
        onItemEditClicked(itemId);
    }, [onItemEditClicked]);

    const handlePageChange = useCallback((event: React.ChangeEvent<unknown>, value: number) => {
        event.stopPropagation();
        log.debug(`Page changed, value: ${ value }`);
        onPaginationItemClicked(value);
    }, [onPaginationItemClicked]);

    // UI components
    const renderIconButton = (itemId?: number) => (
        <IconButton edge="end" aria-label="edit" onClick={ handleItemEditClick(itemId) } sx={ { marginRight: 3 } }>
            <EditIcon/>
        </IconButton>
    );

    const renderListItem = (item: GenericListViewItem) => (
        <React.Fragment key={ item.itemId }>
            <Paper elevation={ 3 } sx={ { backgroundColor: itemColor } }>
                <ListItem disablePadding onClick={ handleItemClick(item.itemId) }>
                    <ListItemButton>
                        <ListItemText primary={ item.itemName ?? "" } secondary={ item.itemAdditionalText ?? "" }/>
                    </ListItemButton>
                    { renderIconButton(item.itemId) }
                </ListItem>
            </Paper>
            <Divider sx={ { padding: 0.3 } }/>
        </React.Fragment>
    );

    const pagination = (
        <Box sx={ { display: "flex", justifyContent: "center", mt: 2 } }>
            <Pagination count={ totalPages } page={ currentPage } color="primary" onChange={ handlePageChange }/>
        </Box>
    );

    const loadingView = <CircularProgress/>;

    const noContent = (
        <Box sx={ { width: "100%" } }>
            <Skeleton/>
            <Skeleton animation="wave"/>
            <Skeleton animation={ false }/>
        </Box>
    );

    const contentList = <List dense={ false }>{ listOfItems.map(renderListItem) }</List>;

    const contentView = (
        <>
            { listOfItems.length > 0 ? contentList : noContent }
            { showPagination && pagination }
        </>
    );

    return <Box sx={ boxStyle }>{ isLoading ? loadingView : contentView }</Box>;
};

export default GenericListOfItems;
