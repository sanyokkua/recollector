import React, {FC, useEffect} from "react";
import {
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
import {useAppDispatch} from "../store/hooks.ts";
import {appBarSetCustomState} from "../store/features/appBar/appBarSlice.ts";

const fabStyle: SxProps = {
    position: 'absolute',
    bottom: 16,
    right: 16,
};

function generate(element: React.ReactElement) {
    return [0, 1, 2].map((value) =>
        React.cloneElement(element, {
            key: value,
        }),
    );
}

const DashboardItems: FC = () => {
    const dispatch = useAppDispatch();
    useEffect(() => {
        dispatch(appBarSetCustomState("[Category] - Items"))
    });
    return (
        <>
            <TextField id="category-search" label="Search" type="search"/>
            <Typography sx={{mt: 4, mb: 2}} variant="h6" component="div" align={"center"}>
                [Category] - Items
            </Typography>
            <List dense={false}>
                {generate(
                    <ListItem disablePadding key={"fff"}
                              secondaryAction={
                                  <IconButton edge="end" aria-label="delete">
                                      <EditIcon/>
                                  </IconButton>
                              }>
                        <ListItemButton>
                            <ListItemText
                                primary="Single-line item"
                                secondary={'Secondary text'}
                            />
                        </ListItemButton>
                    </ListItem>
                )}
            </List>
            <Pagination count={10} color="primary"/>
            <Fab color="success" aria-label="add" sx={fabStyle}>
                <AddIcon/>
            </Fab>
        </>
    );
}

export default DashboardItems;