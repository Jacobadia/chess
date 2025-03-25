package server.handlers;

import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import service.ClearService;
import service.requestresult.*;
import spark.*;


public class ClearHandler implements Route {
	private final ClearService clearService;
	private final Gson gson = new Gson();

	public ClearHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
		this.clearService = new ClearService(userDAO, authDAO, gameDAO);
	}

	@Override
	public Object handle(Request req, Response res) throws ResponseException {

		MessageResult result = clearService.clear();

		if (result.message() != null) {
			throw new ResponseException(500, result.message());
		} else {
			res.status(200);
		}
		return gson.toJson(result);
	}
}
