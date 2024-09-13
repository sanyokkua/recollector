import { yupResolver }                                               from "@hookform/resolvers/yup";
import { Box, Button, FormControl, Snackbar, TextField, Typography } from "@mui/material";
import Alert                                                         from "@mui/material/Alert";
import { FC, useEffect, useState }                                   from "react";
import { Controller, useForm }                                       from "react-hook-form";
import * as yup                                                      from "yup";
import { logger }                                                    from "../config/appConfig";
import { appBarSetCustomState }                                      from "../store/features/appBar/appBarSlice";
import { changePassword }                                            from "../store/features/global/globalSlice"; // Assuming there's a changePassword action in your slice
import { useAppDispatch, useAppSelector }                            from "../store/hooks";


const log = logger.getLogger("ChangePassword");

// Define validation schema with Yup
const schema = yup.object({
                              currentPassword: yup
                                  .string()
                                  .required("Current password is required"),
                              newPassword: yup
                                  .string()
                                  .min(6, "New password must be at least 6 characters")
                                  .max(24, "New password must be up to 24 characters")
                                  .required("New password is required"),
                              confirmNewPassword: yup
                                  .string()
                                  .oneOf([yup.ref("newPassword")], "Passwords must match")
                                  .required("Confirm new password is required")
                          });

interface FormValues {
    currentPassword: string;
    newPassword: string;
    confirmNewPassword: string;
}

const ChangePassword: FC = () => {
    const dispatch = useAppDispatch();
    const { userJwtToken, userEmail } = useAppSelector((state) => state.globals);
    const [errorMessage, setErrorMessage] = useState<string | null>(null);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);

    const {
        control,
        handleSubmit,
        formState: { errors }
    } = useForm<FormValues>({
                                resolver: yupResolver(schema)
                            });

    useEffect(() => {
        dispatch(appBarSetCustomState("Change Password"));
    }, [dispatch]);

    const onSubmit = async (data: FormValues) => {
        try {
            await dispatch(
                changePassword({
                                   jwtToken: userJwtToken,
                                   changePasswordDto: {
                                       email: userEmail,
                                       passwordCurrent: data.currentPassword,
                                       password: data.newPassword,
                                       passwordConfirm: data.confirmNewPassword
                                   }
                               })
            ).unwrap();
            log.info("Password changed successfully");
            setSuccessMessage("Password changed successfully!");
        } catch (error: any) {
            setErrorMessage(error.response?.data?.message || "Failed to change password. Please try again.");
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
                Change Password
            </Typography>

            <Box display="flex" flexDirection="column" alignItems="center" width="100%" maxWidth="400px">
                <FormControl fullWidth margin="normal">
                    <Controller
                        name="currentPassword"
                        control={ control }
                        render={ ({ field }) => (
                            <TextField
                                { ...field }
                                id="current-password-input"
                                label="Current Password"
                                type="password"
                                variant="outlined"
                                error={ !!errors.currentPassword }
                                helperText={ errors.currentPassword?.message }
                            />
                        ) }
                    />
                </FormControl>

                <FormControl fullWidth margin="normal">
                    <Controller
                        name="newPassword"
                        control={ control }
                        render={ ({ field }) => (
                            <TextField
                                { ...field }
                                id="new-password-input"
                                label="New Password"
                                type="password"
                                variant="outlined"
                                error={ !!errors.newPassword }
                                helperText={ errors.newPassword?.message }
                            />
                        ) }
                    />
                </FormControl>

                <FormControl fullWidth margin="normal">
                    <Controller
                        name="confirmNewPassword"
                        control={ control }
                        render={ ({ field }) => (
                            <TextField
                                { ...field }
                                id="confirm-new-password-input"
                                label="Confirm New Password"
                                type="password"
                                variant="outlined"
                                error={ !!errors.confirmNewPassword }
                                helperText={ errors.confirmNewPassword?.message }
                            />
                        ) }
                    />
                </FormControl>

                <Button variant="contained" color="success" fullWidth onClick={ handleSubmit(onSubmit) }>
                    Change Password
                </Button>
            </Box>

            <Snackbar open={ !!errorMessage } autoHideDuration={ 6000 } onClose={ () => setErrorMessage(null) }>
                <Alert onClose={ () => setErrorMessage(null) } severity="error">
                    { errorMessage }
                </Alert>
            </Snackbar>

            <Snackbar open={ !!successMessage } autoHideDuration={ 6000 } onClose={ () => setSuccessMessage(null) }>
                <Alert onClose={ () => setSuccessMessage(null) } severity="success">
                    { successMessage }
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default ChangePassword;
