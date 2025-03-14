package dataaccess;

import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.sql.Types;

import chess.ChessGame;

import java.util.ArrayList;


public class MySqlGameDAO extends MySqlBaseDAO implements GameDAO {
	private final Gson gson = new Gson();

	@Override
	public void createGame(GameData game) throws DataAccessException {
		if (gameIDExists(game.gameID())) {
			throw new DataAccessException("Game ID already exists");
		}

		String gameJson = gson.toJson(game.game());

		var statement = "INSERT INTO Games (gameID, whiteUsername, blackUsername, gameName, gameData) VALUES (?, ?, ?, ?, ?)";
		executeUpdate(statement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), gameJson);
	}

	@Override
	public GameData getGame(int gameID) throws DataAccessException {
		try (var conn = DatabaseManager.getConnection()) {
			var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameData FROM Games WHERE gameID=?";
			try (var ps = conn.prepareStatement(statement)) {
				ps.setInt(1, gameID);
				try (var rs = ps.executeQuery()) {
					if (rs.next()) {

						String gameJson = rs.getString("gameData");
						ChessGame gameObject = gson.fromJson(gameJson, ChessGame.class);

						return new GameData(
								rs.getInt("gameID"),
								rs.getString("whiteUsername"),
								rs.getString("blackUsername"),
								rs.getString("gameName"),
								gameObject
						);
					}
				}
			}
		} catch (Exception e) {
			throw new DataAccessException("Game not found");
		}
		throw new DataAccessException("Game not found");
	}

	@Override
	public boolean gameIDExists(int gameID) {
		try {
			getGame(gameID);
			return true;
		} catch (DataAccessException e) {
			if (e.getMessage().equals("Game not found")) {
				return false;
			}
		}
		return false;
	}


	@Override
	public void clearAllGames() throws DataAccessException {
		var statement = "DELETE FROM Games";
		executeUpdate(statement);
	}

	@Override
	public void updateGame(GameData game) throws DataAccessException {
		if (!gameIDExists(game.gameID())) {
			throw new DataAccessException("Game not found");
		}

		String gameJson = gson.toJson(game.game());

		var statement = "UPDATE Games SET whiteUsername=?, blackUsername=?, gameName=?, gameData=? WHERE gameID=?";
		executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), gameJson, game.gameID());
	}

	private void executeUpdate(String statement, Object... params) throws DataAccessException {
		try (var conn = DatabaseManager.getConnection()) {
			try (var ps = conn.prepareStatement(statement)) {
				for (int i = 0; i < params.length; i++) {
					var param = params[i];
					if (param instanceof Integer p) {
						ps.setInt(i + 1, p);
					} else if (param instanceof String p) {
						ps.setString(i + 1, p);
					} else if (param == null) {
						ps.setNull(i + 1, Types.NULL);
					}
				}
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			throw new DataAccessException("Unable to update database: " + e.getMessage());
		}
	}

	@Override
	public ArrayList<GameData> listGames() throws DataAccessException {
		var games = new ArrayList<GameData>();
		try (var conn = DatabaseManager.getConnection()) {
			var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameData FROM Games";
			try (var ps = conn.prepareStatement(statement);
				 var rs = ps.executeQuery()) {
				while (rs.next()) {

					String gameJson = rs.getString("gameData");
					ChessGame gameObject = gson.fromJson(gameJson, ChessGame.class);

					games.add(new GameData(
							rs.getInt("gameID"),
							rs.getString("whiteUsername"),
							rs.getString("blackUsername"),
							rs.getString("gameName"),
							gameObject
					));
				}
			}
		} catch (SQLException e) {
			throw new DataAccessException("Unable to list games: " + e.getMessage());
		}
		return games;
	}

	@Override
	protected String[] getCreateStatements() {
		return new String[]{
            """
            CREATE TABLE IF NOT EXISTS Games (
              `gameID` INT PRIMARY KEY,
              `whiteUsername` VARCHAR(100),
              `blackUsername` VARCHAR(100),
              `gameName` VARCHAR(255) NOT NULL,
              `gameData` TEXT NOT NULL,
              FOREIGN KEY (`whiteUsername`) REFERENCES users(`username`) ON DELETE SET NULL,
              FOREIGN KEY (`blackUsername`) REFERENCES users(`username`) ON DELETE SET NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
		};
	}
}
