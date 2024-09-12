import {FC, useEffect} from "react";
import {useAppDispatch, useAppSelector} from "../store/hooks.ts";
import {appBarSetCustomState} from "../store/features/appBar/appBarSlice.ts";
import {Box, Paper, Table, TableBody, TableCell, TableContainer, TableRow, Typography} from "@mui/material";
import {getStatistics} from "../store/features/helper/helperSlice.ts";


const Profile: FC = () => {
    const dispatch = useAppDispatch();
    const {userEmail, userJwtToken} = useAppSelector((state) => state.globals);
    const {statistics} = useAppSelector((state) => state.helper);

    useEffect(() => {
        dispatch(appBarSetCustomState("Profile"));
        dispatch(getStatistics(userJwtToken));
    }, [userEmail]);

    return <Box sx={{p: 3}}>
        <Typography variant="h6" gutterBottom>
            User Email: {userEmail}
        </Typography>

        <TableContainer component={Paper}>
            <Table>
                <TableBody>
                    <TableRow>
                        <TableCell>Total Number of Categories</TableCell>
                        <TableCell>{statistics.totalNumberOfCategories}</TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell>Total Number of Items</TableCell>
                        <TableCell>{statistics.totalNumberOfItems}</TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell>Total Number of Items To do</TableCell>
                        <TableCell>{statistics.totalNumberOfItemsTodo}</TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell>Total Number of Items In Progress</TableCell>
                        <TableCell>{statistics.totalNumberOfItemsInProgress}</TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell>Total Number of Items Finished</TableCell>
                        <TableCell>{statistics.totalNumberOfItemsFinished}</TableCell>
                    </TableRow>
                </TableBody>
            </Table>
        </TableContainer>
    </Box>;
};

export default Profile;