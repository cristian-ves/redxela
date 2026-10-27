import type { PolicyStats } from '../../api/types'
import { ProgressBar } from '../../components/ProgressBar'
import { formatPercent } from '../../lib/format'
import { useAppSelector } from '../../store/hooks'
import { selectResources, selectWarehouse } from '../../store/selectors'
import styles from './StatsPanel.module.css'

/* Average use of each resource under the active policy, plus how full the warehouse is right now. */
export function UtilizationBars({ stats }: { stats: PolicyStats }) {
  const resources = useAppSelector(selectResources)
  const warehouse = useAppSelector(selectWarehouse)
  const occupied = warehouse.filter((slot) => slot.lotId !== null).length
  const occupancy = warehouse.length === 0 ? 0 : occupied / warehouse.length

  return (
    <ul className={styles.bars}>
      {resources.map((resource) => {
        const percent = stats.utilizationPercent[resource.type] ?? 0
        return (
          <li key={resource.type} className={styles.bar}>
            <span>{resource.label}</span>
            <ProgressBar value={percent / 100} label={`${resource.label} use`} />
            <span className="num">{formatPercent(percent)}</span>
          </li>
        )
      })}
      <li className={styles.bar}>
        <span>Warehouse slots</span>
        <ProgressBar value={occupancy} tone="ok" label="Warehouse occupancy" />
        <span className="num">
          {occupied} / {warehouse.length}
        </span>
      </li>
    </ul>
  )
}
