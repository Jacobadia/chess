package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.GameData;
import service.requestresult.*;

import java.util.ArrayList;
import java.util.List;


public class GameService {

	private final MemoryUserDAO userDAO;
	private final MemoryAuthDAO authDAO;
	private final MemoryGameDAO gameDAO;

	public GameService(MemoryUserDAO userDAO, MemoryAuthDAO authDAO, MemoryGameDAO gameDAO) {
		this.userDAO = userDAO;
		this.authDAO = authDAO;
		this.gameDAO = gameDAO;
	}

	public ListGamesResult listgames(AuthTokenRequest r) {

		try {
			if (!authDAO.authExists(r.authToken())) {
				return new ListGamesResult(null,  "Error: unauthorized");
			}
			List<GameData> listGameData = gameDAO.listGames();
			List<GameData> list = new ArrayList<>();
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

}


