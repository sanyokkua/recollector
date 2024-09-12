import React, {FC, useCallback} from "react";
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
} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import {logger} from "../config/appConfig";

const log = logger.getLogger("GenericListView");

const boxStyle: SxProps = {
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
    log.debug(`Current Page: ${currentPage}, Total Pages: ${totalPages}, Total Items: ${totalItems}`);
    const showPagination = totalPages > 1;

    // Handlers
    const handleItemClick = useCallback((itemId?: number) => (event: React.MouseEvent) => {
            event.stopPropagation();
            log.debug(`handleItemClick, itemId: ${itemId}`);
            onItemClicked(itemId);
        },
        [onItemClicked]
    );
    const handleItemEditClick = useCallback((itemId?: number) => (event: React.MouseEvent) => {
            event.stopPropagation();
            log.debug(`Edit clicked, itemId: ${itemId}`);
            onItemEditClicked(itemId);
        },
        [onItemEditClicked]
    );
    const handlePageChange = useCallback((event: React.ChangeEvent<unknown>, value: number) => {
            event.stopPropagation();
            log.debug(`Page changed, value: ${value}`);
            onPaginationItemClicked(value);
        },
        [onPaginationItemClicked]
    );

    // UI components
    const renderIconButton = (item: GenericListViewItem) => {
        return <IconButton edge="end" aria-label="edit" onClick={handleItemEditClick(item.itemId)}>
            <EditIcon/>
        </IconButton>;
    };
    const renderListItem = (item: GenericListViewItem) => {
        return <>
            <Paper elevation={3} sx={{backgroundColor: itemColor}}>
                <ListItem key={item.itemId} disablePadding onClick={handleItemClick(item.itemId)}
                          secondaryAction={renderIconButton(item)}>
                    <ListItemButton>
                        <ListItemText primary={item?.itemName ?? ""} secondary={item?.itemAdditionalText ?? ""}/>
                    </ListItemButton>
                </ListItem>
            </Paper>
            <Divider sx={{padding: 0.3}}/>
        </>;
    };

    const pagination = <Box sx={{display: "flex", justifyContent: "center", mt: 2}}>
        <Pagination count={totalPages} page={currentPage} color="primary" onChange={handlePageChange}/>
    </Box>;
    const loadingView = <CircularProgress/>;
    const noContent = <Box sx={{width: "100hv"}}>
        <Skeleton/>
        <Skeleton animation="wave"/>
        <Skeleton animation={false}/>
    </Box>;
    const contentList = <List dense={false}>{listOfItems.map(renderListItem)}</List>;
    const contentView = <>
        {listOfItems && listOfItems.length > 0 ? contentList : noContent}
        {showPagination && pagination}
    </>;
    const currentView = isLoading ? loadingView : contentView;

    return <Box sx={boxStyle}>
        {currentView}
    </Box>;
};

export default GenericListOfItems;
