import type { Holding } from '../../api/types'
import styles from './Owners.module.css'

interface OwnersProps {
  title: string
  holdings: Holding[]
  waiting?: boolean
}

/* A labelled list of operations (orders or trucks) that use, or are waiting for, a resource. */
export function Owners({ title, holdings, waiting = false }: OwnersProps) {
  if (waiting && holdings.length === 0) return null
  return (
    <div className={styles.owners}>
      <span className={styles.title}>{title}</span>
      {holdings.length === 0 && <span className={styles.none}>idle</span>}
      {holdings.map((holding) => (
        <span key={holding.owner} className={`${styles.chip} ${waiting ? styles.waiting : ''}`}>
          {holding.owner}
          {holding.units > 1 ? ` ×${holding.units}` : ''}
        </span>
      ))}
    </div>
  )
}
