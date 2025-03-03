package service;

import chess.ChessGame;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.GameData;
import service.requestresult.*;

import java.util.ArrayList;
import java.util.UUID;

import static java.lang.Math.abs;

public class GameService {

	private final MemoryUserDAO userDAO;
	private final MemoryAuthDAO authDAO;
	private final MemoryGameDAO gameDAO;

	public GameService(MemoryUserDAO userDAO, MemoryAuthDAO authDAO, MemoryGameDAO gameDAO) {
		this.userDAO = userDAO;
		this.authDAO = authDAO;
		this.gameDAO = gameDAO;
	}

	public ListGamesResult listGames(AuthTokenRequest r) {

		try {
			if (!authDAO.authExists(r.authToken())) {
				return new ListGamesResult(null,  "Error: unauthorized");
			}
			ArrayList<GameData> listGameData = gameDAO.listGames();
			ArrayList<GameData> list = new ArrayList<>();
			for (GameData game : listGameData) {
				GameData gameData = new GameData(
						game.gameID(),
						game.whiteUsername(),
						game.blackUsername(),
						game.gameName(),
						null);
				list.add(gameData);
			}
			return new ListGamesResult(list, null);
		} catch (Exception e) {
			return new ListGamesResult(null,  "Error: " + e.getMessage());
		}
	}


	public CreateGameResult createGame(CreateGameRequest r) {

		try {

			if (r.gameName() == null || r.gameName().isBlank()) {
				return new CreateGameResult(null, "Error: bad request" );
			}

			if (!authDAO.authExists(r.authToken())) {
				return new CreateGameResult(null,  "Error: unauthorized");
			}

			UUID uuid = UUID.randomUUID();
			int newID = abs(uuid.hashCode());
			GameData newGame = new GameData(
					newID,
					"",
					"",
					r.gameName(),
					new ChessGame());
			gameDAO.createGame(newGame);
			return new CreateGameResult(newID, null);
		} catch (Exception e) {
			return new CreateGameResult(null,  "Error: " + e.getMessage());
		}
	}

}


