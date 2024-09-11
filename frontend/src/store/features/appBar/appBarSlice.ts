import {createSlice, PayloadAction} from "@reduxjs/toolkit";

// Define the state interface for the AppBar slice
export interface AppBarSliceState {
    value: string;
}

// Initial state for the AppBar, with a default title
const initialState: AppBarSliceState = {
    value: "Recollector App"
};

// Create a slice for managing the AppBar's state
export const appBarSlice = createSlice({
    name: "appBarHeader",
    initialState,
    reducers: {
        /**
         * Action to set a custom AppBar header title.
         *
         * @param {AppBarSliceState} state - The current state of the AppBar.
         * @param {PayloadAction<string>} action - The action containing the new title.
         */
        appBarSetCustomState: (state: AppBarSliceState, action: PayloadAction<string>) => {
            state.value = action.payload;
        }
    }
});

// Export the action generated by createSlice
export const {appBarSetCustomState} = appBarSlice.actions;

// Export the reducer to be used in the store
export default appBarSlice.reducer;
