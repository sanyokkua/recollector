import {createAsyncThunk, createSlice, PayloadAction} from "@reduxjs/toolkit";
import axiosClient, {logger} from "../../../config/appConfig";
import {Response} from "../../../api/dto/common.ts";
import {StatisticDto} from "../../../api/dto/helperDto.ts";
import HelperApiClient from "../../../api/client/helperApiClient.ts";

const log = logger.getLogger("helperSlice");

export interface HelperState {
    statistics: StatisticDto,
    statuses: string[],
    error: string
}

const initialState: HelperState = {
    statistics: {
        totalNumberOfCategories: 0,
        totalNumberOfItems: 0,
        totalNumberOfItemsTodo: 0,
        totalNumberOfItemsInProgress: 0,
        totalNumberOfItemsFinished: 0
    },
    statuses: [],
    error: ""
};

export const getItemStatuses = createAsyncThunk("helper/statuses",
    async (jwtToken: string, {rejectWithValue}) => {
        log.info("Attempting to get statuses");
        try {
            const client = new HelperApiClient(axiosClient, jwtToken);
            return await client.getItemStatuses();
        } catch (error: any) {
            const errorMessage = error?.message || "Failed to get statuses";
            log.error("ItemStatuses API call failed", error);
            return rejectWithValue(errorMessage);
        }
    }
);

export const getStatistics = createAsyncThunk("helper/statistics",
    async (jwtToken: string, {rejectWithValue}) => {
        log.info("Attempting to get statuses");
        try {
            const client = new HelperApiClient(axiosClient, jwtToken);
            return await client.getStatistics();
        } catch (error: any) {
            const errorMessage = error?.message || "Failed to get statistics";
            log.error("Statistics API call failed", error);
            return rejectWithValue(errorMessage);
        }
    }
);

export const helperSlice = createSlice({
    name: "helper",
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder
            .addCase(getItemStatuses.pending, (state: HelperState) => {
                log.debug("getItemStatuses request pending");
                state.error = "";
            })
            .addCase(getItemStatuses.fulfilled, (state: HelperState, action: PayloadAction<Response<string[]>>) => {
                log.info("getItemStatuses request fulfilled successfully");
                state.statuses = action.payload.data ?? [];
                state.error = action.payload.error ?? "";
            })
            .addCase(getItemStatuses.rejected, (state: HelperState, action) => {
                log.error("getItemStatuses request was rejected", action.payload);
                state.error = action.payload as string || "getItemStatuses request was rejected";
            })
            .addCase(getStatistics.pending, (state: HelperState) => {
                log.debug("getStatistics request pending");
                state.error = "";
            })
            .addCase(getStatistics.fulfilled, (state: HelperState, action: PayloadAction<Response<StatisticDto>>) => {
                log.info("getStatistics request fulfilled successfully");
                log.debug(`Response: ${JSON.stringify(action.payload)}`);
                state.statistics = action.payload.data ?? {
                    totalNumberOfCategories: 0,
                    totalNumberOfItems: 0,
                    totalNumberOfItemsTodo: 0,
                    totalNumberOfItemsInProgress: 0,
                    totalNumberOfItemsFinished: 0
                };
                state.error = action.payload.error ?? "getStatistics request was rejected";
            })
            .addCase(getStatistics.rejected, (state: HelperState, action) => {
                log.error("getStatistics request was rejected", action.payload);
                state.error = action.payload as string || "getStatistics request was rejected";
            });
    }
});


// Export the reducer to be used in the store
export default helperSlice.reducer;