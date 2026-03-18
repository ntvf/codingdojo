import { useParams } from 'react-router-dom'

export const LobbyPage = () => {
  const { roomCode } = useParams()
  
  return (
    <div className="space-y-8">
      <div>
        <h2 className="text-3xl font-bold text-gray-900">Room: {roomCode}</h2>
        <p className="mt-2 text-gray-600">Waiting for game to start</p>
      </div>
      <div className="bg-white rounded-lg shadow p-6">
        <h3 className="text-xl font-bold mb-4">Players</h3>
        <p className="text-gray-600">Placeholder for player list</p>
      </div>
    </div>
  )
}
