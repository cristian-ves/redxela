import { useEffect } from 'react'
import { startSnapshotSocket } from '../api/socket'
import { useAppDispatch } from '../store/hooks'

/* Opens the live connection when the application mounts and closes it when it unmounts. */
export function useSnapshotSocket(): void {
  const dispatch = useAppDispatch()
  useEffect(() => startSnapshotSocket(dispatch), [dispatch])
}
