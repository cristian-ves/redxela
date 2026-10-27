import { createSlice, type PayloadAction } from '@reduxjs/toolkit'

export type ConnectionStatus = 'connecting' | 'open' | 'closed'

const connectionSlice = createSlice({
  name: 'connection',
  initialState: { status: 'connecting' as ConnectionStatus },
  reducers: {
    connectionChanged(state, action: PayloadAction<ConnectionStatus>) {
      state.status = action.payload
    },
  },
})

export const { connectionChanged } = connectionSlice.actions
export default connectionSlice.reducer
