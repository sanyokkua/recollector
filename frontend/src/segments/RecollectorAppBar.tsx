import React from "react";
import {AppBar, IconButton, Toolbar, Typography} from "@mui/material";
import MenuIcon from "@mui/icons-material/Menu";
import {drawerSwitchOn} from "../store/features/drawer/drawerSlice.ts";
import {useAppDispatch, useAppSelector} from "../store/hooks.ts";


const RecollectorAppBar: React.FC = () => {
    const header = useAppSelector(state => state.appBarHeader.value)
    const dispatch = useAppDispatch();

    return <AppBar position="static">
        <Toolbar>
            <IconButton
                size="large"
                edge="start"
                color="inherit"
                aria-label="menu"
                sx={{mr: 2}} onClick={() => dispatch(drawerSwitchOn())}>
                <MenuIcon/>
            </IconButton>

            <Typography variant="h6" component="div" sx={{flexGrow: 1}}>
                {header}
            </Typography>
        </Toolbar>
    </AppBar>
}

export default RecollectorAppBar;