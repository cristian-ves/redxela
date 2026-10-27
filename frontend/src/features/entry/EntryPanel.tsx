import { useState } from 'react'
import { Panel } from '../../components/Panel'
import { Segmented } from '../../components/Segmented'
import { ClientForm } from './ClientForm'
import { OrderForm } from './OrderForm'
import { ReceptionForm } from './ReceptionForm'
import styles from './EntryForm.module.css'

type Tab = 'order' | 'truck' | 'client'

const TABS: { value: Tab; label: string }[] = [
  { value: 'order', label: 'New order' },
  { value: 'truck', label: 'Truck arrival' },
  { value: 'client', label: 'New client' },
]

const SUBTITLES: Record<Tab, string> = {
  order: 'Goes to the waiting list; the scheduler decides when it starts',
  truck: 'Stock only grows through Reception and Inventory',
  client: 'The service level sets the urgency of its orders',
}

/* Manual entry: one panel with a tab per kind of input the operator can add by hand. */
export function EntryPanel() {
  const [tab, setTab] = useState<Tab>('order')
  return (
    <Panel title="Manual entry" subtitle={SUBTITLES[tab]}>
      <div className={styles.tabs}>
        <Segmented label="Kind of entry" options={TABS} value={tab} onChange={setTab} />
      </div>
      {tab === 'order' && <OrderForm />}
      {tab === 'truck' && <ReceptionForm />}
      {tab === 'client' && <ClientForm />}
    </Panel>
  )
}
