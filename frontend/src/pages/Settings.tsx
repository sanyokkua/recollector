import {FC, useEffect, useState} from "react";
import {useAppDispatch} from "../store/hooks.ts";
import {appBarSetCustomState} from "../store/features/appBar/appBarSlice.ts";
import {Box, Button, Grid, Paper, Slider, Typography} from "@mui/material";
// @ts-ignore
import {SketchPicker} from "react-color";
import {useNavigate} from "react-router-dom";

interface Settings {
    categories: {
        backgroundColor: string;
        itemColor: string;
        pageSize: number;
    };
    items: {
        backgroundColor: string;
        itemColor: string;
        pageSize: number;
    };
}

const Settings: FC = () => {
    const dispatch = useAppDispatch();
    const navigate = useNavigate();
    useEffect(() => {
        dispatch(appBarSetCustomState("Settings"));
    });

    const [settings, setSettings] = useState<Settings>({
        categories: {
            backgroundColor: "#ffffff",
            itemColor: "#e1bee7",
            pageSize: 10
        },
        items: {
            backgroundColor: "#ffffff",
            itemColor: "#e1bee7",
            pageSize: 10
        }
    });

    // Handle input change in the settings
    const handleInputChange = (section: "categories" | "items", field: "backgroundColor" | "itemColor" | "pageSize", value: string | number) => {
        setSettings((prev) => ({
            ...prev,
            [section]: {
                ...prev[section],
                [field]: value
            }
        }));
    };

    // Handle page size change
    const handlePageSizeChange = (section: "categories" | "items", value: number) => {
        setSettings((prev) => ({
            ...prev,
            [section]: {
                ...prev[section],
                pageSize: value
            }
        }));
    };

    // Render input fields for settings with a color picker
    const renderSettings = (section: "categories" | "items", title: string) => (
        <Paper elevation={3} sx={{p: 2, mb: 3}}>
            <Typography variant="h6">{title}</Typography>
            <Grid container spacing={2} sx={{mt: 1}}>
                <Grid item xs={12} sm={6}>
                    <Typography>Background Color:</Typography>
                    <SketchPicker
                        color={settings[section].backgroundColor}
                        onChangeComplete={(color: { hex: string | number; }) =>
                            handleInputChange(section, "backgroundColor", color.hex)
                        }
                    />
                </Grid>
                <Grid item xs={12} sm={6}>
                    <Typography>Item Color:</Typography>
                    <SketchPicker
                        color={settings[section].itemColor}
                        onChangeComplete={(color: { hex: string | number; }) =>
                            handleInputChange(section, "itemColor", color.hex)
                        }
                    />
                </Grid>
                <Grid item xs={12}>
                    <Typography>Page Size:</Typography>
                    <Slider
                        value={settings[section].pageSize}
                        aria-labelledby="input-slider"
                        valueLabelDisplay="auto"
                        marks
                        step={1}
                        min={1}
                        max={100}
                        onChange={(_e, value) =>
                            handlePageSizeChange(section, value as number)
                        }
                    />
                </Grid>
            </Grid>
        </Paper>
    );

    const handleChangePassword = () => {
        navigate("/change_password");
    };

    // Function to handle account deletion
    const handleDeleteAccount = () => {
        // Add your account deletion logic here
        alert("Account deletion is not implemented yet.");
    };

    return (
        <Box sx={{p: 3}}>
            <Typography variant="h4" gutterBottom>Settings</Typography>

            {/* Render Category and Item Settings */}
            {renderSettings("categories", "Categories Settings")}
            {renderSettings("items", "Items Settings")}

            <Button variant="contained" color="primary" sx={{mt: 2}}
                    onClick={handleChangePassword}>
                Change Password
            </Button>

            {/* Delete Account Button */}
            <Button variant="contained" color="error" sx={{mt: 2}}
                    onClick={handleDeleteAccount}>
                Delete Account
            </Button>
        </Box>
    );
};

export default Settings;