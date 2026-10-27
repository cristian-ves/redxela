import { createSelector } from '@reduxjs/toolkit'
import type { ResourceTypeId } from '../api/types'
import type { RootState } from './index'

/* Small named readers over the store, so components say what they need instead of how to reach it. */
export const selectSnapshot = (state: RootState) => state.snapshot.current
export const selectControls = (state: RootState) => state.snapshot.current.controls
export const selectResources = (state: RootState) => state.snapshot.current.resources
export const selectInProcess = (state: RootState) => state.snapshot.current.inProcess
export const selectWaiting = (state: RootState) => state.snapshot.current.waiting
export const selectCompleted = (state: RootState) => state.snapshot.current.completed
export const selectStats = (state: RootState) => state.snapshot.current.stats
export const selectWarehouse = (state: RootState) => state.snapshot.current.warehouse
export const selectConnection = (state: RootState) => state.connection.status
export const selectMeta = (state: RootState) => state.catalog.meta
export const selectClients = (state: RootState) => state.catalog.clients
export const selectTheme = (state: RootState) => state.ui.theme
export const selectNotice = (state: RootState) => state.ui.notice

/* Resource type -> display name, taken from the live snapshot so it is available from the first message. */
export const selectResourceLabels = createSelector([selectResources], (resources) =>
  Object.fromEntries(resources.map((resource) => [resource.type, resource.label])) as Partial<
    Record<ResourceTypeId, string>
  >,
)
