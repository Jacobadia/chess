package ui;

import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

public class LogedInClient implements BasicClient {
	private final ServerFacade server;
    private final String serverUrl;

    public LogedInClient(String url) {
        this.serverUrl = url;
        server = new ServerFacade(serverUrl);
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "logout" -> logout();
                case "quit" -> "quit";
                case "help" -> help();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            var gameName = params[0];
            var gameId = server.createGame(gameName, ReplMenu.myAuth);
            return "Game created with ID: " + gameId;
        }
        throw new ResponseException(400, "Expected: <GAME NAME>");
    }

    @Override
    public String help() {
        return """
                - create <GAME NAME> - Create a game
                - list - All games
                - join <ID> [WHITE or BLACK] - Join as player
                - observe <ID> - Watch a game
                - logout - exit your account
                - quit - exit program
                - help - all possible commands
                """;
    }

}
