import {configureStore} from "@reduxjs/toolkit";
import drawerToggledReducer from "./features/drawer/drawerSlice";
import appBarSliceReducer from "./features/appBar/appBarSlice";
import categoriesSliceReducer from "./features/categories/categoriesSlice";
import itemsSliceReducer from "./features/items/itemsSlice";
import globalsSliceReducer from "./features/global/globalSlice";

/**
 * Configures and creates the Redux store with the specified reducers.
 * The store manages the global state of the application.
 */
const store = configureStore({
    reducer: {
        // Reducer for managing the drawer toggled state
        drawerToggled: drawerToggledReducer,

        // Reducer for managing the AppBar header state
        appBarHeader: appBarSliceReducer,

        categories: categoriesSliceReducer,

        items: itemsSliceReducer,

        globals: globalsSliceReducer
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
