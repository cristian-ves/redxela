import type { SlotState } from '../../api/types'
import styles from './SlotGrid.module.css'

const SLOTS_PER_LEVEL = 5
const LEVELS = 3
const SLOTS_PER_AISLE = SLOTS_PER_LEVEL * LEVELS
export const SLOT_CAPACITY_UNITS = 10

interface SlotGridProps {
  slots: SlotState[]
  highlighted: string | null
  onHover: (lotId: string | null) => void
}

/* Neighbouring lots get alternating shades so their boundaries are visible without hovering. */
function tint(lotId: string | null): string {
  if (lotId === null) return ''
  return Number(lotId.replace(/\D/g, '')) % 2 === 0 ? styles.tintA : styles.tintB
}

function cellClass(slot: SlotState, highlighted: string | null): string {
  const state = highlighted === null ? '' : slot.lotId === highlighted ? styles.lit : styles.dim
  return `${styles.cell} ${tint(slot.lotId)} ${state}`
}

/* The physical layout: one row per aisle, three shelf levels of five positions each. */
export function SlotGrid({ slots, highlighted, onHover }: SlotGridProps) {
  const aisles = Math.ceil(slots.length / SLOTS_PER_AISLE)
  return (
    <div className={styles.grid}>
      <div className={styles.row}>
        <span />
        {Array.from({ length: LEVELS }, (_, level) => (
          <span key={level} className={styles.levelName}>Level {level + 1}</span>
        ))}
      </div>
      {Array.from({ length: aisles }, (_, aisle) => (
        <div key={aisle} className={styles.row}>
          <span className={styles.aisle}>A{aisle + 1}</span>
          {Array.from({ length: LEVELS }, (_, level) => {
            const start = aisle * SLOTS_PER_AISLE + level * SLOTS_PER_LEVEL
            return (
              <div key={level} className={styles.level}>
                {slots.slice(start, start + SLOTS_PER_LEVEL).map((slot) => (
                  <button
                    key={slot.index}
                    type="button"
                    className={cellClass(slot, highlighted)}
                    aria-label={slot.lotId ? `${slot.location}: ${slot.lotId}, ${slot.units} units` : `${slot.location}: free`}
                    onMouseEnter={() => onHover(slot.lotId)}
                    onFocus={() => onHover(slot.lotId)}
                    onMouseLeave={() => onHover(null)}
                    onBlur={() => onHover(null)}
                  >
                    {slot.lotId && <i className={styles.fill} style={{ height: `${(slot.units / SLOT_CAPACITY_UNITS) * 100}%` }} />}
                  </button>
                ))}
              </div>
            )
          })}
        </div>
      ))}
    </div>
  )
}
