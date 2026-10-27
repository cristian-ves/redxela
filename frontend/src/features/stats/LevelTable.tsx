import type { PolicyStats } from '../../api/types'
import { formatDuration, formatPercent, orDash, titleCase } from '../../lib/format'
import { useAppSelector } from '../../store/hooks'
import { selectMeta } from '../../store/selectors'

/* Waiting time per service level against the target each level promises. */
export function LevelTable({ stats }: { stats: PolicyStats }) {
  const { levels } = useAppSelector(selectMeta)
  const targetOf = (key: string) => levels.find((level) => level.label.toUpperCase() === key)?.targetWaitMinutes

  return (
    <table>
      <thead>
        <tr>
          <th>Level</th>
          <th>Completed</th>
          <th>Avg wait</th>
          <th>Target</th>
          <th>Within target</th>
        </tr>
      </thead>
      <tbody>
        {stats.levels.map((row) => {
          const target = targetOf(row.level)
          const any = row.completed > 0
          return (
            <tr key={row.level}>
              <td>{titleCase(row.level)}</td>
              <td className="mono">{row.completed}</td>
              <td className="mono">{orDash(formatDuration(row.averageWaitMinutes), any)}</td>
              <td className="mono muted">{target === undefined ? '—' : `≤ ${formatDuration(target)}`}</td>
              <td className="mono">{orDash(formatPercent(row.withinTargetPercent), any)}</td>
            </tr>
          )
        })}
      </tbody>
    </table>
  )
}
