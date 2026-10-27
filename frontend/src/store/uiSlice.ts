import { createSlice, type PayloadAction } from '@reduxjs/toolkit'

export type Theme = 'dark' | 'light'

export interface Notice {
  id: number
  kind: 'error' | 'success'
  text: string
}

const THEME_KEY = 'redxela-theme'

/* The browser may refuse storage access (private windows), so a failure just means "use the default". */
function savedTheme(): Theme {
  try {
    return localStorage.getItem(THEME_KEY) === 'light' ? 'light' : 'dark'
  } catch {
    return 'dark'
  }
}

export function rememberTheme(theme: Theme): void {
  try {
    localStorage.setItem(THEME_KEY, theme)
  } catch {
    // Not being able to remember the choice is harmless.
  }
}

interface UiState {
  theme: Theme
  notice: Notice | null
}

let nextNoticeId = 1

const uiSlice = createSlice({
  name: 'ui',
  initialState: { theme: savedTheme(), notice: null } as UiState,
  reducers: {
    themeToggled(state) {
      state.theme = state.theme === 'dark' ? 'light' : 'dark'
    },
    noticeShown(state, action: PayloadAction<Omit<Notice, 'id'>>) {
      state.notice = { id: nextNoticeId++, ...action.payload }
    },
    noticeDismissed(state) {
      state.notice = null
    },
  },
})

export const { themeToggled, noticeShown, noticeDismissed } = uiSlice.actions
export default uiSlice.reducer
