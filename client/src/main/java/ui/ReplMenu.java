package ui;



import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ReplMenu {
	private final PreLogInClient client;

	public ReplMenu(String serverUrl) {
		client = new PreLogInClient(serverUrl);
	}

	public void run() {
		System.out.println("Welcome to Chess! Type Help to get started.");
		System.out.print(client.help());

		Scanner scanner = new Scanner(System.in);
		var result = "";
		while (!result.equals("quit")) {
			printPrompt();
			String line = scanner.nextLine();

			try {
				result = client.eval(line);
				System.out.print(result);
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
