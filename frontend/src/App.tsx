import React, {useEffect} from "react";
import {CssBaseline} from "@mui/material";
import Layout from "./segments/Layout";
import {useNavigate} from "react-router-dom";
import {useAppSelector} from "./store/hooks.ts";
import {getDateFromSeconds} from "./api/client/utils.ts";

/**
 * App component that serves as the root component of the application.
 * It applies the Material-UI baseline CSS to standardize styles across browsers
 * and renders the main layout of the application.
 *
 * @returns {JSX.Element} The rendered App component.
 */
const App: React.FC = (): React.JSX.Element => {
    const navigate = useNavigate();
    const {userTimeExp} = useAppSelector((state) => state.globals);

    const checkSession = () => {
        if (!userTimeExp) {
            return false;
        }

        const now = new Date();
        const expiryDate = getDateFromSeconds(userTimeExp);

        return now < expiryDate;
    };

    useEffect(() => {
        if (!checkSession()) {
            navigate("/login");
        }
    }, [navigate]);

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
