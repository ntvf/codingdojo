import { createBrowserRouter } from 'react-router-dom'
import { Layout } from './components/Layout'
import { HomePage } from './pages/HomePage'
import { LobbyPage } from './pages/LobbyPage'
import { GamePage } from './pages/GamePage'
import ResultsPage from './pages/ResultsPage'

export const router = createBrowserRouter([
  {
    path: '/',
    element: <Layout />,
    children: [
      {
        path: '/',
        element: <HomePage />,
      },
      {
        path: '/room/:roomCode',
        element: <LobbyPage />,
      },
      {
        path: '/room/:roomCode/results',
        element: <ResultsPage />,
      },
    ],
  },
])
