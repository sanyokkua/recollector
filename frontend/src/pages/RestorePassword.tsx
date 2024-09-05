import {FC, useEffect} from 'react';
import {Box, Button, FormControl, TextField, Typography} from '@mui/material';
import {useAppDispatch} from '../store/hooks';
import {appBarSetCustomState} from '../store/features/appBar/appBarSlice';

const RestorePassword: FC = () => {
    const dispatch = useAppDispatch();

    useEffect(() => {
        dispatch(appBarSetCustomState("Password Restoration"));
    }, [dispatch]);

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
                Forgot your password?
            </Typography>

            <Typography variant="body1" paragraph>
                Enter your email below and request password restoration.
            </Typography>

            <Box
                display="flex"
                flexDirection="column"
                alignItems="center"
                width="100%"
                maxWidth="400px"
            >
                <FormControl fullWidth margin="normal">
                    <TextField
                        id="email-input"
                        label="Email"
                        variant="outlined"
                        autoComplete="email"
                    />
                </FormControl>

                <Button variant="contained" color="primary" fullWidth>
                    Send Restoration Email
                </Button>
            </Box>
        </Box>
    );
};

export default RestorePassword;
