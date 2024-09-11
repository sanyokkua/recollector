import {Navigate, Outlet} from "react-router-dom";
import {useAppSelector} from "../store/hooks.ts"; // adjust the path to your store definition

const PrivateRoute = () => {
    const {userIsLoggedIn} = useAppSelector((state) => state.globals);

    // Redirect to login page if not authenticated
    return userIsLoggedIn ? <Outlet/> : <Navigate to="/"/>;
};

export default PrivateRoute;
