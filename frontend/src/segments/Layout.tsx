import React from 'react';
import RecollectorAppBar from './RecollectorAppBar';
import RecollectorAppDrawer from './RecollectorAppDrawer';
import {Box} from '@mui/material';
import {Outlet} from 'react-router-dom';

/**
 * Layout component that defines the overall structure of the application.
 * It includes the AppBar at the top, the Drawer for navigation, and a placeholder (Outlet) for rendering matched routes.
 *
 * @returns {JSX.Element} The rendered Layout component.
 */
const Layout: React.FC = (): React.JSX.Element => {
    return (
        <Box>
            {/* AppBar component for the top section of the app */}
            <RecollectorAppBar/>

            {/* Drawer component for the side navigation */}
            <RecollectorAppDrawer/>

            {/* Outlet for rendering child routes */}
            <Outlet/>
        </Box>
    );
};

export default Layout;
