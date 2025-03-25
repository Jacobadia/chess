package server.handlers;

import com.google.gson.Gson;
import exception.ResponseException;
import service.UserService;
import service.requestresult.*;
import spark.*;


public class RegisterHandler implements Route {
	private final UserService userService;
	private final Gson gson = new Gson();

	public RegisterHandler(UserService userService) {
		this.userService = userService;
	}

	@Override
	public Object handle(Request req, Response res) throws ResponseException {

		UserInfoRequest request = gson.fromJson(req.body(), UserInfoRequest.class);
		AuthUserNameResult result = userService.register(request);

		if (result.message() != null) {
			// Determine error type based on message
			if (result.message().equals("Error: bad request")) {
				res.status(400);
				throw new ResponseException(400, result.message());
			} else if (result.message().equals("Error: already taken")) {
				res.status(403);
				throw new ResponseException(403, result.message());
			} else {
				res.status(500);
				throw new ResponseException(500, result.message());
			}
		} else {
			res.status(200);
		}
		return gson.toJson(result);
	}
}

