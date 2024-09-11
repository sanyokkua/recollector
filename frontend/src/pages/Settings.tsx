import {FC, useEffect} from "react";
import {useAppDispatch} from "../store/hooks.ts";
import {appBarSetCustomState} from "../store/features/appBar/appBarSlice.ts";


const Settings: FC = () => {
    const dispatch = useAppDispatch();
    useEffect(() => {
        dispatch(appBarSetCustomState("Settings"));
    });
    return <p>Settings page</p>;
};

export default Settings;