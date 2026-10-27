import type { OrderView } from '../../api/types'
import { Badge } from '../../components/Badge'
import { LevelBadge } from './LevelBadge'
import styles from './OrderCell.module.css'

/* The identifying cell every order table starts with: id and urgency on top, who and what below. */
export function OrderCell({ order }: { order: OrderView }) {
  return (
    <>
      <div className={styles.top}>
        <span className="num">{order.id}</span>
        <LevelBadge level={order.level} label={order.levelLabel} />
        {order.immediate && <Badge tone="mute">Immediate</Badge>}
      </div>
      <div className={styles.sub}>
        {order.clientName} · {order.typeLabel} · {order.totalUnits} {order.totalUnits === 1 ? 'unit' : 'units'}
      </div>
    </>
  )
}
