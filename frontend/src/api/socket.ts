import { loadCatalog } from '../store/catalogSlice'
import { connectionChanged } from '../store/connectionSlice'
import type { AppDispatch } from '../store'
import { snapshotReceived } from '../store/snapshotSlice'
import { API_URL } from './http'
import type { Snapshot } from './types'

const WS_URL = API_URL.replace(/^http/, 'ws') + '/ws'
const MAX_RETRY_MILLIS = 5000

/*
 * Keeps a WebSocket open to the server and feeds every snapshot into the
 * store. If the connection drops it retries with a growing delay, and each
 * time it connects it reloads the catalog because a restarted server starts
 * from its initial data. Returns the function that closes everything.
 */
export function startSnapshotSocket(dispatch: AppDispatch): () => void {
  let socket: WebSocket | null = null
  let timer: ReturnType<typeof setTimeout> | undefined
  let attempts = 0
  let stopped = false

  const connect = () => {
    dispatch(connectionChanged('connecting'))
    socket = new WebSocket(WS_URL)
    socket.onopen = () => {
      attempts = 0
      dispatch(connectionChanged('open'))
      void dispatch(loadCatalog())
    }
    socket.onmessage = (event: MessageEvent<string>) => {
      dispatch(snapshotReceived(JSON.parse(event.data) as Snapshot))
    }
    socket.onerror = () => socket?.close()
    socket.onclose = () => {
      dispatch(connectionChanged('closed'))
      if (!stopped) {
        timer = setTimeout(connect, Math.min(MAX_RETRY_MILLIS, 500 * 2 ** attempts++))
      }
    }
  }

  connect()
  return () => {
    stopped = true
    clearTimeout(timer)
    socket?.close()
  }
}
