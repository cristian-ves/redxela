import { Panel } from '../../components/Panel'
import { useAppSelector } from '../../store/hooks'
import { selectReception } from '../../store/selectors'
import { ArrivalQueue } from './ArrivalQueue'
import { BufferSlots } from './BufferSlots'
import { Dock } from './Dock'
import { ProcessedList } from './ProcessedList'
import styles from './ReceptionPanel.module.css'

/* The inbound flow from left to right: queue, dock, bounded buffer, Inventory. */
export function ReceptionPanel() {
  const reception = useAppSelector(selectReception)
  return (
    <Panel title="Reception and Inventory" subtitle="Trucks are unloaded at the dock; Inventory stores them through a bounded buffer">
      <div className={styles.sections}>
        <ArrivalQueue queued={reception.queued} capacity={reception.queueCapacity} />
        <Dock truck={reception.current} />
        <BufferSlots buffer={reception.buffer} capacity={reception.bufferCapacity} />
        <ProcessedList processed={reception.processed} />
      </div>
    </Panel>
  )
}
