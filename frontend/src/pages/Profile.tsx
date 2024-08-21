import {FC, useEffect} from "react";
import {useAppDispatch} from "../store/hooks.ts";
import {appBarSetCustomState} from "../store/features/appBar/appBarSlice.ts";


const Profile: FC = () => {
    const dispatch = useAppDispatch();
    useEffect(() => {
        dispatch(appBarSetCustomState("Profile"))
    });
    return <p>Profile Page</p>;
}

export default Profile;