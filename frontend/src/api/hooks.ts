import { useCallback, useEffect, useState } from 'react'
import apiClient from './client'

interface Room {
  id: string
  code: string
  status: 'waiting' | 'in_game' | 'finished'
  createdAt: number
}

interface Player {
  id: string
  name: string
  token: string
}

export const useRooms = () => {
  const [rooms, setRooms] = useState<Room[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error | null>(null)

  const fetchRooms = useCallback(async () => {
    setLoading(true)
    try {
      const response = await apiClient.get('/room')
      setRooms(response.data || [])
      setError(null)
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Failed to fetch rooms'))
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchRooms()
  }, [fetchRooms])

  return { rooms, loading, error, refetch: fetchRooms }
}

export const useCreateRoom = () => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error | null>(null)

  const createRoom = useCallback(async (playerName: string) => {
    setLoading(true)
    try {
      const response = await apiClient.post('/room', { playerName })
      setError(null)
      return response.data
    } catch (err) {
      const error = err instanceof Error ? err : new Error('Failed to create room')
      setError(error)
      throw error
    } finally {
      setLoading(false)
    }
  }, [])

  return { createRoom, loading, error }
}

export const useJoinRoom = () => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<Error | null>(null)

  const joinRoom = useCallback(async (roomCode: string, playerName: string) => {
    setLoading(true)
    try {
      const response = await apiClient.post(`/room/${roomCode}/join`, { playerName })
      const player: Player = response.data
      localStorage.setItem('authToken', player.token)
      setError(null)
      return player
    } catch (err) {
      const error = err instanceof Error ? err : new Error('Failed to join room')
      setError(error)
      throw error
    } finally {
      setLoading(false)
    }
  }, [])

  return { joinRoom, loading, error }
}
