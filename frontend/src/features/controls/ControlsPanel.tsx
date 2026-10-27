import { useState } from 'react'
import { SlidersHorizontalIcon } from 'lucide-react'
import { api } from '../../api/http'
import type { PolicyId } from '../../api/types'
import { Button } from '../../components/Button'
import { Panel } from '../../components/Panel'
import { Segmented } from '../../components/Segmented'
import { Toggle } from '../../components/Toggle'
import { useCommand } from '../../hooks/useCommand'
import { useAppSelector } from '../../store/hooks'
import { selectControls, selectMeta, selectSnapshot } from '../../store/selectors'
import { GeneratorControls } from './GeneratorControls'
import styles from './ControlsPanel.module.css'

const SPEEDS = [1, 2, 5, 10, 20].map((value) => ({ value, label: `×${value}` }))

/* Simulation-wide settings: speed, scheduling policy, order generator and supplier. */
export function ControlsPanel() {
  const run = useCommand()
  const controls = useAppSelector(selectControls)
  const { speed } = useAppSelector(selectSnapshot)
  const { policies } = useAppSelector(selectMeta)
  const [busy, setBusy] = useState(false)
  const criterion = policies.find((policy) => policy.id === controls.policy)?.criterion

  const resetStats = async () => {
    setBusy(true)
    await run(api.resetStats, 'Statistics reset')
    setBusy(false)
  }

  return (
    <Panel icon={SlidersHorizontalIcon} title="Simulation controls" subtitle="1 real second = 1 simulated minute × speed">
      <div className={styles.grid}>
        <div className={styles.field}>
          <span className={styles.label}>Speed</span>
          <Segmented label="Speed" options={SPEEDS} value={speed} onChange={(value) => run(() => api.setSpeed(value))} />
        </div>
        <div className={styles.field}>
          <span className={styles.label}>Scheduling policy</span>
          <Segmented
            label="Scheduling policy"
            options={policies.map((policy) => ({ value: policy.id, label: policy.label }))}
            value={controls.policy}
            onChange={(value: PolicyId) => run(() => api.selectPolicy(value))}
          />
          <span className={styles.hint}>{criterion}</span>
        </div>
        <GeneratorControls />
        <div className={styles.field}>
          <span className={styles.label}>Supplier and statistics</span>
          <Toggle
            label="Automatic replenishment"
            checked={controls.replenisherEnabled}
            onChange={(enabled) => run(() => api.setReplenisher(enabled))}
          />
          <Button small disabled={busy} onClick={resetStats}>
            Reset statistics
          </Button>
        </div>
      </div>
    </Panel>
  )
}
