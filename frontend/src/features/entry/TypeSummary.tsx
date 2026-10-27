import type { Meta } from '../../api/types'
import { useAppSelector } from '../../store/hooks'
import { selectResourceLabels } from '../../store/selectors'
import styles from './EntryForm.module.css'

/* What a merchandise type is and which resources every order of that type needs. */
export function TypeSummary({ type }: { type: Meta['types'][number] }) {
  const labels = useAppSelector(selectResourceLabels)
  const needs = type.requirements
    .map((need) => `${need.units} × ${labels[need.resource] ?? need.resource}`)
    .join(' · ')
  return (
    <p className={styles.hint}>
      {type.description} Needs: {needs}.
    </p>
  )
}
