package ui;



import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ReplMenu {
	private BasicClient client;
	protected static State state = State.SIGNEDOUT;
	private final String url;
	protected static String myAuth;
	private final PreLogInClient preClient;
	private final LogedInClient logedClient;
	private final BasicClient gameClient;

	public ReplMenu(String serverUrl) {
		client = new PreLogInClient(serverUrl);
		this.url = serverUrl;
		preClient = new PreLogInClient(url);
		logedClient = new LogedInClient(url);
		gameClient = new GameClient(url);
	}

	public void run() {
		System.out.println("Welcome to Chess! Type Help to get started.");
		System.out.print(client.help());

		Scanner scanner = new Scanner(System.in);
		var result = "";
		while (!result.equals("quit")) {
			printPrompt();
			String line = scanner.nextLine();

			if (state == State.SIGNEDOUT) {
				client = preClient;
			} else if (state == State.SIGNEDIN) {
				client = logedClient;
			}
			else if (state == State.INGAME) {
				client = gameClient;
			}

			try {
				result = client.eval(line);
				System.out.print(ERASE_SCREEN + result);
			} catch (Throwable e) {
				var msg = e.toString();
				System.out.print(msg);
			}
		}
		System.out.println();
	}

	private void printPrompt() {
		System.out.print("\n" + ">>> ");
	}

}
