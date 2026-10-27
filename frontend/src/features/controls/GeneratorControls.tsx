import { useState } from 'react'
import { api } from '../../api/http'
import { Button } from '../../components/Button'
import { useCommand } from '../../hooks/useCommand'
import { useAppSelector } from '../../store/hooks'
import { selectControls } from '../../store/selectors'
import styles from './ControlsPanel.module.css'

/* Start and stop the automatic order generator, with its rate and an optional seed. */
export function GeneratorControls() {
  const run = useCommand()
  const { generatorRunning, ordersPerMinute, seed } = useAppSelector(selectControls)
  const [rate, setRate] = useState(String(ordersPerMinute))
  const [seedText, setSeedText] = useState(seed === null ? '' : String(seed))

  const start = () => {
    const parsedSeed = seedText.trim() === '' ? null : Number(seedText)
    return run(() => api.startGenerator(parsedSeed, Number(rate)), 'Generator started')
  }

  return (
    <div className={styles.field}>
      <span className={styles.label}>Order generator</span>
      <div className={styles.row}>
        <label className={styles.inline}>
          <span className="muted">Orders / min</span>
          <input className={styles.input} type="number" min="0.1" max="5" step="0.1" value={rate}
            onChange={(event) => setRate(event.target.value)} />
        </label>
        <label className={styles.inline}>
          <span className="muted">Seed</span>
          <input className={styles.input} type="number" placeholder="default" value={seedText}
            disabled={generatorRunning} onChange={(event) => setSeedText(event.target.value)} />
        </label>
      </div>
      <div className={styles.row}>
        {generatorRunning ? (
          <>
            <Button small onClick={() => run(() => api.setRate(Number(rate)), 'Rate updated')}>Apply rate</Button>
            <Button small variant="danger" onClick={() => run(api.stopGenerator, 'Generator stopped')}>Stop</Button>
          </>
        ) : (
          <Button small variant="primary" onClick={start}>Start generator</Button>
        )}
      </div>
    </div>
  )
}
