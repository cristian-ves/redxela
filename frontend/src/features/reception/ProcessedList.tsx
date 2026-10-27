import type { ProcessedArrival } from '../../api/types'
import { Badge } from '../../components/Badge'
import { Section } from '../../components/Section'
import styles from './ReceptionPanel.module.css'

const SHOWN = 6

function outcomeText(arrival: ProcessedArrival): string {
  if (arrival.outcome === 'STORED') return `${arrival.lotId} · ${arrival.locations.length} slot${arrival.locations.length === 1 ? '' : 's'}`
  return arrival.outcome.replace(/^REJECTED:\s*/, '')
}

/* What Inventory did with each delivery, newest first. */
export function ProcessedList({ processed }: { processed: ProcessedArrival[] }) {
  return (
    <Section title="Stored by Inventory">
      {processed.length === 0 ? (
        <span className="muted">Nothing has been stored yet.</span>
      ) : (
        <table>
          <tbody>
            {processed.slice(0, SHOWN).map((arrival) => {
              const stored = arrival.outcome === 'STORED'
              return (
                <tr key={arrival.receptionId}>
                  <td className="num">{arrival.receptionId}</td>
                  <td>{arrival.sku} × {arrival.units}</td>
                  <td><Badge tone={stored ? 'ok' : 'bad'}>{stored ? 'Stored' : 'Rejected'}</Badge></td>
                  <td className={styles.where}>{outcomeText(arrival)}</td>
                </tr>
              )
            })}
          </tbody>
        </table>
      )}
    </Section>
  )
}
