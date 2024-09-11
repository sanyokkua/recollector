import React from "react";
import {CssBaseline} from "@mui/material";
import Layout from "./segments/Layout";

/**
 * App component that serves as the root component of the application.
 * It applies the Material-UI baseline CSS to standardize styles across browsers
 * and renders the main layout of the application.
 *
 * @returns {JSX.Element} The rendered App component.
 */
const App: React.FC = (): React.JSX.Element => {
    return (
        <>
            {/* CssBaseline component to set up a consistent baseline for styling */}
            <CssBaseline/>

            {/* Main layout component that contains the structure of the app */}
            <Layout/>
        </>
    );
};

export default App;
