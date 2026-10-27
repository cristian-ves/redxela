import type { SlotState } from '../../api/types'
import { Badge } from '../../components/Badge'
import { useAppSelector } from '../../store/hooks'
import { selectMeta } from '../../store/selectors'
import styles from './LotDetail.module.css'

/* Number of separate runs of consecutive slot indices: 1 means the lot sits in one block. */
function countGroups(indices: number[]): number {
  return indices.filter((index, position) => position === 0 || index !== indices[position - 1] + 1).length
}

/* Describes the lot under the pointer: what it holds and whether its slots are contiguous. */
export function LotDetail({ lotId, slots }: { lotId: string | null; slots: SlotState[] }) {
  const { products } = useAppSelector(selectMeta)
  const lot = lotId === null ? [] : slots.filter((slot) => slot.lotId === lotId)

  if (lotId === null || lot.length === 0) {
    return (
      <p className={styles.detail}>
        <span className="muted">
          Hover a slot to see its lot. A lot can be split across non-contiguous slots when the free space is fragmented.
        </span>
      </p>
    )
  }
  const sku = lot[0].sku ?? ''
  const units = lot.reduce((sum, slot) => sum + slot.units, 0)
  const groups = countGroups(lot.map((slot) => slot.index))
  const name = products.find((product) => product.sku === sku)?.name ?? sku

  return (
    <div className={styles.detail}>
      <div className={styles.top}>
        <span className="mono">{lotId}</span>
        <span>{name} <span className="mono muted">{sku}</span></span>
        <span className="muted">{units} units in {lot.length} slot{lot.length === 1 ? '' : 's'}</span>
        <Badge tone={groups === 1 ? 'ok' : 'wait'}>{groups === 1 ? 'Contiguous' : `Split into ${groups} groups`}</Badge>
      </div>
      <div className={styles.where}>{lot.map((slot) => slot.location).join(' · ')}</div>
    </div>
  )
}
