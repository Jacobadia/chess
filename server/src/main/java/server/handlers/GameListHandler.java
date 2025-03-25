package server.handlers;

import com.google.gson.Gson;
import exception.ResponseException;
import service.GameService;
import service.requestresult.AuthTokenRequest;
import service.requestresult.ListGamesResult;
import spark.Request;
import spark.Response;
import spark.Route;

import static server.handlers.LoginHandler.throwEx;


public class GameListHandler implements Route {
	private final GameService gameService;
	private final Gson gson = new Gson();

	public GameListHandler(GameService gameService) {
		this.gameService = gameService;
	}

	@Override
	public Object handle(Request req, Response res) throws ResponseException {

		String authToken = req.headers("Authorization");
		AuthTokenRequest request = new AuthTokenRequest(authToken);
		ListGamesResult result = gameService.listGames(request);

		if (!throwEx(result.message())) {
			res.status(200);
		}
		return gson.toJson(result);
	}
}