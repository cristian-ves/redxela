import { useEffect } from 'react'
import { useAppDispatch, useAppSelector } from '../store/hooks'
import { selectNotice } from '../store/selectors'
import { noticeDismissed } from '../store/uiSlice'
import styles from './Notice.module.css'

const VISIBLE_MILLIS = 5000

/* Shows the outcome of the last command and hides it after a few seconds. */
export function Notice() {
  const notice = useAppSelector(selectNotice)
  const dispatch = useAppDispatch()

  useEffect(() => {
    if (!notice) return
    const timer = setTimeout(() => dispatch(noticeDismissed()), VISIBLE_MILLIS)
    return () => clearTimeout(timer)
  }, [notice, dispatch])

  if (!notice) return null
  return (
    <div className={`${styles.notice} ${styles[notice.kind]}`} role="status">
      <span>{notice.text}</span>
      <button type="button" className={styles.close} aria-label="Dismiss" onClick={() => dispatch(noticeDismissed())}>
        ×
      </button>
    </div>
  )
}
