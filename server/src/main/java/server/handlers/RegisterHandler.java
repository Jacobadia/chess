package server.handlers;

import com.google.gson.Gson;
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
	public Object handle(Request req, Response res) {

		RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);
		RegisterResult result = userService.register(request);

		if (result.message() != null) {
			// Determine error type based on message
			if (result.message().equals("Error: bad request")) {
				res.status(400);
			} else if (result.message().equals("Error: already taken")) {
				res.status(403);
			} else {
				res.status(500);
			}
		} else {
			res.status(200);
		}
		return gson.toJson(result);
	}
}

