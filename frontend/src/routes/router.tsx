import { createBrowserRouter } from "react-router-dom";
import App                     from "../App.tsx";
import ChangePassword          from "../pages/ChangePassword.tsx";
import DashboardCategories     from "../pages/DashboardCategories.tsx";
import DashboardItems          from "../pages/DashboardItems.tsx";
import ErrorPage               from "../pages/ErrorPage.tsx";
import Login                   from "../pages/Login.tsx";
import Profile                 from "../pages/Profile.tsx";
import Registration            from "../pages/Registration.tsx";
import RestorePassword         from "../pages/RestorePassword.tsx";
import Settings                from "../pages/Settings.tsx";
import Welcome                 from "../pages/Welcome.tsx";
import PrivateRoute            from "./PrivateRoute.tsx";


const router = createBrowserRouter([
                                       {
                                           path: "/",
                                           element: <App/>,
                                           errorElement: <ErrorPage/>,
                                           children: [
                                               {
                                                   index: true,
                                                   element: <Welcome/>,
                                                   errorElement: <ErrorPage/>
                                               },
                                               {
                                                   path: "/login",
                                                   element: <Login/>,
                                                   errorElement: <ErrorPage/>
                                               },
                                               {
                                                   path: "/register",
                                                   element: <Registration/>,
                                                   errorElement: <ErrorPage/>
                                               },
                                               {
                                                   path: "/restore",
                                                   element: <RestorePassword/>,
                                                   errorElement: <ErrorPage/>
                                               },
                                               {
                                                   path: "/dashboard",
                                                   element: <PrivateRoute/>,
                                                   children: [
                                                       {
                                                           index: true,
                                                           element: <DashboardCategories/>,
                                                           errorElement: <ErrorPage/>
                                                       },
                                                       {
                                                           path: "/dashboard/items",
                                                           element: <DashboardItems/>,
                                                           errorElement: <ErrorPage/>
                                                       }
                                                   ]
                                               },
                                               {
                                                   path: "/profile",
                                                   element: <PrivateRoute/>,
                                                   children: [
                                                       {
                                                           index: true,
                                                           element: <Profile/>,
                                                           errorElement: <ErrorPage/>
                                                       }
                                                   ]
                                               },
                                               {
                                                   path: "/settings",
                                                   element: <PrivateRoute/>,
                                                   children: [
                                                       {
                                                           index: true,
                                                           element: <Settings/>,
                                                           errorElement: <ErrorPage/>
                                                       }
                                                   ]
                                               },
                                               {
                                                   path: "/change_password",
                                                   element: <PrivateRoute/>,
                                                   children: [
                                                       {
                                                           index: true,
                                                           element: <ChangePassword/>,
                                                           errorElement: <ErrorPage/>
                                                       }
                                                   ]
                                               }
                                           ]
                                       }
                                   ]);

export default router;