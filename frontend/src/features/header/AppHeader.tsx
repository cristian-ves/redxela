import { Badge, type Tone } from '../../components/Badge'
import { Button } from '../../components/Button'
import { formatClock } from '../../lib/format'
import { useAppDispatch, useAppSelector } from '../../store/hooks'
import { selectConnection, selectSnapshot, selectTheme } from '../../store/selectors'
import { themeToggled } from '../../store/uiSlice'
import styles from './AppHeader.module.css'

const CONNECTION_TONE: Record<string, Tone> = { open: 'ok', connecting: 'wait', closed: 'bad' }
const CONNECTION_TEXT: Record<string, string> = { open: 'Live', connecting: 'Connecting', closed: 'Offline' }

/* Application bar: name, simulated clock, connection state and theme switch. */
export function AppHeader() {
  const dispatch = useAppDispatch()
  const { clockMinutes, speed, counters, deadlock } = useAppSelector(selectSnapshot)
  const connection = useAppSelector(selectConnection)
  const theme = useAppSelector(selectTheme)

  return (
    <header className={styles.bar}>
      <div className={styles.inner}>
        <div>
          <h1 className={styles.name}>REDXela</h1>
          <p className={styles.tagline}>Distribution center simulator</p>
        </div>
        <div className={styles.status}>
          <span className="mono muted">
            {counters.submitted} submitted · {counters.completed} completed · {counters.waiting} waiting ·{' '}
            {counters.inProcess} in process
          </span>
          <span className={`mono ${styles.clock}`}>{formatClock(clockMinutes)}</span>
          <span className="mono muted">×{speed}</span>
          {deadlock && <Badge tone="bad">Deadlock</Badge>}
          <Badge tone={CONNECTION_TONE[connection]}>{CONNECTION_TEXT[connection]}</Badge>
          <Button small onClick={() => dispatch(themeToggled())}>
            {theme === 'dark' ? 'Cream theme' : 'Dark theme'}
          </Button>
        </div>
      </div>
    </header>
  )
}
