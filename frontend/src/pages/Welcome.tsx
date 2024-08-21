import {Button} from "@mui/material";
import {FC, useEffect} from "react";
import {useAppDispatch} from "../store/hooks.ts";
import {appBarSetCustomState} from "../store/features/appBar/appBarSlice.ts";


const Welcome: FC = () => {
    const dispatch = useAppDispatch();
    useEffect(() => {
        dispatch(appBarSetCustomState("Welcome"))
    });
    return (
        <>
            <p>This App will help you to Learn the most used English words.</p>
            <br/>
            <Button variant="text">Register New Account</Button>
            <Button variant="contained" color={"success"}>Login</Button>
        </>
    );
}

export default Welcome;