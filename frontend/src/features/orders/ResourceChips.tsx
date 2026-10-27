import type { ResourceTypeId } from '../../api/types'
import { useAppSelector } from '../../store/hooks'
import { selectResourceLabels } from '../../store/selectors'
import styles from './ResourceChips.module.css'

/* A compact list of resource names, used for what an order holds. */
export function ResourceChips({ types }: { types: ResourceTypeId[] }) {
  const labels = useAppSelector(selectResourceLabels)
  if (types.length === 0) return <span className="muted">nothing yet</span>
  return (
    <span className={styles.chips}>
      {types.map((type, index) => (
        <span key={`${type}-${index}`} className={styles.chip}>
          {labels[type] ?? type}
        </span>
      ))}
    </span>
  )
}
