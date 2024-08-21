import {FC, useEffect} from "react";
import {Button, FormControl, TextField} from "@mui/material";
import {useAppDispatch} from "../store/hooks.ts";
import {appBarSetCustomState} from "../store/features/appBar/appBarSlice.ts";


const Login: FC = () => {
    const dispatch = useAppDispatch();
    useEffect(() => {
        dispatch(appBarSetCustomState("Login"))
    });
    return (
        <>
            <h1>Login to existing Account</h1>

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

            <Button variant="contained" color={"primary"}>Login</Button>
            <Button variant="text" color={"error"}>Forgot Password??</Button>
            <Button variant="text" color={"success"}>New User?</Button>
        </>
    );
}

export default Login;