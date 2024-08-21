import {FC, useEffect} from "react";
import {Button, FormControl, TextField} from "@mui/material";
import {useAppDispatch} from "../store/hooks.ts";
import {appBarSetCustomState} from "../store/features/appBar/appBarSlice.ts";


const Registration: FC = () => {
    const dispatch = useAppDispatch();
    useEffect(() => {
        dispatch(appBarSetCustomState("Registration"))
    });
    return (
        <>
            <h1>Create New Account</h1>

            <FormControl>
                <TextField
                    id="email-input"
                    label="Email"
                    variant="standard"
                    autoComplete="email"
                />
            </FormControl>
            <FormControl>
                <TextField
                    id="password-input"
                    label="Password"
                    type="password"
                    autoComplete="current-password"
                    variant="filled"
                />
            </FormControl>
            <FormControl>
                <TextField
                    id="confirm-password-input"
                    label="Confirm Password"
                    type="password"
                    autoComplete="current-password"
                    variant="filled"
                />

                <Button variant="contained" color={"success"}>Register</Button>
            </FormControl>
            <Button variant="text" color={"secondary"}>Already has account?</Button>
        </>
    );
}

export default Registration;