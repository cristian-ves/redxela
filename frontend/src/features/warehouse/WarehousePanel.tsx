import { useState } from 'react'
import { Panel } from '../../components/Panel'
import { useAppSelector } from '../../store/hooks'
import { selectWarehouse } from '../../store/selectors'
import { LotDetail } from './LotDetail'
import { SLOT_CAPACITY_UNITS, SlotGrid } from './SlotGrid'
import styles from './SlotGrid.module.css'

/* The paged warehouse: every slot, who occupies it, and where each lot has been placed. */
export function WarehousePanel() {
  const slots = useAppSelector(selectWarehouse)
  const [lotId, setLotId] = useState<string | null>(null)
  const used = slots.filter((slot) => slot.lotId !== null).length

  return (
    <Panel
      title="Warehouse"
      subtitle={`${used} of ${slots.length} slots in use · a slot holds up to ${SLOT_CAPACITY_UNITS} units`}
    >
      <SlotGrid slots={slots} highlighted={lotId} onHover={setLotId} />
      <div className={styles.legend}>
        <span><i className={styles.swatch} />Free</span>
        <span><i className={styles.swatch} style={{ background: 'var(--busy)' }} />Stored (bar height = fill)</span>
        <span><i className={styles.swatch} style={{ background: 'var(--ok)' }} />Selected lot</span>
      </div>
      <LotDetail lotId={lotId} slots={slots} />
    </Panel>
  )
}
