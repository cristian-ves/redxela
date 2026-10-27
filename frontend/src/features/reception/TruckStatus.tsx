import { Badge, type Tone } from '../../components/Badge'
import type { ReceptionStatus } from '../../api/types'

const TONE: Record<ReceptionStatus, Tone> = {
  QUEUED: 'mute',
  RECEIVING: 'busy',
  BLOCKED: 'bad',
  WAITING_FOR_INVENTORY: 'wait',
  SENT: 'ok',
}

const TEXT: Record<ReceptionStatus, string> = {
  QUEUED: 'Queued',
  RECEIVING: 'Unloading',
  BLOCKED: 'Blocked',
  WAITING_FOR_INVENTORY: 'Waiting for Inventory',
  SENT: 'Sent',
}

export function TruckStatus({ status }: { status: ReceptionStatus }) {
  return <Badge tone={TONE[status]}>{TEXT[status]}</Badge>
}
