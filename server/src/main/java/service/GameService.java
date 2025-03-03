package service;

import chess.ChessGame;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.GameData;
import service.requestresult.*;

import java.util.ArrayList;
import java.util.UUID;

import static java.lang.Math.abs;

public class GameService {

	private final MemoryAuthDAO authDAO;
	private final MemoryGameDAO gameDAO;

	public GameService(MemoryAuthDAO authDAO, MemoryGameDAO gameDAO) {
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
					null,
					null,
					r.gameName(),
					new ChessGame());
			gameDAO.createGame(newGame);
			return new CreateGameResult(newID, null);
		} catch (Exception e) {
			return new CreateGameResult(null,  "Error: " + e.getMessage());
		}
	}

	public MessageResult joinGame(JoinGameRequest r) {

		try {

			if (!gameDAO.gameIDExists(r.gameID()) || r.playerColor() == null) {
				return new MessageResult("Error: bad request" );
			}

			if (!authDAO.authExists(r.authToken())) {
				return new MessageResult("Error: unauthorized");
			}

			if (gameDAO.getGame(r.gameID()).whiteUsername() != null
					&& r.playerColor() == ChessGame.TeamColor.WHITE
					|| gameDAO.getGame(r.gameID()).blackUsername() != null
					&& r.playerColor() == ChessGame.TeamColor.BLACK) {
				return new MessageResult("Error: already taken");
			}

			if (r.playerColor() == ChessGame.TeamColor.WHITE){
				GameData myGame = gameDAO.getGame(r.gameID());
				GameData newGame = new GameData(
						myGame.gameID(),
						authDAO.getAuth(r.authToken()).username(),
						myGame.blackUsername(),
						myGame.gameName(),
						myGame.game());
				gameDAO.updateGame(newGame);
			} else if (r.playerColor() == ChessGame.TeamColor.BLACK){
				GameData myGame = gameDAO.getGame(r.gameID());
				GameData newGame = new GameData(
						myGame.gameID(),
						myGame.whiteUsername(),
						authDAO.getAuth(r.authToken()).username(),
						myGame.gameName(),
						myGame.game());
				gameDAO.updateGame(newGame);
			}
			return new MessageResult(null);
		} catch (Exception e) {
			return new MessageResult("Error: " + e.getMessage());
		}
	}

}


