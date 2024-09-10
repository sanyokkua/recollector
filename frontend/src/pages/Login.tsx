import {FC, useEffect, useState} from "react";
import {Box, Button, FormControl, Snackbar, TextField, Typography} from "@mui/material";
import {useAppDispatch} from "../store/hooks";
import {appBarSetCustomState} from "../store/features/appBar/appBarSlice";
import {Controller, useForm} from "react-hook-form";
import * as yup from "yup";
import {yupResolver} from "@hookform/resolvers/yup";
import {authApiClient} from "../api/index.ts";
import {Link, useNavigate} from "react-router-dom";
import Alert from "@mui/material/Alert";
import {jwtTokenSaver} from "../store/browserStore.ts";
import {jwtDecode, JwtPayload} from "jwt-decode";
import {setUserEmail, setUserIsLoggedIn, setUserTimeExp} from "../store/features/global/globalSlice.ts";

// Define validation schema with Yup
const schema = yup.object({
    email: yup.string().email("Invalid email address").required("Email is required"),
    password: yup.string().required("Password is required")
});

interface FormValues {
    email: string;
    password: string;
}

const Login: FC = () => {
    const dispatch = useAppDispatch();

    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    const {control, handleSubmit, formState: {errors}} = useForm<FormValues>({
        resolver: yupResolver(schema)
    });

    useEffect(() => {
        dispatch(appBarSetCustomState("Login"));
    }, [dispatch]);

    const onSubmit = async (data: FormValues) => {
        try {
            const result = await authApiClient.loginUser({
                email: data.email,
                password: data.password
            });

            if (!result || !result.data || !result.data.jwtToken) {
                throw new Error("Token is not found in the response!");
            }

            const decoded = jwtDecode<JwtPayload>(result.data.jwtToken);
            if (!decoded || !decoded.sub || !decoded.exp) {
                throw new Error("Token is not valid!");
            }

            dispatch(setUserEmail(decoded.sub));
            dispatch(setUserIsLoggedIn(Boolean(decoded)));
            dispatch(setUserTimeExp(decoded.exp));
            jwtTokenSaver(result.data.jwtToken);

            navigate("/dashboard"); // Redirect to a protected route, e.g., dashboard
        } catch (error: any) {
            setErrorMessage(error.response?.data?.message || "Login failed. Please try again.");
        }
    };

    return (
        <Box
            display="flex"
            flexDirection="column"
            alignItems="center"
            justifyContent="center"
            minHeight="100vh"
            p={2}
        >
            <Typography variant="h4" gutterBottom>
                Login to Existing Account
            </Typography>

            <Box
                display="flex"
                flexDirection="column"
                alignItems="center"
                width="100%"
                maxWidth="400px"
            >
                <FormControl fullWidth margin="normal">
                    <Controller
                        name="email"
                        control={control}
                        render={({field}) => (
                            <TextField
                                {...field}
                                id="email-input"
                                label="Email"
                                variant="outlined"
                                autoComplete="email"
                                error={!!errors.email}
                                helperText={errors.email?.message}
                            />
                        )}
                    />
                </FormControl>

                <FormControl fullWidth margin="normal">
                    <Controller
                        name="password"
                        control={control}
                        render={({field}) => (
                            <TextField
                                {...field}
                                id="password-input"
                                label="Password"
                                type="password"
                                autoComplete="current-password"
                                variant="outlined"
                                error={!!errors.password}
                                helperText={errors.password?.message}
                            />
                        )}
                    />
                </FormControl>

                <Button
                    variant="contained"
                    color="primary"
                    fullWidth
                    onClick={handleSubmit(onSubmit)}
                >
                    Login
                </Button>

                <Button component={Link} to={"/restore"} variant="text" color="error" fullWidth
                        style={{marginTop: "16px"}}>
                    Forgot Password?
                </Button>

                <Button component={Link} to={"/register"} variant="text" color="success" fullWidth
                        style={{marginTop: "8px"}}>
                    New User?
                </Button>
            </Box>

            <Snackbar open={!!errorMessage} autoHideDuration={6000} onClose={() => setErrorMessage(null)}>
                <Alert onClose={() => setErrorMessage(null)} severity="error">
                    {errorMessage}
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default Login;
