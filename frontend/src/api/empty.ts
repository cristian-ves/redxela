import type { Meta, Snapshot } from './types'

/* What the screen shows before the first snapshot arrives, so no panel ever has to handle "no data". */
export const EMPTY_SNAPSHOT: Snapshot = {
  clockMinutes: 0,
  speed: 1,
  controls: {
    policy: 'STRICT_PRIORITY',
    generatorRunning: false,
    ordersPerMinute: 0.4,
    seed: null,
    preventionEnabled: false,
    replenisherEnabled: true,
  },
  counters: { submitted: 0, completed: 0, waiting: 0, inProcess: 0 },
  resources: [],
  inProcess: [],
  waiting: { policy: 'STRICT_PRIORITY', policyLabel: '', criterion: '', total: 0, orders: [] },
  completed: [],
  deadlock: null,
  stats: [],
  stock: [],
  warehouse: [],
  reception: {
    queueCapacity: 0,
    queued: [],
    current: null,
    bufferCapacity: 0,
    buffer: [],
    recent: [],
    processed: [],
  },
}

export const EMPTY_META: Meta = { levels: [], resources: [], policies: [], types: [], products: [] }
