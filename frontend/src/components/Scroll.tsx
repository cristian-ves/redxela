import type { ReactNode } from 'react'
import styles from './Scroll.module.css'

interface ScrollProps {
  maxHeight?: number
  children: ReactNode
}

/* Lets a wide or long table scroll inside its panel while its header row stays in view. */
export function Scroll({ maxHeight, children }: ScrollProps) {
  return (
    <div className={styles.scroll} style={maxHeight ? { maxHeight } : undefined}>
      {children}
    </div>
  )
}
