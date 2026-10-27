/* Presentation helpers. All times in the application are simulated minutes. */

export function formatClock(minutes: number): string {
  const total = Math.max(0, Math.floor(minutes))
  const day = Math.floor(total / 1440) + 1
  const hours = String(Math.floor((total % 1440) / 60)).padStart(2, '0')
  const mins = String(total % 60).padStart(2, '0')
  return `Day ${day} · ${hours}:${mins}`
}

export function formatDuration(minutes: number): string {
  if (minutes < 1) return `${Math.round(minutes * 60)} s`
  if (minutes < 60) return `${minutes.toFixed(1)} min`
  return `${Math.floor(minutes / 60)} h ${Math.round(minutes % 60)} min`
}

export function formatPercent(value: number): string {
  return `${value.toFixed(1)} %`
}

/* Level 1 is the most urgent. */
export function levelTone(level: number): 'bad' | 'wait' | 'busy' | 'mute' {
  if (level === 1) return 'bad'
  if (level === 2) return 'wait'
  return level === 3 ? 'busy' : 'mute'
}

/* "EXPRESS" -> "Express". */
export function titleCase(text: string): string {
  return text.charAt(0).toUpperCase() + text.slice(1).toLowerCase()
}

/* Averages mean nothing before the first order is completed, so they are shown as a dash. */
export function orDash(value: string, hasData: boolean): string {
  return hasData ? value : '—'
}
