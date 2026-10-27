import { api } from '../../api/http'
import { Button } from '../../components/Button'
import { Toggle } from '../../components/Toggle'
import { useCommand } from '../../hooks/useCommand'
import { useAppDispatch, useAppSelector } from '../../store/hooks'
import { selectControls, selectSnapshot } from '../../store/selectors'
import { noticeShown } from '../../store/uiSlice'
import styles from './ConflictPanel.module.css'

/* The two switches of the deadlock experiment: cause one on purpose, or prevent them by resource ordering. */
export function ConflictActions() {
  const run = useCommand()
  const dispatch = useAppDispatch()
  const { preventionEnabled } = useAppSelector(selectControls)
  const { deadlock } = useAppSelector(selectSnapshot)

  const provoke = async () => {
    const result = await run(api.provokeConflict)
    if (!result) return
    dispatch(noticeShown({ kind: 'success', text: `${result.orders.join(' and ')} started needing the same resources in opposite order` }))
  }

  return (
    <div className={styles.actions}>
      <Toggle
        label="Prevent by global resource ordering"
        checked={preventionEnabled}
        onChange={(enabled) => run(() => api.setPrevention(enabled))}
      />
      <Button disabled={deadlock !== null} onClick={provoke}>
        Provoke a deadlock
      </Button>
    </div>
  )
}
