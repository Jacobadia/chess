package ui;

import java.util.Arrays;

import exception.ResponseException;
import server.ServerFacade;

public class PreLogInClient implements BasicClient {
	private final ServerFacade server;

	public PreLogInClient(String url) {
		server = new ServerFacade(url);
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                case "help" -> help();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            var username = params[0];
            var password = params[1];
            var email = params[2];
            server.register(username, password, email);
            return "Registration successful." + login(username, password);
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            var username = params[0];
            var password = params[1];
            ReplMenu.myAuth = server.login(username, password);
            ReplMenu.state = State.SIGNEDIN;
			return String.format(" You signed in as %s. \n Press enter to continue", username);
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    @Override
    public String help() {
        return """
                - register <username> <password> <email> - Create an account
                - login <username> <password> - use existing account
                - quit
                - help - possible commands
                """;
    }

}
