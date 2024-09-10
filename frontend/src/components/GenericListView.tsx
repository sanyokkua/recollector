import React, {FC, useCallback} from "react";
import {Box, SxProps, TextField, Typography} from "@mui/material";
import {logger} from "../config/appConfig";
import GenericListOfItems, {GenericListViewProps} from "./GenericListOfItems.tsx";

const log = logger.getLogger("GenericItemListView");

const boxStyle: SxProps = {
    display: "flex",
    flexDirection: "column",
    padding: 5,
    height: "100vh",
    backgroundColor: "#f9fbe7"
};

export interface GenericItemListViewProps extends GenericListViewProps {
    header: string;
    searchBarText: string;
    itemsBackgroundColor?: string;
    onSearchTextChanged: (searchText?: string | null) => void;
}

const GenericListView: FC<GenericItemListViewProps> = ({
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
                                                           onSearchTextChanged,
                                                           backgroundColor = boxStyle.backgroundColor,
                                                           itemsBackgroundColor
                                                       }) => {
    log.debug(`Header: ${header}, Current Page: ${currentPage}, Total Pages: ${totalPages}, Total Items: ${totalItems}`);
    boxStyle.backgroundColor = backgroundColor;
    // Handlers
    const handleSearchChange = useCallback(
        (event: React.ChangeEvent<HTMLInputElement>) => {
            const searchValue = event.target.value;
            log.debug(`handleSearchChange, value: ${searchValue}`);
            onSearchTextChanged(searchValue);
        }, [onSearchTextChanged]
    );

    return (

        <Box sx={boxStyle}>
            <Typography sx={{mb: 2}} variant="h6" component="div" align="center">{header}</Typography>

            <TextField sx={{mb: 2}} id="item-search" label="Search" type="search" variant="outlined"
                       value={searchBarText}
                       onChange={handleSearchChange}/>

            <GenericListOfItems isLoading={isLoading}
                                backgroundColor={itemsBackgroundColor}

                                currentPage={currentPage}
                                totalPages={totalPages}
                                totalItems={totalItems}
                                listOfItems={listOfItems}

                                onItemClicked={onItemClicked}
                                onItemEditClicked={onItemEditClicked}
                                onPaginationItemClicked={onPaginationItemClicked}
            />
        </Box>

    );
};

export default GenericListView;
