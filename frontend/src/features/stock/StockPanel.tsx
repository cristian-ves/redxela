import { BoxesIcon } from 'lucide-react'
import { Panel } from '../../components/Panel'
import { Scroll } from '../../components/Scroll'
import { useAppSelector } from '../../store/hooks'
import { selectMeta, selectStock } from '../../store/selectors'
import styles from './StockPanel.module.css'

const LOW_STOCK = 10

/* Units per product: in the warehouse, promised to admitted orders, and still free to promise. */
export function StockPanel() {
  const stock = useAppSelector(selectStock)
  const { types, products } = useAppSelector(selectMeta)
  const typeOf = (sku: string) => {
    const typeId = products.find((product) => product.sku === sku)?.type
    return types.find((type) => type.id === typeId)?.label ?? '—'
  }
  const onHand = stock.reduce((sum, item) => sum + item.onHand, 0)
  const reserved = stock.reduce((sum, item) => sum + item.reserved, 0)

  return (
    <Panel icon={BoxesIcon} title="Stock" subtitle={`${onHand} units in the warehouse, ${reserved} reserved by admitted orders`}>
      <Scroll maxHeight={420}>
        <table>
          <thead>
            <tr>
              <th>Product</th>
              <th>Type</th>
              <th>On hand</th>
              <th>Reserved</th>
              <th>Available</th>
            </tr>
          </thead>
          <tbody>
            {stock.map((item) => (
              <tr key={item.sku}>
                <td>{item.name} <span className="num muted">{item.sku}</span></td>
                <td className="muted">{typeOf(item.sku)}</td>
                <td className="num">{item.onHand}</td>
                <td className="num">{item.reserved}</td>
                <td className={`num ${item.available === 0 ? styles.out : item.available < LOW_STOCK ? styles.low : ''}`}>
                  {item.available}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </Scroll>
    </Panel>
  )
}
