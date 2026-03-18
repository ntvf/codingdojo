import { InputHTMLAttributes } from 'react'
import { clsx } from 'clsx'

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {}

export const Input = ({ className, disabled, ...props }: InputProps) => {
  return (
    <input
      className={clsx(
        'w-full px-3 py-2 border border-gray-300 rounded-md',
        'focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent',
        'disabled:bg-gray-100 disabled:cursor-not-allowed disabled:text-gray-500',
        className
      )}
      disabled={disabled}
      {...props}
    />
  )
}
