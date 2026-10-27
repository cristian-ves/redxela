import { useState, type FormEvent } from 'react'
import { api } from '../../api/http'
import { Button } from '../../components/Button'
import { Field } from '../../components/Field'
import { Toggle } from '../../components/Toggle'
import { useCommand } from '../../hooks/useCommand'
import { useAppSelector } from '../../store/hooks'
import { selectClients, selectMeta, selectStock } from '../../store/selectors'
import { buildDraft, MAX_UNITS_PER_LINE } from './orderDraft'
import { OrderLines } from './OrderLines'
import { TypeSummary } from './TypeSummary'
import styles from './EntryForm.module.css'

/*
 * Builds an order for one client. The merchandise type is chosen first because
 * an order can only contain products of a single type: that is what decides
 * which resources it needs.
 */
export function OrderForm() {
  const run = useCommand()
  const clients = useAppSelector(selectClients)
  const { types, products } = useAppSelector(selectMeta)
  const stock = useAppSelector(selectStock)
  const [clientId, setClientId] = useState('')
  const [typeId, setTypeId] = useState('')
  const [typed, setTyped] = useState<Record<string, string>>({})
  const [immediate, setImmediate] = useState(false)

  const type = types.find((item) => item.id === typeId) ?? types[0]
  const typeProducts = products.filter((product) => product.type === type?.id)
  const draft = buildDraft(typeProducts.map((product) => product.sku), typed)
  const client = clientId || clients[0]?.id

  const submit = async (event: FormEvent) => {
    event.preventDefault()
    if (client === undefined || draft.lines.length === 0 || draft.invalid) return
    const created = await run(() => api.createOrder({ clientId: client, lines: draft.lines, immediate }), 'Order submitted')
    if (created) setTyped({})
  }

  if (clients.length === 0 || !type) return <p className="muted">Add a client first: every order belongs to one.</p>

  return (
    <form className={styles.form} onSubmit={submit}>
      <div className={styles.two}>
        <Field label="Client">
          <select value={client} onChange={(event) => setClientId(event.target.value)}>
            {clients.map((item) => (
              <option key={item.id} value={item.id}>{item.name} · {item.levelLabel}</option>
            ))}
          </select>
        </Field>
        <Field label="Merchandise type">
          <select value={type.id} onChange={(event) => { setTypeId(event.target.value); setTyped({}) }}>
            {types.map((item) => (
              <option key={item.id} value={item.id}>{item.label}</option>
            ))}
          </select>
        </Field>
      </div>
      <TypeSummary type={type} />
      <OrderLines products={typeProducts} stock={stock} typed={typed}
        onChange={(sku, text) => setTyped((current) => ({ ...current, [sku]: text }))} />
      <Toggle checked={immediate} onChange={setImmediate} label="Start immediately, skipping the resource check" />
      {immediate && <p className={styles.hint}>The order starts even if resources are missing. This is how a deadlock is provoked on purpose.</p>}
      {draft.invalid && <p className={styles.error}>Units per product must be whole numbers from 1 to {MAX_UNITS_PER_LINE}.</p>}
      <div className={styles.actions}>
        <Button type="submit" variant="primary" disabled={draft.lines.length === 0 || draft.invalid}>
          Submit order
        </Button>
      </div>
    </form>
  )
}
