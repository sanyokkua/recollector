import {FC} from "react";
import {AppBar, IconButton, Toolbar, Typography} from "@mui/material";
import MenuIcon from "@mui/icons-material/Menu";
import {Link} from "react-router-dom";
import {useAppDispatch, useAppSelector} from "../store/hooks";
import {drawerSwitchOn} from "../store/features/drawer/drawerSlice";
import {getDateFromSeconds} from "../api/client/utils";


const RecollectorAppBar: FC = () => {
    const dispatch = useAppDispatch();
    const header = useAppSelector((state) => state.appBarHeader.value);
    const {userIsLoggedIn, userEmail, userTimeExp} = useAppSelector((state) => state.globals);

    const handleMenuClick = () => {
        dispatch(drawerSwitchOn());
    };

    const renderRightInfo = () => {
        if (userIsLoggedIn && userEmail) {
            const sessionEndTime = getDateFromSeconds(userTimeExp)?.toLocaleTimeString();
            return `You are logged in as: ${userEmail}, Session Ends: ${sessionEndTime}`;
        }
        return <Link to={"/login"}>Login</Link>;
    };

    return (
        <AppBar position="static">
            <Toolbar>
                <IconButton size="large" edge="start" color="inherit" aria-label="menu" sx={{mr: 2}}
                            onClick={handleMenuClick}>
                    <MenuIcon/>
                </IconButton>
                <Typography variant="h6" component="div" sx={{flexGrow: 1}}>
                    {header}
                </Typography>
                {renderRightInfo()}
            </Toolbar>
        </AppBar>
    );
};

export default RecollectorAppBar;
