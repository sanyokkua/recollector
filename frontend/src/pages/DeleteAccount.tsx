import { yupResolver }                                               from "@hookform/resolvers/yup";
import { Box, Button, FormControl, Snackbar, TextField, Typography } from "@mui/material";
import Alert                                                         from "@mui/material/Alert";
import Dialog                                                        from "@mui/material/Dialog";
import DialogActions                                                 from "@mui/material/DialogActions";
import DialogContent                                                 from "@mui/material/DialogContent";
import DialogTitle                                                   from "@mui/material/DialogTitle";
import { FC, useEffect, useState }                                   from "react";
import { Controller, useForm }                                       from "react-hook-form";
import { useNavigate }                                               from "react-router-dom";
import * as yup                                                      from "yup";
import { parseErrorMessage }                                         from "../api/client/utils";
import { AccountDeleteRequestDto }                                   from "../api/dto/authenticationDto";
import { logger }                                                    from "../config/appConfig";
import { appBarSetCustomState }                                      from "../store/features/appBar/appBarSlice";
import { deleteUser }                                                from "../store/features/global/globalSlice";
import { useAppDispatch }                                            from "../store/hooks";


const log = logger.getLogger("DeleteAccount");

// Define validation schema with Yup
const schema = yup.object(
    {
        email: yup
            .string()
            .email("Enter a valid email address")
            .required("Email is required"),
        password: yup
            .string()
            .required("Password is required"),
        passwordConfirm: yup
            .string()
            .oneOf([yup.ref("password")], "Passwords must match")
            .required("Confirm password is required")
    }
);

interface FormValues extends AccountDeleteRequestDto {
}

const DeleteAccount: FC = () => {
    const dispatch = useAppDispatch();
    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = useState<string | null>(null);
    const [successMessage, setSuccessMessage] = useState<string | null>(null);
    const [openDialog, setOpenDialog] = useState<boolean>(false);
    const [formData, setFormData] = useState<FormValues | null>(null);

    const { control, handleSubmit, formState: { errors } } = useForm<FormValues>({ resolver: yupResolver(schema) });

    useEffect(() => {
        dispatch(appBarSetCustomState("Delete Account"));
    }, [dispatch]);

    const onSubmit = async (data: FormValues) => {
        setFormData(data);
        setOpenDialog(true);
    };

    const handleDeleteConfirm = async () => {
        if (formData) {
            try {
                await dispatch(deleteUser(formData)).unwrap();
                log.info("Account deleted successfully");
                setSuccessMessage("Account deleted successfully!");
                setOpenDialog(false);
                setTimeout(() => navigate("/login"), 5000);
            } catch (error: any) {
                const errorMessage = parseErrorMessage(error, "Failed to delete account. Please try again.");
                setErrorMessage(errorMessage);
            }
        }
    };

    const handleDeleteCancel = () => {
        setOpenDialog(false);
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
                Delete Account
            </Typography>

            <Box display="flex" flexDirection="column" alignItems="center" width="100%" maxWidth="400px">
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
                                variant="outlined"
                                error={ !!errors.password }
                                helperText={ errors.password?.message }
                            />
                        ) }
                    />
                </FormControl>

                <FormControl fullWidth margin="normal">
                    <Controller
                        name="passwordConfirm"
                        control={ control }
                        render={ ({ field }) => (
                            <TextField
                                { ...field }
                                id="password-confirm-input"
                                label="Confirm Password"
                                type="password"
                                variant="outlined"
                                error={ !!errors.passwordConfirm }
                                helperText={ errors.passwordConfirm?.message }
                            />
                        ) }
                    />
                </FormControl>

                <Button variant="contained" color="error" fullWidth onClick={ handleSubmit(onSubmit) }>
                    Delete Account
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

            <Dialog open={ openDialog } onClose={ handleDeleteCancel }>
                <DialogTitle>Confirm Account Deletion</DialogTitle>
                <DialogContent>
                    <Typography variant="body1">
                        Are you sure you want to delete your account? This action cannot be undone and all your data
                        will be lost.
                    </Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={ handleDeleteCancel } color="primary">
                        Cancel
                    </Button>
                    <Button onClick={ handleDeleteConfirm } color="error">
                        Delete
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default DeleteAccount;
