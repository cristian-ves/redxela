import { Badge } from '../../components/Badge'
import { levelTone } from '../../lib/format'

export function LevelBadge({ level, label }: { level: number; label: string }) {
  return <Badge tone={levelTone(level)}>{label}</Badge>
}
