import React, { useEffect, useState } from 'react';
import apiClient from '../api/client';

interface LeaderboardEntry {
  rank: number;
  playerId: string;
  playerName: string;
  score: number;
  gameCount: number;
  averageScore: number;
  bestScore: number;
}

interface FunnyStat {
  title: string;
  description: string;
  entries: Array<{
    playerName: string;
    value: string;
  }>;
}

const ResultsPage: React.FC = () => {
  const [leaderboard, setLeaderboard] = useState<LeaderboardEntry[]>([]);
  const [funnyStats, setFunnyStats] = useState<FunnyStat[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchResults = async () => {
      try {
        setLoading(true);
        const [lbResponse, fsResponse] = await Promise.all([
          apiClient.get('/stats/leaderboard?limit=100'),
          apiClient.get('/stats/funny'),
        ]);

        setLeaderboard(lbResponse.data);
        setFunnyStats(fsResponse.data);
      } catch (err) {
        setError('Failed to load results');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchResults();
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-gray-400">Loading results...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-red-500">{error}</div>
      </div>
    );
  }

  const topThree = leaderboard.slice(0, 3);

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-900 to-gray-800 p-6">
      <h1 className="text-4xl font-bold text-white text-center mb-12">Game Results</h1>

      {/* Podium */}
      <div className="flex justify-center gap-8 mb-12">
        {/* Silver (2nd) */}
        {topThree[1] && (
          <div className="flex flex-col items-center">
            <div className="w-24 h-32 bg-gray-400 rounded-t-lg flex items-center justify-center mb-2">
              <span className="text-3xl font-bold text-gray-800">2️⃣</span>
            </div>
            <p className="text-white font-bold text-center">{topThree[1].playerName}</p>
            <p className="text-gray-300 text-sm">{topThree[1].score} pts</p>
          </div>
        )}

        {/* Gold (1st) */}
        {topThree[0] && (
          <div className="flex flex-col items-center">
            <div className="w-24 h-40 bg-yellow-400 rounded-t-lg flex items-center justify-center mb-2 shadow-lg">
              <span className="text-4xl font-bold">🏆</span>
            </div>
            <p className="text-yellow-400 font-bold text-center text-lg">{topThree[0].playerName}</p>
            <p className="text-yellow-300 text-sm font-bold">{topThree[0].score} pts</p>
          </div>
        )}

        {/* Bronze (3rd) */}
        {topThree[2] && (
          <div className="flex flex-col items-center">
            <div className="w-24 h-28 bg-orange-400 rounded-t-lg flex items-center justify-center mb-2">
              <span className="text-3xl font-bold text-orange-700">3️⃣</span>
            </div>
            <p className="text-white font-bold text-center">{topThree[2].playerName}</p>
            <p className="text-gray-300 text-sm">{topThree[2].score} pts</p>
          </div>
        )}
      </div>

      {/* Full Leaderboard */}
      <div className="max-w-4xl mx-auto mb-12">
        <h2 className="text-2xl font-bold text-white mb-4">Leaderboard</h2>
        <div className="bg-gray-800 rounded-lg overflow-hidden border border-gray-700">
          <table className="w-full">
            <thead className="bg-gray-900 border-b border-gray-700">
              <tr>
                <th className="px-6 py-3 text-left text-white font-bold">Rank</th>
                <th className="px-6 py-3 text-left text-white font-bold">Player</th>
                <th className="px-6 py-3 text-right text-white font-bold">Score</th>
                <th className="px-6 py-3 text-right text-white font-bold">Games</th>
                <th className="px-6 py-3 text-right text-white font-bold">Avg Score</th>
                <th className="px-6 py-3 text-right text-white font-bold">Best</th>
              </tr>
            </thead>
            <tbody>
              {leaderboard.map((entry) => (
                <tr
                  key={entry.playerId}
                  className="border-b border-gray-700 hover:bg-gray-750 transition"
                >
                  <td className="px-6 py-3 text-gray-300 font-mono">#{entry.rank}</td>
                  <td className="px-6 py-3 text-white font-medium">{entry.playerName}</td>
                  <td className="px-6 py-3 text-right text-yellow-400 font-bold">
                    {entry.score}
                  </td>
                  <td className="px-6 py-3 text-right text-gray-400">{entry.gameCount}</td>
                  <td className="px-6 py-3 text-right text-gray-400">
                    {entry.averageScore.toFixed(1)}
                  </td>
                  <td className="px-6 py-3 text-right text-gray-400">{entry.bestScore}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Funny Stats Cards */}
      {funnyStats.length > 0 && (
        <div className="max-w-4xl mx-auto">
          <h2 className="text-2xl font-bold text-white mb-4">Funny Stats</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {funnyStats.map((stat, idx) => (
              <div
                key={idx}
                className="bg-gray-800 rounded-lg p-6 border border-gray-700 hover:border-gray-600 transition"
              >
                <h3 className="text-xl font-bold text-white mb-2">{stat.title}</h3>
                <p className="text-gray-400 text-sm mb-4">{stat.description}</p>
                <ul className="space-y-2">
                  {stat.entries.map((entry, entryIdx) => (
                    <li key={entryIdx} className="flex justify-between text-sm">
                      <span className="text-gray-300">{entry.playerName}</span>
                      <span className="text-green-400 font-mono">{entry.value}</span>
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Back to Home */}
      <div className="text-center mt-12">
        <a
          href="/"
          className="px-8 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition inline-block"
        >
          Back to Home
        </a>
      </div>
    </div>
  );
};

export default ResultsPage;
