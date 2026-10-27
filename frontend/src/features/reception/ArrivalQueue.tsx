import type { TruckView } from '../../api/types'
import { Section } from '../../components/Section'
import styles from './ReceptionPanel.module.css'

const SHOWN = 10

/* Trucks waiting their turn at the dock, in arrival order. */
export function ArrivalQueue({ queued, capacity }: { queued: TruckView[]; capacity: number }) {
  return (
    <Section title="Arrival queue" note={`${queued.length} / ${capacity}`}>
      {queued.length === 0 ? (
        <span className="muted">No truck is waiting.</span>
      ) : (
        <div className={styles.chips}>
          {queued.slice(0, SHOWN).map((truck) => (
            <span key={truck.id} className={styles.chip}>{truck.id} · {truck.product} × {truck.units}</span>
          ))}
          {queued.length > SHOWN && <span className="muted">+{queued.length - SHOWN} more</span>}
        </div>
      )}
    </Section>
  )
}
