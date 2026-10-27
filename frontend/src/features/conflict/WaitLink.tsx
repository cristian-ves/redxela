import type { InvolvedOperation } from '../../api/types'
import { useAppSelector } from '../../store/hooks'
import { selectResourceLabels } from '../../store/selectors'
import styles from './WaitCycle.module.css'

/* The arrow out of an operation: what it is waiting for, and who holds it. */
export function WaitLink({ operation, closing }: { operation: InvolvedOperation; closing: boolean }) {
  const labels = useAppSelector(selectResourceLabels)
  const need = operation.waitingFor
    ? `${labels[operation.waitingFor] ?? operation.waitingFor} ×${operation.waitingForUnits}`
    : 'nothing'
  return (
    <div className={styles.link}>
      <span className={styles.caption}>waits for</span>
      <span className={styles.need}>{need}</span>
      <span className={styles.arrow} aria-hidden="true">{closing ? '↩' : '→'}</span>
      <span className={styles.caption}>held by {operation.waitingOn.join(', ') || '—'}</span>
    </div>
  )
}
