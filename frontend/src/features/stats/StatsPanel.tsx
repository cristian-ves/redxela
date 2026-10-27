import { ChartColumnIcon } from 'lucide-react'
import { Empty } from '../../components/Empty'
import { Panel } from '../../components/Panel'
import { Scroll } from '../../components/Scroll'
import { useAppSelector } from '../../store/hooks'
import { selectStats } from '../../store/selectors'
import { LevelTable } from './LevelTable'
import { PolicyComparison } from './PolicyComparison'
import { UtilizationBars } from './UtilizationBars'
import styles from './StatsPanel.module.css'

/* Performance figures: the policy comparison, then the detail of the policy that is running now. */
export function StatsPanel() {
  const stats = useAppSelector(selectStats)
  const active = stats.find((row) => row.active)

  return (
    <Panel icon={ChartColumnIcon} title="Statistics" subtitle="Measured per policy since the last reset; times are simulated minutes, rates count only the time with open orders">
      {stats.length === 0 || !active ? (
        <Empty>No measurements yet.</Empty>
      ) : (
        <div className={styles.sections}>
          <section>
            <h3 className={styles.heading}>Policy comparison</h3>
            <Scroll><PolicyComparison /></Scroll>
          </section>
          <div className={styles.columns}>
            <section>
              <h3 className={styles.heading}>Service levels (active policy)</h3>
              <Scroll><LevelTable stats={active} /></Scroll>
            </section>
            <section>
              <h3 className={styles.heading}>Occupancy (active policy)</h3>
              <UtilizationBars stats={active} />
            </section>
          </div>
        </div>
      )}
    </Panel>
  )
}
