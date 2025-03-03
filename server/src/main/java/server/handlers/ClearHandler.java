package server.handlers;

import com.google.gson.Gson;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import service.ClearService;
import service.requestresult.*;
import spark.*;


public class ClearHandler implements Route {
	private final ClearService clearService;
	private final Gson gson = new Gson();

	public ClearHandler(MemoryUserDAO userDAO, MemoryAuthDAO authDAO, MemoryGameDAO gameDAO) {
		this.clearService = new ClearService(userDAO, authDAO, gameDAO);
	}

	@Override
	public Object handle(Request req, Response res) {

		ClearResult result = clearService.clear();

		if (result.message() != null) {
			res.status(500);
		} else {
			res.status(200);
		}
		return gson.toJson(result);
	}
}
