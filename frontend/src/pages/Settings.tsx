import { Box, Button, Grid, Paper, Slider, Typography } from "@mui/material";
import { FC, useEffect, useState }                      from "react";
// @ts-ignore
import { SwatchesPicker }                               from "react-color";
import { useNavigate }                                  from "react-router-dom";
import { SettingsDto }                                  from "../api/dto/helperDto.ts";
import { logger }                                       from "../config/appConfig.ts";
import { appBarSetCustomState }                         from "../store/features/appBar/appBarSlice.ts";
import { setCategoryFilterSize }                        from "../store/features/categories/categoriesSlice.ts";
import { getSettings, updateSettings }                  from "../store/features/helper/helperSlice";
import { setItemFilterSize }                            from "../store/features/items/itemsSlice.ts";
import { useAppDispatch, useAppSelector }               from "../store/hooks.ts";


const log = logger.getLogger("Settings");

const isValidHexColor = (color: string) => {
    const isValid = /^#([0-9A-F]{3}){1,2}$/i.test(color);
    log.debug("Validating hex color", { color, isValid });
    return isValid;
};

type Section = "category" | "item";
type Field = "BackgroundColor" | "ItemColor" | "FabColor";
type ColorChanged = (section: Section, field: Field, color: string) => void;
type SizeChanged = (section: Section, value: number) => void;
type SettingsSectionProps = {
    section: Section;
    title: string;
    localSettings: SettingsDto;
    onColorChange: ColorChanged;
    onPageSizeChange: SizeChanged;
    backGroundColor: string;
};

const renderColorDisplay = (section: Section, field: Field, localSettings: SettingsDto, handleColorInputChange: ColorChanged) => (
    <Grid item xs={ 12 } sm={ 6 }>
        <Typography>{ `${ field.replace(/([A-Z])/g, " $1") }:` }</Typography>
        <SwatchesPicker
            color={ localSettings[`${ section }${ field }`] }
            onChangeComplete={ (color) => handleColorInputChange(section, field, color.hex) }
        />
        <Box sx={ { mt: 1, p: 1, borderRadius: 1, bgcolor: localSettings[`${ section }${ field }`] } }>
            <Typography variant="body2">
                Current Color: { localSettings[`${ section }${ field }`] }
            </Typography>
        </Box>
    </Grid>
);
const renderPageSizeSlider = (section: Section, localSettings: SettingsDto, onPageSizeChange: SizeChanged) => (
    <Grid item xs={ 12 }>
        <Typography>Page Size:</Typography>
        <Slider value={ localSettings[`${ section }PageSize`] } aria-labelledby="input-slider" valueLabelDisplay="auto"
                marks step={ 1 } min={ 1 } max={ 100 }
                onChange={ (_, value) => onPageSizeChange(section, value as number) }
        />
        <Typography variant="body2">
            Current Page Size: { localSettings[`${ section }PageSize`] }
        </Typography>
    </Grid>
);
const SettingsSection: FC<SettingsSectionProps> = ({
                                                       section,
                                                       title,
                                                       localSettings,
                                                       onColorChange,
                                                       onPageSizeChange,
                                                       backGroundColor
                                                   }) => (
    <Paper elevation={ 1 } sx={ { p: 2, mb: 3, backgroundColor: backGroundColor } }>
        <Typography variant="h6">{ title }</Typography>
        <Grid container spacing={ 2 } sx={ { mt: 1 } }>
            { renderColorDisplay(section, "BackgroundColor", localSettings, onColorChange) }
            { renderColorDisplay(section, "ItemColor", localSettings, onColorChange) }
            { renderColorDisplay(section, "FabColor", localSettings, onColorChange) }
            { renderPageSizeSlider(section, localSettings, onPageSizeChange) }
        </Grid>
    </Paper>
);

const Settings: FC = () => {
    const dispatch = useAppDispatch();
    const navigate = useNavigate();
    const { settings } = useAppSelector((state) => state.helper);
    const [localSettings, setLocalSettings] = useState<SettingsDto>(settings);

    useEffect(() => {
        log.info("Settings component mounted");
        dispatch(getSettings());
        dispatch(appBarSetCustomState("Settings"));
    }, [dispatch]);
    useEffect(() => {
        log.debug("Settings updated from store", settings);
        setLocalSettings(settings);
    }, [settings]);

    const handleColorInputChange = (section: Section, field: Field, value: string) => {
        log.info("Changing color input", { section, field, value });
        if (!isValidHexColor(value)) {
            log.warn("Invalid color format", { value });
            alert("Invalid color format. Please use #colorCode format.");
            return;
        }

        const fieldName = `${ section }${ field }`;
        setLocalSettings((prev) => ({
            ...prev,
            [fieldName]: value
        }));
    };
    const handlePageSizeChange = (section: Section, value: number) => {
        log.info("Changing page size", { section, value });
        const fieldName = `${ section }PageSize`;
        setLocalSettings((prev) => ({
            ...prev,
            [fieldName]: value
        }));
    };
    const handleSaveSettings = () => {
        log.info("Saving settings", localSettings);
        try {
            dispatch(updateSettings({ settings: localSettings }));
            dispatch(setCategoryFilterSize(localSettings.categoryPageSize));
            dispatch(setItemFilterSize(localSettings.itemPageSize));
            log.info("Settings saved successfully");
            navigate("/dashboard");
        } catch (error) {
            log.error("Error saving settings", error);
        }
    };
    const handleChangePassword = () => {
        log.info("Navigating to Change Password");
        navigate("/change_password");
    };
    const handleDeleteAccount = () => {
        log.warn("Account deletion feature not implemented");
        navigate("/delete_account");
    };

    return (
        <Box sx={ { p: 3 } }>
            <Typography variant="h4" gutterBottom>Settings</Typography>
            <Typography variant="h5" sx={ { mt: 2 } }>Properties</Typography>

            <SettingsSection section="category" title="Categories Settings" localSettings={ localSettings }
                             backGroundColor={ "#e3f2fd" }
                             onColorChange={ handleColorInputChange }
                             onPageSizeChange={ handlePageSizeChange }
            />
            <SettingsSection section="item" title="Items Settings" localSettings={ localSettings }
                             backGroundColor={ "#e0f2f1" }
                             onColorChange={ handleColorInputChange }
                             onPageSizeChange={ handlePageSizeChange }
            />

            <Button variant="contained" color="primary" fullWidth onClick={ handleSaveSettings }>
                Save Settings
            </Button>

            <Typography variant="h5" sx={ { mt: 2 } }>Password Management</Typography>

            <Button variant="contained" color="warning" fullWidth onClick={ handleChangePassword }>
                Change Password
            </Button>

            <Typography variant="h5" sx={ { mt: 2 } }>Account Management</Typography>

            <Button variant="contained" color="error" sx={ { mt: 2 } } fullWidth onClick={ handleDeleteAccount }>
                Delete Account
            </Button>
        </Box>
    );
};

export default Settings;
