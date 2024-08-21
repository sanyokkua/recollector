import {createBrowserRouter} from "react-router-dom";
import App from "../App.tsx";
import ErrorPage from "../pages/ErrorPage.tsx";
import Welcome from "../pages/Welcome.tsx";
import Login from "../pages/Login.tsx";
import Registration from "../pages/Registration.tsx";
import RestorePassword from "../pages/RestorePassword.tsx";
import DashboardCategories from "../pages/DashboardCategories.tsx";
import DashboardItems from "../pages/DashboardItems.tsx";
import Profile from "../pages/Profile.tsx";
import Settings from "../pages/Settings.tsx";

const router = createBrowserRouter([
    {
        path: "/",
        element: <App/>,
        errorElement: <ErrorPage/>,
        children: [
            {
                index: true,
                element: <Welcome/>,
                errorElement: <ErrorPage/>,
            },
            {
                path: "/login",
                element: <Login/>,
                errorElement: <ErrorPage/>,
            },
            {
                path: "/register",
                element: <Registration/>,
                errorElement: <ErrorPage/>,
            },
            {
                path: "/restore",
                element: <RestorePassword/>,
                errorElement: <ErrorPage/>,
            },
            {
                path: "/dashboard",
                element: <DashboardCategories/>,
                errorElement: <ErrorPage/>,
            },
            {
                path: "/dashboard/items",
                element: <DashboardItems/>,
                errorElement: <ErrorPage/>,
            },
            {
                path: "/profile",
                element: <Profile/>,
                errorElement: <ErrorPage/>,
            },
            {
                path: "/settings",
                element: <Settings/>,
                errorElement: <ErrorPage/>,
            },
        ],
    },
]);

export default router;