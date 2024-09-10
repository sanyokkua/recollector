import React from "react";
import {AppBar, IconButton, Toolbar, Typography} from "@mui/material";
import MenuIcon from "@mui/icons-material/Menu";
import {drawerSwitchOn} from "../store/features/drawer/drawerSlice.ts";
import {useAppDispatch, useAppSelector} from "../store/hooks.ts";
import {Link} from "react-router-dom";


const RecollectorAppBar: React.FC = () => {
    const header = useAppSelector(state => state.appBarHeader.value);
    const dispatch = useAppDispatch();

    const {
        userIsLoggedIn,
        userEmail,
        userDateTimeExp
    } = useAppSelector((state) => state.globals);

    let rightInfo = null;
    if (userIsLoggedIn && userEmail) {
        rightInfo = `You logged as: ${userEmail}, Session Ends: ${userDateTimeExp?.toLocaleTimeString()}`;
    } else {
        rightInfo = <Link to={"/login"}>Login</Link>;
    }

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

            {rightInfo}
        </Toolbar>
    </AppBar>;
};

export default RecollectorAppBar;