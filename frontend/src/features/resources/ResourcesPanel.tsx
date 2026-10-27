import { Panel } from '../../components/Panel'
import { useAppSelector } from '../../store/hooks'
import { selectResources } from '../../store/selectors'
import { ResourceRow } from './ResourceRow'
import styles from './ResourcesPanel.module.css'

/* State of the six shared resource types, including the operations blocked waiting for them. */
export function ResourcesPanel() {
  const resources = useAppSelector(selectResources)
  return (
    <Panel title="Resources" subtitle="Units in use, who holds them and who is blocked waiting">
      <ul className={styles.list}>
        {resources.map((resource) => (
          <ResourceRow key={resource.type} resource={resource} />
        ))}
      </ul>
    </Panel>
  )
}
