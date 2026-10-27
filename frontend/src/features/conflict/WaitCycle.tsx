import { Fragment } from 'react'
import type { InvolvedOperation } from '../../api/types'
import { cycleOrder } from './cycle'
import { OperationCard } from './OperationCard'
import { WaitLink } from './WaitLink'
import styles from './WaitCycle.module.css'

/* The circular wait drawn as a chain that closes on itself: each operation waits for the next one. */
export function WaitCycle({ operations }: { operations: InvolvedOperation[] }) {
  const ordered = cycleOrder(operations)
  return (
    <div className={styles.cycle}>
      {ordered.map((operation, index) => (
        <Fragment key={operation.id}>
          <OperationCard operation={operation} />
          <WaitLink operation={operation} closing={index === ordered.length - 1} />
        </Fragment>
      ))}
    </div>
  )
}
