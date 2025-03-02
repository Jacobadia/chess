package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
	private final ChessGame.TeamColor myColor;
	private ChessPiece.PieceType myType;
	private boolean doubleMoved;
	private boolean notMoved;

	public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
		myColor = pieceColor;
		myType = type;
		doubleMoved = false;
		notMoved = true;
	}

	/**
	 * The various different chess piece options
	 */
	public enum PieceType {
		KING,
		QUEEN,
		BISHOP,
		KNIGHT,
		ROOK,
		PAWN
	}

	/**
	 * @return Which team this chess piece belongs to
	 */
	public ChessGame.TeamColor getTeamColor() {
		return myColor;
	}

	/**
	 * @return which type of chess piece this piece is
	 */
	public PieceType getPieceType() {
		return myType;
	}

	public void setPieceType(PieceType newType) {
		myType = newType;
	}

	//for En Passant
	public boolean isDoubleMoved() {
		return doubleMoved;
	}

	public void setDoubleMoved(boolean doubleMoved) {
		this.doubleMoved = doubleMoved;
	}

	//for Castleing
	public boolean isNotMoved() {
		return notMoved;
	}

	public void setNotMoved(boolean notMoved) {
		this.notMoved = notMoved;
	}

	/**
	 * Calculates all the positions a chess piece can move to
	 * Does not take into account moves that are illegal due to leaving the king in
	 * danger
	 *
	 * @return Collection of valid moves
	 */
	public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
		Collection<ChessMove> validMoves = new ArrayList<>();
		switch (getPieceType()) {
			case BISHOP:
				addDiagonalLineMoves(board, myPosition, validMoves);
				break;
			case ROOK:
				addStraightLineMoves(board, myPosition, validMoves);
				break;
			case QUEEN:
				addDiagonalLineMoves(board, myPosition, validMoves);
				addStraightLineMoves(board, myPosition, validMoves);
				break;
			case KNIGHT:
				addKnightMoves(board, myPosition, validMoves);
				break;
			case KING:
				addKingMoves(board, myPosition, validMoves);
				break;
			case PAWN:
				addPawnMoves(board, myPosition, validMoves);
				break;
		}
		return validMoves;
	}

	private void addDiagonalLineMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves) {
		int[][] directions = {
				{1, 1}, {-1, 1}, {1, -1}, {-1, -1}
		};
		addInLineMoves(board, myPosition, validMoves, directions);
	}

	private void addStraightLineMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves) {
		int[][] directions = {
				{1, 0}, {-1, 0}, {0, -1}, {0, 1}
		};
		addInLineMoves(board, myPosition, validMoves, directions);
	}

	private void addInLineMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves, int[][] directions) {
		for (int[] direction : directions) {
			int row = myPosition.getRow();
			int col = myPosition.getColumn();

			while (true) {
				row = row + direction[0];
				col = col + direction[1];

				if (row < 1 || row > 8 || col < 1 || col > 8) {
					break;
				}

				ChessPosition newPosition = new ChessPosition(row, col);
				ChessPiece occupyingPiece = board.getPiece(newPosition);

				if (occupyingPiece == null || occupyingPiece.getTeamColor() != this.getTeamColor()) {
					validMoves.add(new ChessMove(myPosition, newPosition, null));
				}

				if (occupyingPiece != null) {
					break;
				}
			}
		}
	}

	private void addKingOrKnightMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves, int[][] moves) {
		for (int[] move : moves) {
			int row = myPosition.getRow() + move[0];
			int col = myPosition.getColumn() + move[1];

			if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
				ChessPosition newPosition = new ChessPosition(row, col);
				ChessPiece occupyingPiece = board.getPiece(newPosition);

				if (occupyingPiece == null || occupyingPiece.getTeamColor() != this.getTeamColor()) {
					validMoves.add(new ChessMove(myPosition, newPosition, null));
				}
			}
		}
	}

	private void addKnightMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves) {
		int[][] moves = {
				{2, 1}, {2, -1}, {-2, -1}, {-2, 1},
				{1, 2}, {-1, 2}, {-1, -2}, {1, -2}
		};
		addKingOrKnightMoves(board, myPosition, validMoves, moves);
	}

	private void addKingMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves) {
		int[][] moves = {
				{1, 1}, {1, -1}, {1, 0},
				{-1, 1}, {-1, -1}, {-1, 0},
				{0, -1}, {0, 1}
		};
		addKingOrKnightMoves(board, myPosition, validMoves, moves);

		//Castleing
		int myRow = myPosition.getRow();
		int myCol = myPosition.getColumn();
		//has king moved
		if (notMoved && myCol == 5) {

			//Right Rook
			int rRookCol = myCol + 3;
			if (board.getPiece(new ChessPosition(myRow, myCol + 1)) == null //empty space
					&& board.getPiece(new ChessPosition(myRow, myCol + 2)) == null //empty space
					&& board.getPiece(new ChessPosition(myRow, rRookCol)) != null //not empty
					&& board.getPiece(new ChessPosition(myRow, rRookCol)).isNotMoved()) { //castle that hasn't moved
				ChessPosition newPosition = new ChessPosition(myRow, myCol + 2);
				validMoves.add(new ChessMove(myPosition, newPosition, null));
			}

			//left Rook
			int lRookCol = myCol - 4;
			if (board.getPiece(new ChessPosition(myRow, myCol - 1)) == null //empty space
					&& board.getPiece(new ChessPosition(myRow, myCol - 2)) == null //empty space
					&& board.getPiece(new ChessPosition(myRow, myCol - 3)) == null //empty space
					&& board.getPiece(new ChessPosition(myRow, lRookCol)) != null //not empty
					&& board.getPiece(new ChessPosition(myRow, lRookCol)).isNotMoved()) { //castle that hasn't moved
				ChessPosition newPosition = new ChessPosition(myRow, myCol - 2);
				validMoves.add(new ChessMove(myPosition, newPosition, null));
			}

		}


	}

	private void addPawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves) {
		int direction = this.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;
		int row = myPosition.getRow();
		int col = myPosition.getColumn();

		row = row + direction;

		if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
			ChessPosition newPosition = new ChessPosition(row, col);
			ChessPiece occupyingPiece = board.getPiece(newPosition);

			if (occupyingPiece == null) {
				addPromotionOrRegularMove(validMoves, myPosition, newPosition, row);

				//Double move
				if ((row == 3 && this.getTeamColor() == ChessGame.TeamColor.WHITE)
						|| (row == 6 && this.getTeamColor() == ChessGame.TeamColor.BLACK)) {
					row = row + direction;
					newPosition = new ChessPosition(row, col);
					occupyingPiece = board.getPiece(newPosition);
					if (occupyingPiece == null) {
						validMoves.add(new ChessMove(myPosition, newPosition, null));
					}
				}
			}
		}

		//capture
		row = myPosition.getRow();
		col = myPosition.getColumn();
		row = row + direction;
		for (int i = 1; i > -3; i = i - 3) {
			col = col + i;
			if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
				ChessPosition newPosition = new ChessPosition(row, col);
				ChessPiece occupyingPiece = board.getPiece(newPosition);
				if (occupyingPiece != null && occupyingPiece.getTeamColor() != this.getTeamColor()) {
					addPromotionOrRegularMove(validMoves, myPosition, newPosition, row);
				}
			}
		}

		//En Passant
		addEnPassantMoves(board, myPosition, validMoves, direction);

	}


	private void addPromotionOrRegularMove(Collection<ChessMove> validMoves, ChessPosition myPosition, ChessPosition newPosition, int row) {
		if (row == 1 || row == 8) {
			validMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
			validMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
			validMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
			validMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
		} else {
			validMoves.add(new ChessMove(myPosition, newPosition, null));
		}
	}

	private void addEnPassantMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves, int direction) {
		int row = myPosition.getRow();
		int col = myPosition.getColumn();
		for (int i = 1; i > -3; i = i - 3) {
			col = col + i;
			if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
				ChessPosition enemyPosition = new ChessPosition(row, col);
				ChessPosition newPosition = new ChessPosition(row + direction, col);
				ChessPiece occupyingPiece = board.getPiece(enemyPosition);
				if (occupyingPiece != null && occupyingPiece.getTeamColor() != this.getTeamColor()
						&& occupyingPiece.isDoubleMoved()) {
					validMoves.add(new ChessMove(myPosition, newPosition, null));
				}
			}
		}
	}


	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ChessPiece that = (ChessPiece) o;
		return myColor == that.myColor && myType == that.myType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(myColor, myType);
	}

	@Override
	public String toString() {
		return "ChessPiece{" +
				"myColor=" + myColor +
				", myType=" + myType +
				'}';
	}
}
