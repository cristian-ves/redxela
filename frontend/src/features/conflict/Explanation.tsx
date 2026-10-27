import { Section } from '../../components/Section'
import styles from './ConflictPanel.module.css'

/* The reasoning behind the detection, as the server worked it out: who holds what, and the four conditions. */
export function Explanation({ lines }: { lines: string[] }) {
  return (
    <Section title="Why this is a deadlock">
      <ol className={styles.explanation}>
        {lines.map((line) => (
          <li key={line}>{line}</li>
        ))}
      </ol>
    </Section>
  )
}
