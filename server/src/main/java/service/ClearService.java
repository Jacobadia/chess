package service;

import dataaccess.*;

import service.requestresult.*;


public class ClearService {

	private final UserDAO userDAO;
	private final AuthDAO authDAO;
	private final GameDAO gameDAO;

	public ClearService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
		this.userDAO = userDAO;
		this.authDAO = authDAO;
		this.gameDAO = gameDAO;
	}

	public MessageResult clear() {
		try {
			userDAO.clearAllUsers();
			authDAO.clearAllAuths();
			gameDAO.clearAllGames();
			return new MessageResult(null); // Success: return an empty result (200 OK)

		} catch (Exception e) {
			// Catch all unexpected exceptions and wrap in DataAccessException
			return new MessageResult("Error: " + e.getMessage());
		}

	}
}