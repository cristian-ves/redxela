import type { ReactNode } from 'react'
import styles from './Field.module.css'

interface FieldProps {
  label: string
  hint?: string
  children: ReactNode
}

/* A form control with its label above and an optional explanation below. */
export function Field({ label, hint, children }: FieldProps) {
  return (
    <label className={styles.field}>
      <span className={styles.label}>{label}</span>
      {children}
      {hint && <span className={styles.hint}>{hint}</span>}
    </label>
  )
}
