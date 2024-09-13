import { configureStore }     from "@reduxjs/toolkit";
import { logger }             from "../config/appConfig.ts";
import {
    currentCategoryIdSaver,
    currentCategoryNameSaver,
    userEmailSaver,
    userJwtRefreshTokenSaver,
    userJwtTokenSaver
}                             from "./browserStore.ts";
import appBarSliceReducer     from "./features/appBar/appBarSlice";
import categoriesSliceReducer from "./features/categories/categoriesSlice";
import drawerToggledReducer   from "./features/drawer/drawerSlice";
import globalsSliceReducer    from "./features/global/globalSlice";
import helperSliceReducer     from "./features/helper/helperSlice.ts";
import itemsSliceReducer      from "./features/items/itemsSlice";


const log = logger.getLogger("reduxStore");

/**
 * Configures and creates the Redux store with the specified reducers.
 * The store manages the global state of the application.
 */
const store = configureStore({
                                 reducer: {
                                     drawerToggled: drawerToggledReducer,
                                     appBarHeader: appBarSliceReducer,
                                     categories: categoriesSliceReducer,
                                     items: itemsSliceReducer,
                                     globals: globalsSliceReducer,
                                     helper: helperSliceReducer
                                 }
                             });

store.subscribe(() => {
    try {
        const state = store.getState();

        const email = state.globals.userEmail;
        const jwt = state.globals.userJwtToken;
        const refresh = state.globals.userJwtRefreshToken;

        const currentCategoryId = state.globals.currentCategoryId ?? -1;
        const currentCategoryName = state.globals.currentCategoryName ?? "";

        currentCategoryIdSaver(currentCategoryId);
        currentCategoryNameSaver(currentCategoryName);
        userEmailSaver(email);
        userJwtTokenSaver(jwt);
        userJwtRefreshTokenSaver(refresh);
    } catch (err) {
        log.error("Could not save partial state", err);
    }
});

/**
 * Type representing the entire Redux state tree.
 * It is inferred from the store's `getState` method.
 */
export type RootState = ReturnType<typeof store.getState>;

/**
 * Type representing the dispatch function from the Redux store.
 * This is used to ensure that dispatch is correctly typed throughout the app.
 */
export type AppDispatch = typeof store.dispatch;

/**
 * Type representing the entire Redux store instance.
 * Useful for when you need to access the store directly.
 */
export type AppStore = typeof store;

export default store;
