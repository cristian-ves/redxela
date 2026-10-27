import { useState, type FormEvent } from 'react'
import { api } from '../../api/http'
import { Button } from '../../components/Button'
import { Field } from '../../components/Field'
import { useCommand } from '../../hooks/useCommand'
import { loadClients } from '../../store/catalogSlice'
import { useAppDispatch, useAppSelector } from '../../store/hooks'
import { selectMeta } from '../../store/selectors'
import styles from './EntryForm.module.css'

/* Registers a client account. Its service level decides how urgent every order it places is. */
export function ClientForm() {
  const run = useCommand()
  const dispatch = useAppDispatch()
  const { levels } = useAppSelector(selectMeta)
  const [name, setName] = useState('')
  const [level, setLevel] = useState(3)

  const submit = async (event: FormEvent) => {
    event.preventDefault()
    const created = await run(() => api.createClient(name.trim(), level), 'Client added')
    if (created) {
      setName('')
      void dispatch(loadClients())
    }
  }

  return (
    <form className={styles.form} onSubmit={submit}>
      <div className={styles.two}>
        <Field label="Client name">
          <input type="text" value={name} maxLength={60} onChange={(event) => setName(event.target.value)} />
        </Field>
        <Field label="Service level">
          <select value={level} onChange={(event) => setLevel(Number(event.target.value))}>
            {levels.map((item) => (
              <option key={item.level} value={item.level}>
                {item.level} · {item.label} (target ≤ {item.targetWaitMinutes} min)
              </option>
            ))}
          </select>
        </Field>
      </div>
      <div className={styles.actions}>
        <Button type="submit" variant="primary" disabled={name.trim() === ''}>
          Add client
        </Button>
      </div>
    </form>
  )
}
