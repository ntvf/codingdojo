import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import App from './App'

describe('App', () => {
  it('renders the CodingDojo title', () => {
    render(<App />)
    expect(screen.getByText('CodingDojo')).toBeInTheDocument()
  })

  it('renders the scaffold complete message', () => {
    render(<App />)
    expect(screen.getByText('Frontend scaffold complete')).toBeInTheDocument()
  })
})
