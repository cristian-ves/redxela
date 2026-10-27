import { useEffect } from 'react'
import { useAppSelector } from '../store/hooks'
import { selectTheme } from '../store/selectors'
import { rememberTheme } from '../store/uiSlice'

/* Applies the chosen theme to the page, where the CSS tokens read it, and remembers it for next time. */
export function useTheme(): void {
  const theme = useAppSelector(selectTheme)
  useEffect(() => {
    document.documentElement.dataset.theme = theme
    rememberTheme(theme)
  }, [theme])
}
