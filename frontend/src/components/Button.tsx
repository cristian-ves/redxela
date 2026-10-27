import type { ButtonHTMLAttributes } from 'react'
import styles from './Button.module.css'

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'ghost' | 'danger'
  small?: boolean
}

export function Button({ variant = 'ghost', small = false, className, type = 'button', ...rest }: ButtonProps) {
  const classes = [styles.button, styles[variant], small ? styles.small : '', className ?? ''].join(' ')
  return <button type={type} className={classes} {...rest} />
}
