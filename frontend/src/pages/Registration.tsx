import {FC, useEffect, useState} from "react";
import {Box, Button, FormControl, Snackbar, TextField, Typography} from "@mui/material";
import {useAppDispatch} from "../store/hooks";
import {appBarSetCustomState} from "../store/features/appBar/appBarSlice";
import {Controller, useForm} from "react-hook-form";
import * as yup from "yup";
import {yupResolver} from "@hookform/resolvers/yup";
import {Link, useNavigate} from "react-router-dom";
import Alert from "@mui/material/Alert";
import {registerUser} from "../store/features/global/globalSlice.ts";
import {logger} from "../config/appConfig.ts";

const log = logger.getLogger("Registration");
// Define validation schema with Yup
const schema = yup.object({
    email: yup.string().email("Invalid email address").required("Email is required"),
    password: yup.string()
        .min(6, "Password must be at least 6 characters")
        .max(24, "Password must be up to 24 characters")
        .required("Password is required"),
    confirmPassword: yup.string()
        .oneOf([yup.ref("password")], "Passwords must match")
        .required("Confirm password is required")
});

interface FormValues {
    email: string;
    password: string;
    confirmPassword: string;
}

const Registration: FC = () => {
    const dispatch = useAppDispatch();
    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = useState<string | null>(null);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);

    const {
        control,
        handleSubmit,
        formState: {errors}
    } = useForm<FormValues>({
        resolver: yupResolver(schema)
    });

    useEffect(() => {
        dispatch(appBarSetCustomState("Registration"));
    }, [dispatch]);

    const onSubmit = async (data: FormValues) => {
        try {
            await dispatch(registerUser({
                email: data.email,
                password: data.password,
                passwordConfirm: data.confirmPassword
            })).unwrap();
            log.info("Successfully registered user");
            setSuccessMessage("Registration successful! Redirecting to login...");
            setTimeout(() => {
                navigate("/login");
            }, 2000); // 2 seconds delay before redirect
        } catch (error: any) {
            setErrorMessage(error.response?.data?.message || "Registration failed. Please try again.");
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
                Create New Account
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

                <FormControl fullWidth margin="normal">
                    <Controller
                        name="confirmPassword"
                        control={control}
                        render={({field}) => (
                            <TextField
                                {...field}
                                id="confirm-password-input"
                                label="Confirm Password"
                                type="password"
                                autoComplete="current-password"
                                variant="outlined"
                                error={!!errors.confirmPassword}
                                helperText={errors.confirmPassword?.message}
                            />
                        )}
                    />
                </FormControl>

                <Button
                    variant="contained"
                    color="success"
                    fullWidth
                    onClick={handleSubmit(onSubmit)}
                >
                    Register
                </Button>

                <Button component={Link} to={"/login"} variant="text" color="secondary" fullWidth
                        style={{marginTop: "16px"}}>
                    Already has an account?
                </Button>
            </Box>

            <Snackbar open={!!errorMessage} autoHideDuration={6000} onClose={() => setErrorMessage(null)}>
                <Alert onClose={() => setErrorMessage(null)} severity="error">
                    {errorMessage}
                </Alert>
            </Snackbar>

            <Snackbar open={!!successMessage} autoHideDuration={6000} onClose={() => setSuccessMessage(null)}>
                <Alert onClose={() => setSuccessMessage(null)} severity="success">
                    {successMessage}
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default Registration;
