import { createAsyncThunk, createSlice, PayloadAction } from "@reduxjs/toolkit";
import HelperApiClient                                  from "../../../api/client/helperApiClient.ts";
import { Response }                                     from "../../../api/dto/common.ts";
import { SettingsDto, StatisticDto }                    from "../../../api/dto/helperDto.ts";
import axiosClient, { logger }                          from "../../../config/appConfig";


const log = logger.getLogger("helperSlice");

export interface HelperState {
    statistics: StatisticDto,
    statuses: string[],
    settings: SettingsDto,
    error: string
}

const defaultSettings = {
    userEmail: "",
    categoryBackgroundColor: "",
    categoryItemColor: "",
    categoryFabColor: "",
    categoryPageSize: 10,
    itemBackgroundColor: "",
    itemItemColor: "",
    itemFabColor: "",
    itemPageSize: 10
};

const initialState: HelperState = {
    statistics: {
        totalNumberOfCategories: 0,
        totalNumberOfItems: 0,
        totalNumberOfItemsTodo: 0,
        totalNumberOfItemsInProgress: 0,
        totalNumberOfItemsFinished: 0
    },
    settings: defaultSettings,
    statuses: [],
    error: ""
};

export const getItemStatuses = createAsyncThunk("helper/statuses",
                                                async (jwtToken: string, { rejectWithValue }) => {
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
                                              async (jwtToken: string, { rejectWithValue }) => {
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

export const getSettings = createAsyncThunk("helper/getSettings",
                                            async (jwtToken: string, { rejectWithValue }) => {
                                                log.info("Attempting to get settings");
                                                try {
                                                    const client = new HelperApiClient(axiosClient, jwtToken);
                                                    return await client.getSettings();
                                                } catch (error: any) {
                                                    const errorMessage = error?.message || "Failed to get settings";
                                                    log.error("settings API call failed", error);
                                                    return rejectWithValue(errorMessage);
                                                }
                                            }
);
type SettingsUpdateRequest = {
    settings: SettingsDto;
    jwtToken: string;
}
export const updateSettings = createAsyncThunk("helper/updateSettings",
                                               async (settingsReq: SettingsUpdateRequest, { rejectWithValue }) => {
                                                   log.info("Attempting to update Settings");
                                                   try {
                                                       const client = new HelperApiClient(axiosClient, settingsReq.jwtToken);
                                                       return await client.updateSettings(settingsReq.settings);
                                                   } catch (error: any) {
                                                       const errorMessage = error?.message || "Failed to update Settings";
                                                       log.error("Update settings API call failed", error);
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
                                                       log.debug(`Response: ${ JSON.stringify(action.payload) }`);
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
                                                   })
                                                   .addCase(getSettings.pending, (state: HelperState) => {
                                                       log.debug("getSettings request pending");
                                                       state.error = "";
                                                   })
                                                   .addCase(getSettings.fulfilled, (state: HelperState, action: PayloadAction<Response<SettingsDto>>) => {
                                                       log.info("getSettings request fulfilled successfully");
                                                       state.settings = action.payload.data ?? defaultSettings;
                                                       state.error = action.payload.error ?? "";
                                                   })
                                                   .addCase(getSettings.rejected, (state: HelperState, action) => {
                                                       log.error("getSettings request was rejected", action.payload);
                                                       state.error = action.payload as string || "getSettings request was rejected";
                                                   })
                                                   .addCase(updateSettings.pending, (state: HelperState) => {
                                                       log.debug("updateSettings request pending");
                                                       state.error = "";
                                                   })
                                                   .addCase(updateSettings.fulfilled, (state: HelperState, action: PayloadAction<Response<SettingsDto>>) => {
                                                       log.info("updateSettings request fulfilled successfully");
                                                       state.settings = action.payload.data ?? defaultSettings;
                                                       state.error = action.payload.error ?? "";
                                                   })
                                                   .addCase(updateSettings.rejected, (state: HelperState, action) => {
                                                       log.error("updateSettings request was rejected", action.payload);
                                                       state.error = action.payload as string || "updateSettings request was rejected";
                                                   });
                                           }
                                       });

// Export the reducer to be used in the store
export default helperSlice.reducer;