package server.handlers;

import com.google.gson.Gson;
import exception.ResponseException;
import service.UserService;
import service.requestresult.*;
import spark.*;


public class LoginHandler implements Route {
	private final UserService userService;
	private final Gson gson = new Gson();

	public LoginHandler(UserService userService) {
		this.userService = userService;
	}

	@Override
	public Object handle(Request req, Response res) throws ResponseException {

		UserInfoRequest request = gson.fromJson(req.body(), UserInfoRequest.class);
		AuthUserNameResult result = userService.login(request);

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