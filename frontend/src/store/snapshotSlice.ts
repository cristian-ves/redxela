import { createSlice, type PayloadAction } from '@reduxjs/toolkit'
import { EMPTY_SNAPSHOT } from '../api/empty'
import type { Snapshot } from '../api/types'

/* The latest complete state pushed by the server. Each snapshot replaces the previous one. */
const snapshotSlice = createSlice({
  name: 'snapshot',
  initialState: { current: EMPTY_SNAPSHOT },
  reducers: {
    snapshotReceived(state, action: PayloadAction<Snapshot>) {
      state.current = action.payload
    },
  },
})

export const { snapshotReceived } = snapshotSlice.actions
export default snapshotSlice.reducer
