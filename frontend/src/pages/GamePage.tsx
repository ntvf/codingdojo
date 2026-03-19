import React, { useEffect, useRef, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { Client, Message } from '@stomp/stompjs'
import apiClient from '../api/client'

interface Snake {
  id: string
  name: string
  color: string
  body: { x: number; y: number }[]
}

interface GameState {
  roomCode: string
  gridSize: number
  snakes: Snake[]
  food: { x: number; y: number }
  gameStatus: 'waiting' | 'in_game' | 'finished'
  scores: { [playerId: string]: number }
  ticks: number
}

export const GamePage: React.FC = () => {
  const { roomCode } = useParams<{ roomCode: string }>()
  const navigate = useNavigate()
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const stompClientRef = useRef<Client | null>(null)
  const [gameState, setGameState] = useState<GameState | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

  const CELL_SIZE = 20
  const COLORS = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#FFA07A', '#98D8C8']

  // Fetch initial state
  useEffect(() => {
    const fetchState = async () => {
      try {
        const token = localStorage.getItem('authToken')
        const response = await apiClient.get(`/game/${roomCode}/state`, {
          headers: {
            Authorization: token || '',
          },
        })
        setGameState(response.data)
        setError(null)
      } catch (err) {
        setError('Failed to load game state')
        console.error(err)
      } finally {
        setLoading(false)
      }
    }

    if (roomCode) {
      fetchState()
    }
  }, [roomCode])

  // Connect to WebSocket and subscribe to game updates
  useEffect(() => {
    if (!roomCode) return

    const token = localStorage.getItem('authToken')
    const stompClient = new Client({
      brokerURL: `${window.location.protocol === 'https:' ? 'wss' : 'ws'}://${window.location.host}/ws`,
      connectHeaders: {
        Authorization: token || '',
      },
      debug: (msg: string) => {
        console.log('STOMP:', msg)
      },
      onConnect: () => {
        const subscription = stompClient.subscribe(
          `/topic/room/${roomCode}`,
          (message: Message) => {
            try {
              const state = JSON.parse(message.body) as GameState
              setGameState(state)
            } catch (err) {
              console.error('Failed to parse game state:', err)
            }
          }
        )

        return () => {
          subscription.unsubscribe()
        }
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame)
      },
    })

    stompClientRef.current = stompClient
    stompClient.activate()

    return () => {
      if (stompClientRef.current?.connected) {
        stompClientRef.current.deactivate()
      }
    }
  }, [roomCode])

  // Canvas rendering
  useEffect(() => {
    if (!canvasRef.current || !gameState) return

    const canvas = canvasRef.current
    const ctx = canvas.getContext('2d')
    if (!ctx) return

    const canvasWidth = gameState.gridSize * CELL_SIZE
    const canvasHeight = gameState.gridSize * CELL_SIZE
    canvas.width = canvasWidth
    canvas.height = canvasHeight

    // Clear canvas with dark background
    ctx.fillStyle = '#1a1a1a'
    ctx.fillRect(0, 0, canvasWidth, canvasHeight)

    // Draw grid
    ctx.strokeStyle = '#333333'
    ctx.lineWidth = 1
    for (let i = 0; i <= gameState.gridSize; i++) {
      ctx.beginPath()
      ctx.moveTo(i * CELL_SIZE, 0)
      ctx.lineTo(i * CELL_SIZE, canvasHeight)
      ctx.stroke()

      ctx.beginPath()
      ctx.moveTo(0, i * CELL_SIZE)
      ctx.lineTo(canvasWidth, i * CELL_SIZE)
      ctx.stroke()
    }

    // Draw food
    if (gameState.food) {
      ctx.fillStyle = '#FFD700'
      ctx.fillRect(
        gameState.food.x * CELL_SIZE + 2,
        gameState.food.y * CELL_SIZE + 2,
        CELL_SIZE - 4,
        CELL_SIZE - 4
      )
    }

    // Draw snakes
    gameState.snakes.forEach((snake, idx) => {
      const color = snake.color || COLORS[idx % COLORS.length]
      ctx.fillStyle = color

      snake.body.forEach((segment, segmentIdx) => {
        // Head is fully opaque, tail fades
        if (segmentIdx === 0) {
          ctx.globalAlpha = 1
        } else {
          ctx.globalAlpha = 0.7 - (segmentIdx / snake.body.length) * 0.5
        }

        ctx.fillRect(
          segment.x * CELL_SIZE + 1,
          segment.y * CELL_SIZE + 1,
          CELL_SIZE - 2,
          CELL_SIZE - 2
        )
      })
      ctx.globalAlpha = 1
    })

    // Draw scores overlay
    ctx.fillStyle = '#FFFFFF'
    ctx.font = '14px monospace'
    ctx.fillText(`Tick: ${gameState.ticks}`, 10, 20)

    let yOffset = 40
    gameState.snakes.forEach((snake) => {
      ctx.fillStyle = snake.color || '#FFFFFF'
      ctx.fillText(
        `${snake.name}: ${gameState.scores[snake.id] || 0}`,
        10,
        yOffset
      )
      yOffset += 20
    })
  }, [gameState])

  if (error) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <p className="text-red-500 text-lg mb-4">{error}</p>
          <button
            onClick={() => navigate(-1)}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          >
            Go Back
          </button>
        </div>
      </div>
    )
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-gray-400">Loading game...</div>
      </div>
    )
  }

  if (!gameState) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-gray-400">Waiting for game state...</div>
      </div>
    )
  }

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-900 p-4">
      <h1 className="text-3xl font-bold text-white mb-4">Game: {roomCode}</h1>
      <div className="border-4 border-gray-700 rounded-lg overflow-hidden bg-black mb-6">
        <canvas ref={canvasRef} className="block" />
      </div>

      {gameState.gameStatus === 'finished' && (
        <div className="mt-6 text-center">
          <p className="text-2xl font-bold text-yellow-400 mb-4">Game Over!</p>
          <a
            href={`/room/${roomCode}/results`}
            className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            View Results
          </a>
        </div>
      )}

      {gameState.gameStatus === 'in_game' && (
        <div className="mt-6 text-center">
          <p className="text-lg text-green-400">Game is running...</p>
        </div>
      )}
    </div>
  )
}
