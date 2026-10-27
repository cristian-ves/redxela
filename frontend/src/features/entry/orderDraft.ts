/* Client-side checks that mirror the server limits, so a mistake is caught before a request is sent. */
export const MAX_UNITS_PER_LINE = 100
export const MAX_UNITS_PER_TRUCK = 200

export interface DraftLine {
  sku: string
  units: number
}

export interface Draft {
  lines: DraftLine[]
  /* True when some box holds something that is not a whole number within the allowed range. */
  invalid: boolean
}

/* A whole number from 1 to max, or null. */
export function parseUnits(text: string, max: number): number | null {
  if (!/^\d+$/.test(text.trim())) return null
  const value = Number(text)
  return value >= 1 && value <= max ? value : null
}

/* Turns the text typed next to each product into order lines; empty boxes are simply not part of the order. */
export function buildDraft(skus: string[], typed: Record<string, string>): Draft {
  const lines: DraftLine[] = []
  let invalid = false
  for (const sku of skus) {
    const text = (typed[sku] ?? '').trim()
    if (text === '') continue
    const units = parseUnits(text, MAX_UNITS_PER_LINE)
    if (units === null) invalid = true
    else lines.push({ sku, units })
  }
  return { lines, invalid }
}
