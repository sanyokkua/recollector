import AccountBoxIcon                                                                       from "@mui/icons-material/AccountBox";
import AppRegistrationIcon
                                                                                            from "@mui/icons-material/AppRegistration";
import ClassIcon
                                                                                            from "@mui/icons-material/Class";
import LoginIcon
                                                                                            from "@mui/icons-material/Login";
import RestoreIcon
                                                                                            from "@mui/icons-material/Restore";
import SettingsIcon
                                                                                            from "@mui/icons-material/Settings";
import WavingHandIcon
                                                                                            from "@mui/icons-material/WavingHand";
import { Box, Divider, Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText } from "@mui/material";
import React, { JSX }                                                                       from "react";
import { Link }                                                                             from "react-router-dom";
import {
    drawerSwitchOff
}                                                                                           from "../store/features/drawer/drawerSlice.ts";
import { useAppDispatch, useAppSelector }                                                   from "../store/hooks.ts";


type DrawerItem = {
    path: string;
    text: string;
    icon: JSX.Element
}

const RecollectorAppDrawer: React.FC = () => {
    const drawerToggled = useAppSelector(state => state.drawerToggled.value);
    const dispatch = useAppDispatch();

    const { userIsLoggedIn } = useAppSelector((state) => state.globals);

    const drawerItems: DrawerItem[] = [];
    const drawerItemsSettings: DrawerItem[] = [];

    drawerItems.push({ path: "/", text: "Welcome", icon: <WavingHandIcon/> });

    if (userIsLoggedIn) {
        drawerItems.push({
                             path: "/dashboard",
                             text: "Categories",
                             icon: <ClassIcon/>
                         });
        drawerItemsSettings.push({
                                     path: "/profile",
                                     text: "Profile",
                                     icon: <AccountBoxIcon/>
                                 });
        drawerItemsSettings.push({
                                     path: "/settings",
                                     text: "Settings",
                                     icon: <SettingsIcon/>
                                 });
    } else {
        drawerItems.push({
                             path: "/login",
                             text: "Login",
                             icon: <LoginIcon/>
                         });
        drawerItems.push({
                             path: "/register",
                             text: "Register",
                             icon: <AppRegistrationIcon/>
                         });
        drawerItems.push({
                             path: "/restore",
                             text: "Restore Password",
                             icon: <RestoreIcon/>
                         });
    }

    const items = drawerItems.map(item => <ListItem key={ item.path } disablePadding>
        <ListItemButton component={ Link } to={ item.path }>
            <ListItemIcon>
                { item.icon }
            </ListItemIcon>
            <ListItemText>
                { item.text }
            </ListItemText>
        </ListItemButton>
    </ListItem>);
    const settings = drawerItemsSettings.map(item => <ListItem key={ item.path } disablePadding>
        <ListItemButton component={ Link } to={ item.path }>
            <ListItemIcon>
                { item.icon }
            </ListItemIcon>
            <ListItemText>
                { item.text }
            </ListItemText>
        </ListItemButton>
    </ListItem>);
    const DrawerList = <Box sx={ { width: 250 } } role="presentation" onClick={ () => dispatch(drawerSwitchOff()) }>
        <List>
            { items }
        </List>
        <Divider/>
        <List>
            { settings }
        </List>
    </Box>;

    return <Drawer open={ drawerToggled } onClose={ () => dispatch(drawerSwitchOff()) }>
        { DrawerList }
    </Drawer>;
};

export default RecollectorAppDrawer;