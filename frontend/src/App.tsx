import { Notice } from './components/Notice'
import { AppHeader } from './features/header/AppHeader'
import { ControlsPanel } from './features/controls/ControlsPanel'
import { EntryPanel } from './features/entry/EntryPanel'
import { CompletedPanel } from './features/orders/CompletedPanel'
import { InProcessPanel } from './features/orders/InProcessPanel'
import { WaitingPanel } from './features/orders/WaitingPanel'
import { ReceptionPanel } from './features/reception/ReceptionPanel'
import { ResourcesPanel } from './features/resources/ResourcesPanel'
import { StatsPanel } from './features/stats/StatsPanel'
import { StockPanel } from './features/stock/StockPanel'
import { WarehousePanel } from './features/warehouse/WarehousePanel'
import { useSnapshotSocket } from './hooks/useSnapshotSocket'
import { useTheme } from './hooks/useTheme'
import styles from './App.module.css'

export default function App() {
  useSnapshotSocket()
  useTheme()

  return (
    <>
      <AppHeader />
      <main className={styles.page}>
        <div className={styles.span12}><ControlsPanel /></div>
        <div className={styles.span5}><ResourcesPanel /></div>
        <div className={styles.span7}><InProcessPanel /></div>
        <div className={styles.span7}><WaitingPanel /></div>
        <div className={styles.span5}><CompletedPanel /></div>
        <div className={styles.span5}><EntryPanel /></div>
        <div className={styles.span7}><ReceptionPanel /></div>
        <div className={styles.span5}><StockPanel /></div>
        <div className={styles.span7}><WarehousePanel /></div>
        <div className={styles.span12}><StatsPanel /></div>
      </main>
      <Notice />
    </>
  )
}
