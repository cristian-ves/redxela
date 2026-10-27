import type { CSSProperties } from 'react'
import type { ArrivalMessage } from '../../api/types'
import { Badge } from '../../components/Badge'
import { Section } from '../../components/Section'
import styles from './ReceptionPanel.module.css'

/*
 * The bounded buffer between Reception (producer) and Inventory (consumer).
 * When every slot is taken the dock has to wait, which is the producer-consumer back-pressure.
 */
export function BufferSlots({ buffer, capacity }: { buffer: ArrivalMessage[]; capacity: number }) {
  const full = capacity > 0 && buffer.length >= capacity
  const note = (
    <>
      {full && <Badge tone="wait">Full: the dock waits</Badge>} {buffer.length} / {capacity}
    </>
  )
  return (
    <Section title="Buffer to Inventory" note={note}>
      <div className={styles.slots} style={{ '--slots': capacity } as CSSProperties}>
        {Array.from({ length: capacity }, (_, index) => {
          const message = buffer[index]
          return message ? (
            <div key={message.receptionId} className={`${styles.slot} ${styles.full}`}>
              <span>{message.receptionId}</span>
              <span>{message.sku} × {message.units}</span>
            </div>
          ) : (
            <div key={`free-${index}`} className={styles.slot}>free</div>
          )
        })}
      </div>
    </Section>
  )
}
