import { Badge } from '../../components/Badge'
import { formatDuration, formatPercent, orDash } from '../../lib/format'
import { useAppSelector } from '../../store/hooks'
import { selectMeta, selectStats } from '../../store/selectors'
import styles from './StatsPanel.module.css'

/* The three scheduling policies side by side. Each one only accumulates while it is the active policy. */
export function PolicyComparison() {
  const stats = useAppSelector(selectStats)
  const { policies } = useAppSelector(selectMeta)
  const labelOf = (id: string) => policies.find((policy) => policy.id === id)?.label ?? id

  return (
    <table>
      <thead>
        <tr>
          <th>Policy</th>
          <th>Completed</th>
          <th>Avg wait</th>
          <th>Avg processing</th>
          <th>Within target</th>
          <th>Per hour</th>
          <th>Resource use</th>
        </tr>
      </thead>
      <tbody>
        {stats.map((row) => {
          const any = row.completed > 0
          return (
            <tr key={row.policy} className={row.active ? styles.active : undefined}>
              <td>
                {labelOf(row.policy)} {row.active && <Badge tone="ok">Active</Badge>}
              </td>
              <td className="mono">{row.completed}</td>
              <td className="mono">{orDash(formatDuration(row.averageWaitMinutes), any)}</td>
              <td className="mono">{orDash(formatDuration(row.averageProcessingMinutes), any)}</td>
              <td className="mono">{orDash(formatPercent(row.withinTargetPercent), any)}</td>
              <td className="mono">{row.throughputPerHour.toFixed(1)}</td>
              <td className="mono">{formatPercent(row.averageUtilizationPercent)}</td>
            </tr>
          )
        })}
      </tbody>
    </table>
  )
}
