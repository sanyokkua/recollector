import { yupResolver }                                               from "@hookform/resolvers/yup";
import { Box, Button, FormControl, Snackbar, TextField, Typography } from "@mui/material";
import Alert                                                         from "@mui/material/Alert";
import { FC, useEffect, useState }                                   from "react";
import { Controller, useForm }                                       from "react-hook-form";
import { Link, useNavigate }                                         from "react-router-dom";
import * as yup                                                      from "yup";
import { parseErrorMessage }                                         from "../api/client/utils";
import { logger }                                                    from "../config/appConfig.ts";
import { appBarSetCustomState }                                      from "../store/features/appBar/appBarSlice";
import { loginUser }                                                 from "../store/features/global/globalSlice.ts";
import { useAppDispatch }                                            from "../store/hooks";


const log = logger.getLogger("Login");

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
    const navigate = useNavigate();
    const dispatch = useAppDispatch();

    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    const { control, handleSubmit, formState: { errors } } = useForm<FormValues>({
                                                                                     resolver: yupResolver(schema)
                                                                                 });

    useEffect(() => {
        dispatch(appBarSetCustomState("Login"));
    }, [dispatch]);

    const onSubmit = async (data: FormValues) => {
        try {
            await dispatch(loginUser({
                                         email: data.email,
                                         password: data.password
                                     })).unwrap();

            log.info("User logged in successfully");
            navigate("/dashboard");
        } catch (error: any) {
            const errorMessage = parseErrorMessage(error, "Login failed. Please try again.");
            setErrorMessage(errorMessage);
        }
    };

    return (
        <Box
            display="flex"
            flexDirection="column"
            alignItems="center"
            justifyContent="center"
            minHeight="100vh"
            p={ 2 }
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
                        control={ control }
                        render={ ({ field }) => (
                            <TextField
                                { ...field }
                                id="email-input"
                                label="Email"
                                variant="outlined"
                                autoComplete="email"
                                error={ !!errors.email }
                                helperText={ errors.email?.message }
                            />
                        ) }
                    />
                </FormControl>

                <FormControl fullWidth margin="normal">
                    <Controller
                        name="password"
                        control={ control }
                        render={ ({ field }) => (
                            <TextField
                                { ...field }
                                id="password-input"
                                label="Password"
                                type="password"
                                autoComplete="current-password"
                                variant="outlined"
                                error={ !!errors.password }
                                helperText={ errors.password?.message }
                            />
                        ) }
                    />
                </FormControl>

                <Button
                    variant="contained"
                    color="primary"
                    fullWidth
                    onClick={ handleSubmit(onSubmit) }
                >
                    Login
                </Button>

                <Button component={ Link } to={ "/restore" } variant="text" color="error" fullWidth
                        style={ { marginTop: "16px" } }>
                    Forgot Password?
                </Button>

                <Button component={ Link } to={ "/register" } variant="text" color="success" fullWidth
                        style={ { marginTop: "8px" } }>
                    New User?
                </Button>
            </Box>

            <Snackbar open={ !!errorMessage } autoHideDuration={ 6000 } onClose={ () => setErrorMessage(null) }>
                <Alert onClose={ () => setErrorMessage(null) } severity="error">
                    { errorMessage }
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default Login;
