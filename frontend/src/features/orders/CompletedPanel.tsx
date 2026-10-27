import { Empty } from '../../components/Empty'
import { Panel } from '../../components/Panel'
import { Scroll } from '../../components/Scroll'
import { formatDuration } from '../../lib/format'
import { useAppSelector } from '../../store/hooks'
import { selectCompleted } from '../../store/selectors'
import { OrderCell } from './OrderCell'

/* The most recent orders to finish, newest first. */
export function CompletedPanel() {
  const orders = useAppSelector(selectCompleted)
  return (
    <Panel title="Completed orders" subtitle="Latest first">
      {orders.length === 0 ? (
        <Empty>Nothing has been completed yet.</Empty>
      ) : (
        <Scroll maxHeight={420}>
          <table>
            <thead>
              <tr>
                <th>Order</th>
                <th>Waited</th>
                <th>Processed</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => (
                <tr key={order.id}>
                  <td><OrderCell order={order} /></td>
                  <td className="mono">{formatDuration(order.waitedMinutes)}</td>
                  <td className="mono">{formatDuration(order.processingMinutes)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </Scroll>
      )}
    </Panel>
  )
}
