import { Empty } from '../../components/Empty'
import { Panel } from '../../components/Panel'
import { Scroll } from '../../components/Scroll'
import { formatDuration } from '../../lib/format'
import { useAppSelector } from '../../store/hooks'
import { selectWaiting } from '../../store/selectors'
import { OrderCell } from './OrderCell'

/* The ordered waiting list. The server sorts it with the active policy, so the first row is next in line. */
export function WaitingPanel() {
  const { policyLabel, criterion, total, orders } = useAppSelector(selectWaiting)
  const hidden = total - orders.length

  return (
    <Panel
      title="Waiting list"
      subtitle={policyLabel ? `${policyLabel}: ${criterion}` : undefined}
      actions={hidden > 0 ? <span className="muted">Showing {orders.length} of {total}</span> : undefined}
    >
      {orders.length === 0 ? (
        <Empty>No order is waiting.</Empty>
      ) : (
        <Scroll maxHeight={420}>
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Order</th>
                <th>Waited</th>
                <th>Why it waits</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order, index) => (
                <tr key={order.id}>
                  <td className="mono muted">{index + 1}</td>
                  <td><OrderCell order={order} /></td>
                  <td className="mono">{formatDuration(order.waitedMinutes)}</td>
                  <td>
                    {order.waitReasonLabel ?? '—'}
                    {order.deferredUntil && <div className="muted">until {order.deferredUntil} finishes</div>}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </Scroll>
      )}
    </Panel>
  )
}
