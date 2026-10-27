import type { ReactNode } from 'react'
import styles from './Badge.module.css'

export type Tone = 'ok' | 'busy' | 'wait' | 'bad' | 'mute'

interface BadgeProps {
  tone?: Tone
  children: ReactNode
}

export function Badge({ tone = 'busy', children }: BadgeProps) {
  return <span className={`${styles.badge} ${styles[tone]}`}>{children}</span>
}
