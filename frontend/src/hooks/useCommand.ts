import { useCallback } from 'react'
import { useAppDispatch } from '../store/hooks'
import { noticeShown } from '../store/uiSlice'

/*
 * Runs a request to the server and reports the outcome in the notice area:
 * the error message on failure, an optional confirmation on success. It
 * returns the result, or undefined when the request failed.
 */
export function useCommand() {
  const dispatch = useAppDispatch()
  return useCallback(
    async function run<T>(action: () => Promise<T>, success?: string): Promise<T | undefined> {
      try {
        const result = await action()
        if (success) dispatch(noticeShown({ kind: 'success', text: success }))
        return result
      } catch (error) {
        const text = error instanceof Error ? error.message : 'Something went wrong'
        dispatch(noticeShown({ kind: 'error', text }))
        return undefined
      }
    },
    [dispatch],
  )
}
