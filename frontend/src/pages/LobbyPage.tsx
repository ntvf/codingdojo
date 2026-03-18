import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { Button } from '../components/ui/button'
import { Card } from '../components/ui/card'
import apiClient from '../api/client'

interface Player {
  id: string
  name: string
  joinedAt: number
}

interface RoomDetails {
  id: string
  code: string
  status: 'waiting' | 'in_game' | 'finished'
  players: Player[]
  createdAt: number
}

export const LobbyPage = () => {
  const { roomCode } = useParams<{ roomCode: string }>()
  const [room, setRoom] = useState<RoomDetails | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [copied, setCopied] = useState(false)

  const currentPlayerId = localStorage.getItem('playerId')

  useEffect(() => {
    const fetchRoom = async () => {
      try {
        const response = await apiClient.get(`/room/${roomCode}`)
        setRoom(response.data)
        setError(null)
      } catch {
        setError('Failed to load room details')
      } finally {
        setLoading(false)
      }
    }

    if (roomCode) {
      fetchRoom()
      const interval = setInterval(fetchRoom, 2000)
      return () => clearInterval(interval)
    }
  }, [roomCode])

  const handleCopyToken = () => {
    const token = localStorage.getItem('authToken')
    if (token) {
      navigator.clipboard.writeText(token)
      setCopied(true)
      setTimeout(() => setCopied(false), 2000)
    }
  }

  const handleStartGame = async () => {
    try {
      await apiClient.post(`/admin/game/${roomCode}/start`)
      setRoom((prev) =>
        prev ? { ...prev, status: 'in_game' } : null
      )
    } catch {
      setError('Failed to start game')
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <p className="text-gray-600">Loading room...</p>
      </div>
    )
  }

  if (error || !room) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <Card className="p-6 text-center">
          <p className="text-red-600 mb-4">{error || 'Room not found'}</p>
          <Button onClick={() => window.history.back()} variant="secondary">
            Go Back
          </Button>
        </Card>
      </div>
    )
  }

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-4xl font-bold text-gray-900">Room: {room.code}</h1>
        <p className="mt-2 text-gray-600">
          Status:{' '}
          <span
            className={
              room.status === 'waiting'
                ? 'text-yellow-600 font-semibold'
                : room.status === 'in_game'
                  ? 'text-blue-600 font-semibold'
                  : 'text-gray-600 font-semibold'
            }
          >
            {room.status}
          </span>
        </p>
      </div>

      <div className="grid md:grid-cols-2 gap-8">
        {/* Your Info Card */}
        <Card className="p-6">
          <h2 className="text-xl font-bold mb-4">Your Info</h2>
          <div className="space-y-4">
            <div>
              <label className="text-sm text-gray-600">Auth Token</label>
              <div className="mt-1 flex gap-2">
                <input
                  type="password"
                  readOnly
                  value={localStorage.getItem('authToken') || ''}
                  className="flex-1 px-3 py-2 border border-gray-300 rounded text-sm font-mono bg-gray-50"
                />
                <Button
                  size="sm"
                  onClick={handleCopyToken}
                  variant={copied ? 'primary' : 'secondary'}
                >
                  {copied ? '✓ Copied' : 'Copy'}
                </Button>
              </div>
              <p className="mt-2 text-xs text-gray-500">
                Share this token with your bot to authenticate
              </p>
            </div>
          </div>
        </Card>

        {/* Game Control Card */}
        <Card className="p-6">
          <h2 className="text-xl font-bold mb-4">Game Control</h2>
          {room.status === 'waiting' && (
            <Button onClick={handleStartGame} className="w-full">
              Start Game
            </Button>
          )}
          {room.status === 'in_game' && (
            <p className="text-gray-600">Game is running...</p>
          )}
          {room.status === 'finished' && (
            <p className="text-gray-600">Game has finished</p>
          )}
        </Card>
      </div>

      {/* Players Card */}
      <Card className="p-6">
        <h2 className="text-xl font-bold mb-4">
          Players ({room.players.length})
        </h2>
        {room.players.length === 0 ? (
          <p className="text-gray-600">No players in this room yet</p>
        ) : (
          <div className="space-y-2">
            {room.players.map((player) => (
              <div
                key={player.id}
                className={
                  'flex items-center justify-between p-3 rounded border ' +
                  (player.id === currentPlayerId
                    ? 'bg-blue-50 border-blue-200'
                    : 'bg-gray-50 border-gray-200')
                }
              >
                <div>
                  <p className="font-medium text-gray-900">{player.name}</p>
                  <p className="text-xs text-gray-500">
                    Joined {new Date(player.joinedAt).toLocaleTimeString()}
                  </p>
                </div>
                {player.id === currentPlayerId && (
                  <span className="text-xs font-semibold text-blue-600">You</span>
                )}
              </div>
            ))}
          </div>
        )}
      </Card>

      {/* Room Info Card */}
      <Card className="p-6">
        <h2 className="text-xl font-bold mb-4">Room Info</h2>
        <dl className="space-y-2">
          <div className="flex justify-between">
            <dt className="text-gray-600">Room Code</dt>
            <dd className="font-mono font-semibold">{room.code}</dd>
          </div>
          <div className="flex justify-between">
            <dt className="text-gray-600">Created</dt>
            <dd className="text-sm">
              {new Date(room.createdAt).toLocaleString()}
            </dd>
          </div>
          <div className="flex justify-between">
            <dt className="text-gray-600">Player Count</dt>
            <dd className="font-semibold">{room.players.length}</dd>
          </div>
        </dl>
      </Card>
    </div>
  )
}
