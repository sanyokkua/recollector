import { AccountCircle }                                                   from "@mui/icons-material";
import MenuIcon                                                            from "@mui/icons-material/Menu";
import { AppBar, Button, IconButton, Menu, MenuItem, Toolbar, Typography } from "@mui/material";
import log                                                                 from "loglevel";
import React, { FC, useEffect, useState }                                  from "react";
import { Link, useNavigate }                                               from "react-router-dom";
import { getDateFromSeconds }                                              from "../api/client/utils";
import { drawerSwitchOn }                                                  from "../store/features/drawer/drawerSlice";
import { logoutUser }                                                      from "../store/features/global/globalSlice";
import {
    getSettings
}                                                                          from "../store/features/helper/helperSlice.ts";
import { useAppDispatch, useAppSelector }                                  from "../store/hooks";


const RecollectorAppBar: FC = () => {
    // Hooks
    const dispatch = useAppDispatch();
    const navigate = useNavigate();

    // Local state for managing the menu anchor element
    const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

    // Global state selectors
    const header = useAppSelector((state) => state.appBarHeader.value);
    const { userIsLoggedIn, userEmail, userTimeExp } = useAppSelector(
        (state) => state.globals
    );
    // Effect hook for logging when the component mounts
    useEffect(() => {
        log.debug("RecollectorAppBar component mounted");
        if (userIsLoggedIn) {
            dispatch(getSettings());
        }
    }, [dispatch, userIsLoggedIn]);

    // Handle drawer toggle button click
    const handleMenuClick = () => {
        dispatch(drawerSwitchOn());
    };

    // Handle "Categories" menu item click
    const handleClickOnCategories = () => {
        navigate("/dashboard");
    };

    // Open user menu
    const handleMenu = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };

    // Close user menu
    const handleClose = () => {
        setAnchorEl(null);
    };

    // Handle logout and reset global state
    const handleLogoutClose = () => {
        handleClose();
        dispatch(logoutUser({ userEmail: userEmail }));
        navigate("/login");
    };

    // Render user information or login link based on authentication status
    const renderRightInfo = () => {
        if (userIsLoggedIn && userEmail) {
            const sessionEndTime = getDateFromSeconds(userTimeExp)?.toLocaleTimeString();
            return (
                <>
                    <IconButton size="large" aria-label="account of current user" aria-controls="menu-appbar"
                                aria-haspopup="true" color="inherit"
                                onClick={ handleMenu }>
                        <AccountCircle/>
                    </IconButton>
                    <Menu id="menu-appbar" anchorEl={ anchorEl }
                          anchorOrigin={ {
                              vertical: "top",
                              horizontal: "right"
                          } }
                          transformOrigin={ {
                              vertical: "top",
                              horizontal: "right"
                          } }
                          open={ Boolean(anchorEl) }
                          onClose={ handleClose }>
                        <MenuItem>{ userEmail }</MenuItem>
                        <MenuItem>{ `Session Ends: ${ sessionEndTime }` }</MenuItem>
                        <MenuItem onClick={ handleLogoutClose }>Logout</MenuItem>
                    </Menu>
                </>
            );
        }
        return <Button component={ Link } to={ "/login" } color="inherit">Login</Button>;
    };

    return (
        <AppBar position="sticky">
            <Toolbar>
                <IconButton size="large" edge="start" color="inherit" aria-label="menu" sx={ { mr: 2 } }
                            onClick={ handleMenuClick }>
                    <MenuIcon/>
                </IconButton>

                { userIsLoggedIn && (
                    <MenuItem onClick={ handleClickOnCategories }>
                        <Typography sx={ { textAlign: "center" } }>Categories</Typography>
                    </MenuItem>
                ) }

                <Typography variant="h6" component="div" sx={ { flexGrow: 1 } }>
                    { header }
                </Typography>

                { renderRightInfo() }
            </Toolbar>
        </AppBar>
    );
};

export default RecollectorAppBar;
