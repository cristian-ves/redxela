/* Shapes of the JSON the backend sends. They mirror the Java records one to one. */

export type ResourceTypeId =
  | 'PACKING_STATION'
  | 'LOADING_AREA'
  | 'WAREHOUSE_STAFF'
  | 'FORKLIFT'
  | 'QC_STATION'
  | 'SCANNER'

export type PolicyId = 'STRICT_PRIORITY' | 'PRIORITY_WITH_AGING' | 'FEWEST_ITEMS_FIRST'
export type OrderStatus = 'WAITING' | 'PROCESSING' | 'BLOCKED' | 'COMPLETED'
export type ReceptionStatus = 'QUEUED' | 'RECEIVING' | 'BLOCKED' | 'WAITING_FOR_INVENTORY' | 'SENT'
export type LevelKey = 'EXPRESS' | 'PRIORITY' | 'STANDARD' | 'SCHEDULED' | 'ECONOMY'

export interface LineView {
  sku: string
  name: string
  units: number
}

export interface OrderView {
  id: string
  clientId: string
  clientName: string
  level: number
  levelLabel: string
  type: string
  typeLabel: string
  lines: LineView[]
  totalUnits: number
  immediate: boolean
  status: OrderStatus
  waitReason: string | null
  waitReasonLabel: string | null
  waitingFor: ResourceTypeId | null
  held: ResourceTypeId[]
  progress: number
  remainingMinutes: number
  createdAt: number
  waitedMinutes: number
  processingMinutes: number
  deferredUntil: string | null
}

export interface Holding {
  owner: string
  units: number
}

export interface ResourceView {
  type: ResourceTypeId
  label: string
  capacity: number
  available: number
  holders: Holding[]
  waiters: Holding[]
}

export interface TruckView {
  id: string
  sku: string
  product: string
  units: number
  status: ReceptionStatus
  waitingFor: ResourceTypeId | null
  held: ResourceTypeId[]
  progress: number
  remainingMinutes: number
}

export interface ArrivalMessage {
  receptionId: string
  sku: string
  units: number
}

export interface ProcessedArrival {
  receptionId: string
  sku: string
  units: number
  lotId: string | null
  locations: string[]
  outcome: string
}

export interface ReceptionView {
  queueCapacity: number
  queued: TruckView[]
  current: TruckView | null
  bufferCapacity: number
  buffer: ArrivalMessage[]
  recent: TruckView[]
  processed: ProcessedArrival[]
}

export interface StockView {
  sku: string
  name: string
  onHand: number
  reserved: number
  available: number
}

export interface SlotState {
  index: number
  location: string
  lotId: string | null
  sku: string | null
  units: number
}

export interface InvolvedOperation {
  id: string
  order: boolean
  holds: Partial<Record<ResourceTypeId, number>>
  waitingFor: ResourceTypeId | null
  waitingForUnits: number
  waitingOn: string[]
}

export interface DeadlockReport {
  detectedAtMinutes: number
  operations: InvolvedOperation[]
  explanation: string[]
}

export interface LevelStats {
  level: LevelKey
  completed: number
  averageWaitMinutes: number
  withinTargetPercent: number
}

export interface PolicyStats {
  policy: PolicyId
  active: boolean
  completed: number
  averageWaitMinutes: number
  averageProcessingMinutes: number
  withinTargetPercent: number
  throughputPerHour: number
  averageUtilizationPercent: number
  utilizationPercent: Partial<Record<ResourceTypeId, number>>
  levels: LevelStats[]
}

export interface Controls {
  policy: PolicyId
  generatorRunning: boolean
  ordersPerMinute: number
  seed: number | null
  preventionEnabled: boolean
  replenisherEnabled: boolean
}

export interface Counters {
  submitted: number
  completed: number
  waiting: number
  inProcess: number
}

export interface WaitingList {
  policy: PolicyId
  policyLabel: string
  criterion: string
  total: number
  orders: OrderView[]
}

export interface Snapshot {
  clockMinutes: number
  speed: number
  controls: Controls
  counters: Counters
  resources: ResourceView[]
  inProcess: OrderView[]
  waiting: WaitingList
  completed: OrderView[]
  deadlock: DeadlockReport | null
  stats: PolicyStats[]
  stock: StockView[]
  warehouse: SlotState[]
  reception: ReceptionView
}

export interface ClientView {
  id: string
  name: string
  level: number
  levelLabel: string
}

export interface Meta {
  levels: { level: number; label: string; targetWaitMinutes: number }[]
  resources: { type: ResourceTypeId; label: string; capacity: number; orderingRank: number }[]
  policies: { id: PolicyId; label: string; criterion: string }[]
  types: {
    id: string
    label: string
    description: string
    requirements: { resource: ResourceTypeId; units: number }[]
  }[]
  products: { sku: string; name: string; type: string }[]
}
