import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button } from '../components/ui/button'
import { Input } from '../components/ui/input'
import { Card } from '../components/ui/card'
import { useCreateRoom, useJoinRoom, useRooms } from '../api/hooks'

export const HomePage = () => {
  const [playerName, setPlayerName] = useState('')
  const [createPlayerName, setCreatePlayerName] = useState('')
  const navigate = useNavigate()
  
  const { createRoom, loading: createLoading, error: createError } = useCreateRoom()
  const { joinRoom, loading: joinLoading, error: joinError } = useJoinRoom()
  const { rooms, loading: roomsLoading, error: roomsError } = useRooms()

  const handleCreateRoom = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!createPlayerName.trim()) return
    
    try {
      const room = await createRoom(createPlayerName)
      navigate(`/room/${room.code}`)
    } catch {
      // Error already in createError state
    }
  }

  const handleJoinRoom = async (roomCode: string) => {
    if (!playerName.trim()) return
    
    try {
      await joinRoom(roomCode, playerName)
      navigate(`/room/${roomCode}`)
    } catch {
      // Error already in joinError state
    }
  }

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-4xl font-bold text-gray-900">Welcome to CodingDojo</h1>
        <p className="mt-2 text-xl text-gray-600">Code. Compete. Learn.</p>
      </div>

      <div className="grid md:grid-cols-2 gap-8">
        {/* Create Room Card */}
        <Card className="p-6">
          <h2 className="text-2xl font-bold mb-4">Create New Room</h2>
          <form onSubmit={handleCreateRoom} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Your Name
              </label>
              <Input
                type="text"
                placeholder="Enter your name"
                value={createPlayerName}
                onChange={(e) => setCreatePlayerName(e.target.value)}
                disabled={createLoading}
              />
            </div>
            {createError && (
              <p className="text-sm text-red-600">{createError.message}</p>
            )}
            <Button
              type="submit"
              disabled={!createPlayerName.trim() || createLoading}
              className="w-full"
            >
              {createLoading ? 'Creating...' : 'Create Room'}
            </Button>
          </form>
        </Card>

        {/* Join Room Card */}
        <Card className="p-6">
          <h2 className="text-2xl font-bold mb-4">Join Room</h2>
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Your Name
              </label>
              <Input
                type="text"
                placeholder="Enter your name"
                value={playerName}
                onChange={(e) => setPlayerName(e.target.value)}
              />
            </div>
            {joinError && (
              <p className="text-sm text-red-600">{joinError.message}</p>
            )}
            <p className="text-sm text-gray-600">
              Select a room below to join
            </p>
          </div>
        </Card>
      </div>

      {/* Room List */}
      <Card className="p-6">
        <h2 className="text-2xl font-bold mb-4">Available Rooms</h2>
        {roomsLoading ? (
          <p className="text-gray-600">Loading rooms...</p>
        ) : roomsError ? (
          <p className="text-red-600">{roomsError.message}</p>
        ) : rooms.length === 0 ? (
          <p className="text-gray-600">No rooms available. Create one!</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full">
              <thead className="bg-gray-100">
                <tr>
                  <th className="px-4 py-2 text-left text-sm font-semibold">Code</th>
                  <th className="px-4 py-2 text-left text-sm font-semibold">Status</th>
                  <th className="px-4 py-2 text-left text-sm font-semibold">Action</th>
                </tr>
              </thead>
              <tbody>
                {rooms.map((room) => (
                  <tr key={room.id} className="border-t">
                    <td className="px-4 py-3 font-mono text-sm">{room.code}</td>
                    <td className="px-4 py-3 text-sm">
                      <span
                        className={
                          room.status === 'waiting'
                            ? 'bg-yellow-100 text-yellow-800 px-2 py-1 rounded text-xs'
                            : room.status === 'in_game'
                              ? 'bg-blue-100 text-blue-800 px-2 py-1 rounded text-xs'
                              : 'bg-gray-100 text-gray-800 px-2 py-1 rounded text-xs'
                        }
                      >
                        {room.status}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      {room.status === 'waiting' && (
                        <Button
                          size="sm"
                          onClick={() => handleJoinRoom(room.code)}
                          disabled={!playerName.trim() || joinLoading}
                        >
                          {joinLoading ? 'Joining...' : 'Join'}
                        </Button>
                      )}
                      {room.status !== 'waiting' && (
                        <span className="text-gray-400 text-sm">Unavailable</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>
    </div>
  )
}
