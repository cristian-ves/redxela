import type { LucideIcon } from 'lucide-react'
import type { ReactNode } from 'react'
import styles from './Panel.module.css'

interface PanelProps {
  title: string
  icon: LucideIcon
  subtitle?: string
  actions?: ReactNode
  alert?: boolean
  className?: string
  children: ReactNode
}

/* The card every section of the dashboard lives in: an icon, a title, an optional subtitle and actions. */
export function Panel({ title, icon: Icon, subtitle, actions, alert = false, className, children }: PanelProps) {
  const classes = [styles.panel, alert ? styles.alert : '', className ?? ''].join(' ')
  return (
    <section className={classes}>
      <header className={styles.head}>
        <span className={styles.icon}>
          <Icon size={18} strokeWidth={1.75} aria-hidden="true" />
        </span>
        <div className={styles.heading}>
          <h2 className={styles.title}>{title}</h2>
          {subtitle && <p className={styles.subtitle}>{subtitle}</p>}
        </div>
        {actions && <div className={styles.actions}>{actions}</div>}
      </header>
      {children}
    </section>
  )
}
