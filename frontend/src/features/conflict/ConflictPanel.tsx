import { LockIcon, ShieldAlertIcon } from 'lucide-react'
import { Panel } from '../../components/Panel'
import { formatClock } from '../../lib/format'
import { useAppSelector } from '../../store/hooks'
import { selectControls, selectSnapshot } from '../../store/selectors'
import { ConflictActions } from './ConflictActions'
import { Explanation } from './Explanation'
import { Resolution } from './Resolution'
import { WaitCycle } from './WaitCycle'
import styles from './ConflictPanel.module.css'

const PREVENTION_ON =
  'Prevention is on: every operation asks for resources in one fixed global order, so a circular wait cannot form.'
const PREVENTION_OFF =
  'No circular wait right now. With prevention off, operations ask for resources in their own order, so one can form.'

/* Shows whether the system is deadlocked and, if it is, the wait cycle, why, and how to break it. */
export function ConflictPanel() {
  const { deadlock } = useAppSelector(selectSnapshot)
  const { preventionEnabled } = useAppSelector(selectControls)

  return (
    <Panel
      icon={deadlock ? ShieldAlertIcon : LockIcon}
      title={deadlock ? 'Deadlock detected' : 'Deadlock monitor'}
      subtitle={deadlock ? `Found at ${formatClock(deadlock.detectedAtMinutes)}` : undefined}
      alert={deadlock !== null}
      actions={<ConflictActions />}
    >
      {deadlock ? (
        <div className={styles.body}>
          <p className={styles.banner}>
            {deadlock.operations.length} operations are waiting on each other and none of them can move.
          </p>
          <WaitCycle operations={deadlock.operations} />
          <div className={styles.columns}>
            <Explanation lines={deadlock.explanation} />
            <Resolution operations={deadlock.operations} />
          </div>
        </div>
      ) : (
        <p className={styles.calm}>{preventionEnabled ? PREVENTION_ON : PREVENTION_OFF}</p>
      )}
    </Panel>
  )
}
