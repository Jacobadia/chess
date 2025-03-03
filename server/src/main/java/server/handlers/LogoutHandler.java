package server.handlers;

import com.google.gson.Gson;
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
	public Object handle(Request req, Response res) {

		String authToken = req.headers("Authorization");
		LogoutRequest request = new LogoutRequest(authToken);
		RegisterResult result = userService.logout(request);

		if (result.message() != null) {
			// Determine error type based on message
			if (result.message().equals("Error: unauthorized")) {
				res.status(401);
			} else {
				res.status(500);
			}
		} else {
			res.status(200);
		}
		return gson.toJson(result);
	}
}