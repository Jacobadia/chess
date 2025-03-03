package dataaccess;

import model.GameData;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {

	private final Map<Integer, GameData> games = new HashMap<>();

	@Override
	public void createGame(GameData game) throws DataAccessException {
		if (gameIDExists(game.gameID())) {
			throw new DataAccessException("Game ID already exists");
		}
		games.put(game.gameID(), new GameData(
				game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game()
		));
	}

	@Override
	public GameData getGame(int gameID) throws DataAccessException {
		if (!gameIDExists(gameID)) {
			throw new DataAccessException("Game not found");
		}
		return games.get(gameID);
	}

	@Override
	public ArrayList<GameData> listGames() throws DataAccessException {
		return new ArrayList<>(games.values());
	}

	@Override
	public void updateGame(GameData game) throws DataAccessException {
		if (!gameIDExists(game.gameID())) {
			throw new DataAccessException("Game not found");
		}
		games.put(game.gameID(), new GameData(
				game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game()
		));
	}

	@Override
	public void clearAllGames() throws DataAccessException {
		games.clear();
	}

	@Override
	public boolean gameIDExists(int gameID) throws DataAccessException {
		return games.containsKey(gameID);
	}
}
