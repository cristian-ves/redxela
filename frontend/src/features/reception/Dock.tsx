import type { TruckView } from '../../api/types'
import { ProgressBar } from '../../components/ProgressBar'
import { Section } from '../../components/Section'
import { formatDuration } from '../../lib/format'
import { useAppSelector } from '../../store/hooks'
import { selectResourceLabels } from '../../store/selectors'
import { ResourceChips } from '../orders/ResourceChips'
import { TruckStatus } from './TruckStatus'
import styles from './ReceptionPanel.module.css'

/* The truck being unloaded right now. There is a single dock, so there is at most one. */
export function Dock({ truck }: { truck: TruckView | null }) {
  const labels = useAppSelector(selectResourceLabels)
  if (!truck) {
    return (
      <Section title="Dock">
        <span className="muted">Idle: no truck is being unloaded.</span>
      </Section>
    )
  }
  return (
    <Section title="Dock">
      <div className={styles.truck}>
        <div className={styles.truckTop}>
          <span className="mono">{truck.id}</span>
          <span>{truck.product} × {truck.units}</span>
          <TruckStatus status={truck.status} />
        </div>
        <div className={styles.meter}>
          <ProgressBar value={truck.progress} label={`${truck.id} unloading`} />
          <span className="mono muted">{Math.round(truck.progress * 100)} % · {formatDuration(truck.remainingMinutes)} left</span>
        </div>
        <ResourceChips types={truck.held} />
        {truck.waitingFor && <span className={styles.waits}>Waits for {labels[truck.waitingFor] ?? truck.waitingFor}</span>}
      </div>
    </Section>
  )
}
