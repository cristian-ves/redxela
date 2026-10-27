import type { ClientView, Meta, OrderView, PolicyId, TruckView } from './types'

export const API_URL: string = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

/* An error answered by the server (or the lack of an answer, with status 0). */
export class ApiRequestError extends Error {
  readonly status: number

  constructor(status: number, message: string) {
    super(message)
    this.name = 'ApiRequestError'
    this.status = status
  }
}

async function readMessage(response: Response): Promise<string> {
  try {
    const body = (await response.json()) as { message?: string }
    if (body.message) return body.message
  } catch {
    // The body was not JSON; fall back to the status line.
  }
  return `${response.status} ${response.statusText}`
}

async function request<T>(method: string, path: string, body?: unknown): Promise<T> {
  let response: Response
  try {
    response = await fetch(`${API_URL}/api${path}`, {
      method,
      headers: body === undefined ? undefined : { 'Content-Type': 'application/json' },
      body: body === undefined ? undefined : JSON.stringify(body),
    })
  } catch {
    throw new ApiRequestError(0, 'The server is not reachable')
  }
  if (!response.ok) throw new ApiRequestError(response.status, await readMessage(response))
  return response.status === 204 ? (undefined as T) : ((await response.json()) as T)
}

export interface NewOrderBody {
  clientId: string
  lines: { sku: string; units: number }[]
  immediate: boolean
}

/* Every command and query the interface can send, one function per endpoint. */
export const api = {
  meta: () => request<Meta>('GET', '/meta'),
  clients: () => request<ClientView[]>('GET', '/clients'),
  createClient: (name: string, level: number) => request<ClientView>('POST', '/clients', { name, level }),
  createOrder: (body: NewOrderBody) => request<OrderView>('POST', '/orders', body),
  createReception: (sku: string, units: number) => request<TruckView>('POST', '/receptions', { sku, units }),
  setSpeed: (value: number) => request<void>('PUT', '/simulation/speed', { value }),
  selectPolicy: (policy: PolicyId) => request<void>('PUT', '/simulation/policy', { policy }),
  setReplenisher: (enabled: boolean) => request<void>('PUT', '/simulation/replenisher', { enabled }),
  resetStats: () => request<void>('POST', '/simulation/stats/reset'),
  startGenerator: (seed: number | null, ordersPerMinute: number) =>
    request<void>('POST', '/generator/start', { seed, ordersPerMinute }),
  stopGenerator: () => request<void>('POST', '/generator/stop'),
  setRate: (value: number) => request<void>('PUT', '/generator/rate', { value }),
  provokeConflict: () => request<{ orders: string[] }>('POST', '/deadlock/provoke'),
  resolveConflict: (winnerId: string) =>
    request<{ returned: string[] }>('POST', '/deadlock/resolve', { winnerId }),
  setPrevention: (enabled: boolean) => request<void>('PUT', '/deadlock/prevention', { enabled }),
}
