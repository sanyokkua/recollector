import {FC, useEffect} from "react";
import {Button, FormControl, TextField} from "@mui/material";
import {useAppDispatch} from "../store/hooks.ts";
import {appBarSetCustomState} from "../store/features/appBar/appBarSlice.ts";


const RestorePassword: FC = () => {
    const dispatch = useAppDispatch();
    useEffect(() => {
        dispatch(appBarSetCustomState("Password Restoration"))
    });
    return (
        <>
            <h1>Forgot your password?</h1>
            <p>Enter your email below and request password restoration.</p>
            <FormControl>
                <TextField
                    id="email-input"
                    label="Email"
                    variant="standard"
                    autoComplete="email"
                />
            </FormControl>
            <Button variant="contained" color={"primary"}>Send Restoration Email</Button>
        </>
    );
}

export default RestorePassword;