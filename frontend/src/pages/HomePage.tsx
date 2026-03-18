export const HomePage = () => {
  return (
    <div className="space-y-8">
      <div>
        <h2 className="text-3xl font-bold text-gray-900">Welcome to CodingDojo</h2>
        <p className="mt-2 text-gray-600">Create or join a game room</p>
      </div>
      <div className="grid md:grid-cols-2 gap-8">
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-xl font-bold mb-4">Create New Room</h3>
          <p className="text-gray-600">Placeholder for create room form</p>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-xl font-bold mb-4">Join Room</h3>
          <p className="text-gray-600">Placeholder for room list / join form</p>
        </div>
      </div>
    </div>
  )
}
