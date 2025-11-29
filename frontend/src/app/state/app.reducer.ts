/**
 * App Reducer
 * 
 * Root reducer for NgRx state management.
 * Manages global application state.
 */
import { createReducer, on, createAction, props } from '@ngrx/store';

// State Interface
export interface AppState {
  isLoading: boolean;
  isAuthenticated: boolean;
  currentUser: User | null;
  error: string | null;
}

// User interface (simplified for hello-world)
export interface User {
  id: string;
  email: string;
  name: string;
}

// Initial State
export const initialState: AppState = {
  isLoading: false,
  isAuthenticated: false,
  currentUser: null,
  error: null,
};

// Actions
export const setLoading = createAction(
  '[App] Set Loading',
  props<{ isLoading: boolean }>()
);

export const setAuthenticated = createAction(
  '[App] Set Authenticated',
  props<{ isAuthenticated: boolean; user: User | null }>()
);

export const setError = createAction(
  '[App] Set Error',
  props<{ error: string | null }>()
);

export const clearError = createAction('[App] Clear Error');

// Reducer
export const appReducer = createReducer(
  initialState,
  on(setLoading, (state, { isLoading }) => ({
    ...state,
    isLoading
  })),
  on(setAuthenticated, (state, { isAuthenticated, user }) => ({
    ...state,
    isAuthenticated,
    currentUser: user,
    error: null
  })),
  on(setError, (state, { error }) => ({
    ...state,
    error,
    isLoading: false
  })),
  on(clearError, (state) => ({
    ...state,
    error: null
  }))
);

// Selectors
export const selectIsLoading = (state: { app: AppState }) => state.app.isLoading;
export const selectIsAuthenticated = (state: { app: AppState }) => state.app.isAuthenticated;
export const selectCurrentUser = (state: { app: AppState }) => state.app.currentUser;
export const selectError = (state: { app: AppState }) => state.app.error;

