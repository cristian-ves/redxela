import type { InvolvedOperation, ResourceTypeId } from '../../api/types'
import { Badge } from '../../components/Badge'
import { useAppSelector } from '../../store/hooks'
import { selectResourceLabels } from '../../store/selectors'
import styles from './WaitCycle.module.css'

/* One operation in the deadlock, with the resources it is holding while it waits. */
export function OperationCard({ operation }: { operation: InvolvedOperation }) {
  const labels = useAppSelector(selectResourceLabels)
  const holds = Object.entries(operation.holds) as [ResourceTypeId, number][]
  return (
    <div className={styles.card}>
      <div className={styles.cardTop}>
        <span className="num">{operation.id}</span>
        <Badge tone="mute">{operation.order ? 'Order' : 'Truck'}</Badge>
      </div>
      <span className={styles.caption}>Holds</span>
      <div className={styles.chips}>
        {holds.map(([type, units]) => (
          <span key={type} className={styles.chip}>
            {labels[type] ?? type} ×{units}
          </span>
        ))}
      </div>
    </div>
  )
}
