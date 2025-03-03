package server.handlers;

import com.google.gson.Gson;
import service.GameService;
import service.requestresult.AuthTokenRequest;
import service.requestresult.ListGamesResult;
import spark.Request;
import spark.Response;
import spark.Route;


public class GameListHandler implements Route {
	private final GameService gameService;
	private final Gson gson = new Gson();

	public GameListHandler(GameService gameService) {
		this.gameService = gameService;
	}

	@Override
	public Object handle(Request req, Response res) {

		String authToken = req.headers("Authorization");
		AuthTokenRequest request = new AuthTokenRequest(authToken);
		ListGamesResult result = gameService.listGames(request);

		if (result.message() != null) {
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