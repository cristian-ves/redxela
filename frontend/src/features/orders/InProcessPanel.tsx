import { Empty } from '../../components/Empty'
import { Panel } from '../../components/Panel'
import { Scroll } from '../../components/Scroll'
import { useAppSelector } from '../../store/hooks'
import { selectInProcess } from '../../store/selectors'
import { InProcessRow } from './InProcessRow'

/* Orders that have left the waiting list: running, or blocked while they gather resources. */
export function InProcessPanel() {
  const orders = useAppSelector(selectInProcess)
  return (
    <Panel title="Orders in process" subtitle="Progress and time remaining, in simulated time">
      {orders.length === 0 ? (
        <Empty>No order is being processed.</Empty>
      ) : (
        <Scroll maxHeight={420}>
          <table>
            <thead>
              <tr>
                <th>Order</th>
                <th>Status</th>
                <th>Progress</th>
                <th>ETA</th>
                <th>Resources</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => (
                <InProcessRow key={order.id} order={order} />
              ))}
            </tbody>
          </table>
        </Scroll>
      )}
    </Panel>
  )
}
