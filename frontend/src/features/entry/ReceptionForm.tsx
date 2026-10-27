import { useState, type FormEvent } from 'react'
import { api } from '../../api/http'
import { Button } from '../../components/Button'
import { Field } from '../../components/Field'
import { useCommand } from '../../hooks/useCommand'
import { useAppSelector } from '../../store/hooks'
import { selectMeta } from '../../store/selectors'
import { MAX_UNITS_PER_TRUCK, parseUnits } from './orderDraft'
import styles from './EntryForm.module.css'

/* Registers a supplier truck. Its goods reach the warehouse only after Reception and Inventory process it. */
export function ReceptionForm() {
  const run = useCommand()
  const { types, products } = useAppSelector(selectMeta)
  const [sku, setSku] = useState('')
  const [units, setUnits] = useState('50')
  const parsed = parseUnits(units, MAX_UNITS_PER_TRUCK)
  const chosen = sku || products[0]?.sku

  const submit = async (event: FormEvent) => {
    event.preventDefault()
    if (chosen === undefined || parsed === null) return
    await run(() => api.createReception(chosen, parsed), 'Truck registered')
  }

  return (
    <form className={styles.form} onSubmit={submit}>
      <div className={styles.two}>
        <Field label="Product">
          <select value={chosen} onChange={(event) => setSku(event.target.value)}>
            {types.map((type) => (
              <optgroup key={type.id} label={type.label}>
                {products.filter((product) => product.type === type.id).map((product) => (
                  <option key={product.sku} value={product.sku}>{product.name} ({product.sku})</option>
                ))}
              </optgroup>
            ))}
          </select>
        </Field>
        <Field label="Units on the truck">
          <input type="number" min="1" max={MAX_UNITS_PER_TRUCK} value={units} onChange={(event) => setUnits(event.target.value)} />
        </Field>
      </div>
      <p className={styles.hint}>A truck carries 1 to {MAX_UNITS_PER_TRUCK} units. Unloading needs a forklift and the barcode scanner.</p>
      {parsed === null && <p className={styles.error}>Enter a whole number from 1 to {MAX_UNITS_PER_TRUCK}.</p>}
      <div className={styles.actions}>
        <Button type="submit" variant="primary" disabled={parsed === null}>Register truck</Button>
      </div>
    </form>
  )
}
