import AuthApiClient from "./client/authApiClient.ts";
import CategoryApiClient from "./client/categoryApiClient.ts";
import HelperApiClient from "./client/helperApiClient.ts";
import ItemApiClient from "./client/itemApiClient.ts";
import axiosClient from "../config/appConfig.ts";
import {jwtTokenExtractor} from "../store/browserStore.ts";

export const authApiClient = new AuthApiClient(axiosClient, jwtTokenExtractor);
export const categoryApiClient = new CategoryApiClient(axiosClient, jwtTokenExtractor);
export const helperApiClient = new HelperApiClient(axiosClient, jwtTokenExtractor);
export const itemApiClient = new ItemApiClient(axiosClient, jwtTokenExtractor);