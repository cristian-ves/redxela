import type { Meta, StockView } from '../../api/types'
import { MAX_UNITS_PER_LINE } from './orderDraft'
import styles from './OrderLines.module.css'

interface OrderLinesProps {
  products: Meta['products']
  stock: StockView[]
  typed: Record<string, string>
  onChange: (sku: string, text: string) => void
}

/* One row per product of the chosen type: leave the box empty to leave the product out of the order. */
export function OrderLines({ products, stock, typed, onChange }: OrderLinesProps) {
  return (
    <ul className={styles.lines}>
      {products.map((product) => {
        const available = stock.find((item) => item.sku === product.sku)?.available
        return (
          <li key={product.sku} className={styles.line}>
            <span>
              {product.name} <span className="mono muted">{product.sku}</span>
            </span>
            <span className="muted">{available === undefined ? '' : `${available} available`}</span>
            <input
              type="number"
              min="0"
              max={MAX_UNITS_PER_LINE}
              placeholder="0"
              aria-label={`Units of ${product.name}`}
              value={typed[product.sku] ?? ''}
              onChange={(event) => onChange(product.sku, event.target.value)}
            />
          </li>
        )
      })}
    </ul>
  )
}
