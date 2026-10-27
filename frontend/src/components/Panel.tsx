import type { ReactNode } from 'react'
import styles from './Panel.module.css'

interface PanelProps {
  title: string
  subtitle?: string
  actions?: ReactNode
  alert?: boolean
  className?: string
  children: ReactNode
}

/* The card every section of the dashboard lives in. */
export function Panel({ title, subtitle, actions, alert = false, className, children }: PanelProps) {
  const classes = [styles.panel, alert ? styles.alert : '', className ?? ''].join(' ')
  return (
    <section className={classes}>
      <header className={styles.head}>
        <div>
          <h2 className={styles.title}>{title}</h2>
          {subtitle && <p className={styles.subtitle}>{subtitle}</p>}
        </div>
        {actions && <div className={styles.actions}>{actions}</div>}
      </header>
      {children}
    </section>
  )
}
