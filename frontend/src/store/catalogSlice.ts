import { createAsyncThunk, createSlice } from '@reduxjs/toolkit'
import { EMPTY_META } from '../api/empty'
import { api } from '../api/http'
import type { ClientView, Meta } from '../api/types'

/* Slow-changing data fetched over REST: the static description of the system and the client accounts. */
export const loadCatalog = createAsyncThunk('catalog/load', async () => {
  const [meta, clients] = await Promise.all([api.meta(), api.clients()])
  return { meta, clients }
})

export const loadClients = createAsyncThunk('catalog/loadClients', () => api.clients())

interface CatalogState {
  meta: Meta
  clients: ClientView[]
}

const initialState: CatalogState = { meta: EMPTY_META, clients: [] }

const catalogSlice = createSlice({
  name: 'catalog',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(loadCatalog.fulfilled, (_state, action) => action.payload)
      .addCase(loadClients.fulfilled, (state, action) => {
        state.clients = action.payload
      })
  },
})

export default catalogSlice.reducer
