import styles from './ProgressBar.module.css'

interface ProgressBarProps {
  /* A fraction between 0 and 1. */
  value: number
  tone?: 'busy' | 'ok' | 'wait' | 'bad'
  label: string
}

export function ProgressBar({ value, tone = 'busy', label }: ProgressBarProps) {
  const percent = Math.round(Math.min(1, Math.max(0, value)) * 100)
  return (
    <div className={styles.track} role="progressbar" aria-label={label} aria-valuenow={percent} aria-valuemin={0} aria-valuemax={100}>
      <i className={`${styles.fill} ${styles[tone]}`} style={{ width: `${percent}%` }} />
    </div>
  )
}
