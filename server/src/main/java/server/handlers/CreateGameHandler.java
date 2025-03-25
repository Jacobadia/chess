package server.handlers;

import com.google.gson.Gson;
import exception.ResponseException;
import service.GameService;
import service.requestresult.*;
import spark.Request;
import spark.Response;
import spark.Route;


public class CreateGameHandler implements Route {
	private final GameService gameService;
	private final Gson gson = new Gson();

	public CreateGameHandler(GameService gameService) {
		this.gameService = gameService;
	}

	@Override
	public Object handle(Request req, Response res) throws ResponseException {

		String authToken = req.headers("Authorization");
		CreateGameRequest bodyRequest = gson.fromJson(req.body(), CreateGameRequest.class);
		String gameName = bodyRequest.gameName();
		CreateGameRequest request = new CreateGameRequest(gameName, authToken);

		CreateGameResult result = gameService.createGame(request);


		if (result.message() != null) {
			if (result.message().equals("Error: unauthorized")) {
				res.status(401);
				throw new ResponseException(401, result.message());
			} else if (result.message().equals("Error: bad request")) {
				res.status(401);
				throw new ResponseException(401, result.message());
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