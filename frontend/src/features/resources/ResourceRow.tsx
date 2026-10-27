import type { ResourceView } from '../../api/types'
import { ProgressBar } from '../../components/ProgressBar'
import { Owners } from './Owners'
import styles from './ResourceRow.module.css'

/* One resource type: how much of it is in use, who holds it and who is waiting for it. */
export function ResourceRow({ resource }: { resource: ResourceView }) {
  const used = resource.capacity - resource.available
  const exhausted = resource.available === 0

  return (
    <li className={styles.row}>
      <div className={styles.line}>
        <span className={styles.name}>{resource.label}</span>
        <span className="num muted">
          {used} / {resource.capacity} in use
        </span>
      </div>
      <ProgressBar value={used / resource.capacity} tone={exhausted ? 'wait' : 'busy'} label={resource.label} />
      <Owners title="Using" holdings={resource.holders} />
      <Owners title="Waiting" holdings={resource.waiters} waiting />
    </li>
  )
}
