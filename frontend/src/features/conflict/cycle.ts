import type { InvolvedOperation } from '../../api/types'

/*
 * Puts the operations of a deadlock in wait-chain order (A waits for B, B waits
 * for C, ...), so the circular wait reads from left to right and closes on
 * itself. If the chain cannot be followed through every operation, the order
 * the server sent is kept.
 */
export function cycleOrder(operations: InvolvedOperation[]): InvolvedOperation[] {
  const byId = new Map(operations.map((operation) => [operation.id, operation]))
  const ordered: InvolvedOperation[] = []
  const seen = new Set<string>()
  let current: InvolvedOperation | undefined = operations[0]
  while (current && !seen.has(current.id)) {
    ordered.push(current)
    seen.add(current.id)
    const next: string | undefined = current.waitingOn.find((id) => byId.has(id) && !seen.has(id))
    current = next === undefined ? undefined : byId.get(next)
  }
  return ordered.length === operations.length ? ordered : operations
}
