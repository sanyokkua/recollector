import React, {JSX} from "react";
import {Box, Divider, Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText} from "@mui/material";
import WavingHandIcon from '@mui/icons-material/WavingHand';
import LoginIcon from '@mui/icons-material/Login';
import AppRegistrationIcon from '@mui/icons-material/AppRegistration';
import RestoreIcon from '@mui/icons-material/Restore';
import ClassIcon from '@mui/icons-material/Class';
import ChecklistRtlIcon from '@mui/icons-material/ChecklistRtl';
import AccountBoxIcon from '@mui/icons-material/AccountBox';
import SettingsIcon from '@mui/icons-material/Settings';
import {drawerSwitchOff} from "../store/features/drawer/drawerSlice.ts";
import {useAppDispatch, useAppSelector} from "../store/hooks.ts";

type DrawerItem = {
    path: string;
    text: string;
    icon: JSX.Element
}

const drawerItems: DrawerItem[] = [
    {
        path: "/",
        text: "Welcome",
        icon: <WavingHandIcon/>,
    },
    {
        path: "/login",
        text: "Login",
        icon: <LoginIcon/>,
    },
    {
        path: "/register",
        text: "Register",
        icon: <AppRegistrationIcon/>,
    },
    {
        path: "/restore",
        text: "Restore Password",
        icon: <RestoreIcon/>,
    },
    {
        path: "/dashboard",
        text: "Categories",
        icon: <ClassIcon/>,
    },
    {
        path: "/dashboard/items",
        text: "Items",
        icon: <ChecklistRtlIcon/>,
    },
];

const drawerItemsSettings: DrawerItem[] = [
    {
        path: "/profile",
        text: "Profile",
        icon: <AccountBoxIcon/>,
    },
    {
        path: "/settings",
        text: "Settings",
        icon: <SettingsIcon/>,
    }
]


const RecollectorAppDrawer: React.FC = () => {
    const drawerToggled = useAppSelector(state => state.drawerToggled.value)
    const dispatch = useAppDispatch();

    const items = drawerItems.map(item => <ListItem key={item.path} disablePadding>
        <ListItemButton href={item.path}>
            <ListItemIcon>
                {item.icon}
            </ListItemIcon>
            <ListItemText>
                {item.text}
            </ListItemText>
        </ListItemButton>
    </ListItem>);
    const settings = drawerItemsSettings.map(item => <ListItem key={item.path} disablePadding>
        <ListItemButton href={item.path}>
            <ListItemIcon>
                {item.icon}
            </ListItemIcon>
            <ListItemText>
                {item.text}
            </ListItemText>
        </ListItemButton>
    </ListItem>);
    const DrawerList = <Box sx={{width: 250}} role="presentation" onClick={() => dispatch(drawerSwitchOff())}>
        <List>
            {items}
        </List>
        <Divider/>
        <List>
            {settings}
        </List>
    </Box>;

    return <Drawer open={drawerToggled} onClose={() => dispatch(drawerSwitchOff())}>
        {DrawerList}
    </Drawer>
}

export default RecollectorAppDrawer;