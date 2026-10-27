import type { ReactNode } from 'react'
import styles from './Section.module.css'

interface SectionProps {
  title: string
  note?: ReactNode
  children: ReactNode
}

/* A titled block inside a panel, for panels that show several related things. */
export function Section({ title, note, children }: SectionProps) {
  return (
    <section className={styles.section}>
      <h3 className={styles.heading}>
        <span>{title}</span>
        {note && <span className={styles.note}>{note}</span>}
      </h3>
      {children}
    </section>
  )
}
