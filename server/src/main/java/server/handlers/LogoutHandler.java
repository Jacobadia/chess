package server.handlers;

import com.google.gson.Gson;
import exception.ResponseException;
import service.UserService;
import service.requestresult.*;
import spark.*;


public class LogoutHandler implements Route {
	private final UserService userService;
	private final Gson gson = new Gson();

	public LogoutHandler(UserService userService) {
		this.userService = userService;
	}

	@Override
	public Object handle(Request req, Response res) throws ResponseException {

		String authToken = req.headers("Authorization");
		AuthTokenRequest request = new AuthTokenRequest(authToken);
		AuthUserNameResult result = userService.logout(request);

		if (result.message() != null) {
			// Determine error type based on message
			if (result.message().equals("Error: unauthorized")) {
				throw new ResponseException(401, result.message());
			} else {
				throw new ResponseException(500, result.message());
			}
		} else {
			res.status(200);
		}
		return gson.toJson(result);
	}
}