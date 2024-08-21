import {TypedUseSelectorHook, useDispatch, useSelector} from 'react-redux';
import type {AppDispatch, RootState} from './store';

/**
 * A custom hook to return the app's dispatch function with the correct typing.
 * This should be used instead of the plain `useDispatch` to ensure that the dispatch
 * function is correctly typed with `AppDispatch`.
 *
 * @returns {AppDispatch} The typed dispatch function.
 */
export const useAppDispatch = (): AppDispatch => useDispatch<AppDispatch>();

/**
 * A custom hook to return the app's state selector with the correct typing.
 * This should be used instead of the plain `useSelector` to ensure that the selector
 * function is correctly typed with `RootState`.
 *
 * @returns {TypedUseSelectorHook<RootState>} The typed selector hook.
 */
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector;
