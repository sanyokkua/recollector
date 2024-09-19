// Import necessary libraries and components from React, ReactDOM, React Router, and Redux
import { StrictMode }     from "react";
import { createRoot }     from "react-dom/client";
import { Provider }       from "react-redux";
import { RouterProvider } from "react-router-dom";
import { logger }         from "./config/appConfig.ts";

// Import the application's router and Redux store
import router from "./routes/router";
import store  from "./store/store";


const log = logger.getLogger("AppRoot");

// Get the root element from the DOM where the React app will be mounted
const rootElement = document.getElementById("root");

if (!rootElement) {
    log.warn("Failed to load root element");
    throw new Error("Root element not found. Please ensure there is an element with id \"root\" in your HTML.");
}

// Create the React root and render the app
logger.debug("Will be rendered React App");
createRoot(rootElement).render(
    <StrictMode>
        {/* Redux Provider to supply the store to the entire app */ }
        <Provider store={ store }>
            {/* React Router Provider to manage navigation and routing */ }
            <RouterProvider router={ router }/>
        </Provider>
    </StrictMode>
);
