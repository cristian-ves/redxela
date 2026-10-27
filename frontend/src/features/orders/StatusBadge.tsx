import { Badge, type Tone } from '../../components/Badge'
import type { OrderStatus } from '../../api/types'

const TONE: Record<OrderStatus, Tone> = { WAITING: 'wait', PROCESSING: 'busy', BLOCKED: 'bad', COMPLETED: 'ok' }
const TEXT: Record<OrderStatus, string> = {
  WAITING: 'Waiting',
  PROCESSING: 'Processing',
  BLOCKED: 'Blocked',
  COMPLETED: 'Completed',
}

export function StatusBadge({ status }: { status: OrderStatus }) {
  return <Badge tone={TONE[status]}>{TEXT[status]}</Badge>
}
