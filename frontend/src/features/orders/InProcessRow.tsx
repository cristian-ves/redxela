import type { OrderView } from '../../api/types'
import { ProgressBar } from '../../components/ProgressBar'
import { formatDuration } from '../../lib/format'
import { useAppSelector } from '../../store/hooks'
import { selectResourceLabels } from '../../store/selectors'
import { OrderCell } from './OrderCell'
import { ResourceChips } from './ResourceChips'
import { StatusBadge } from './StatusBadge'
import styles from './InProcessRow.module.css'

/* One order that holds or is acquiring resources. A blocked order keeps what it has and shows what it lacks. */
export function InProcessRow({ order }: { order: OrderView }) {
  const labels = useAppSelector(selectResourceLabels)
  const blocked = order.status === 'BLOCKED'
  const percent = Math.round(order.progress * 100)

  return (
    <tr>
      <td><OrderCell order={order} /></td>
      <td><StatusBadge status={order.status} /></td>
      <td>
        <div className={styles.progress}>
          <ProgressBar value={order.progress} tone={blocked ? 'bad' : 'busy'} label={`${order.id} progress`} />
          <span className="num muted">{percent} %</span>
        </div>
      </td>
      <td className="num">{blocked ? '—' : formatDuration(order.remainingMinutes)}</td>
      <td>
        <ResourceChips types={order.held} />
        {order.waitingFor && (
          <div className={styles.waits}>Waits for {labels[order.waitingFor] ?? order.waitingFor}</div>
        )}
      </td>
    </tr>
  )
}
