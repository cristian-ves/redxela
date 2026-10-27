import { api } from '../../api/http'
import type { InvolvedOperation } from '../../api/types'
import { Button } from '../../components/Button'
import { Section } from '../../components/Section'
import { useCommand } from '../../hooks/useCommand'
import { useAppDispatch } from '../../store/hooks'
import { noticeShown } from '../../store/uiSlice'
import styles from './ConflictPanel.module.css'

/*
 * Manual resolution by rollback: the order the operator picks keeps its
 * resources; every other order releases what it holds and returns to the
 * waiting list. Only orders can be picked, not trucks.
 */
export function Resolution({ operations }: { operations: InvolvedOperation[] }) {
  const run = useCommand()
  const dispatch = useAppDispatch()

  const resolve = async (winnerId: string) => {
    const result = await run(() => api.resolveConflict(winnerId))
    if (!result) return
    const text = `${winnerId} continues; ${result.returned.join(', ')} went back to the waiting list`
    dispatch(noticeShown({ kind: 'success', text }))
  }

  return (
    <Section title="Resolve it">
      <p className={styles.hint}>
        Pick the order that continues. The other orders give back everything they hold and wait until it has finished.
      </p>
      <div className={styles.buttons}>
        {operations.filter((operation) => operation.order).map((operation) => (
          <Button key={operation.id} variant="primary" onClick={() => resolve(operation.id)}>
            Continue {operation.id}
          </Button>
        ))}
      </div>
    </Section>
  )
}
