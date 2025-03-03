package service;

import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;

import service.requestresult.*;


public class ClearService {

	private final MemoryUserDAO userDAO;
	private final MemoryAuthDAO authDAO;
	private final MemoryGameDAO gameDAO;

	public ClearService(MemoryUserDAO userDAO, MemoryAuthDAO authDAO, MemoryGameDAO gameDAO) {
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