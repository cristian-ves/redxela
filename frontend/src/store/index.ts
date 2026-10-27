import { configureStore } from '@reduxjs/toolkit'
import catalog from './catalogSlice'
import connection from './connectionSlice'
import snapshot from './snapshotSlice'
import ui from './uiSlice'

export const store = configureStore({
  reducer: { snapshot, connection, catalog, ui },
})

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch
