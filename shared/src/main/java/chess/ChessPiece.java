package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private PieceType type;
    private ChessGame.TeamColor teamColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = teamColor;
        this.type = type;
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }


    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        // Define movement logic based on the type of piece
        switch (getPieceType()) {
            case BISHOP:
                addDiagonalMoves(board, myPosition, validMoves);
                break;
            case ROOK:
                addStraightMoves(board, myPosition, validMoves);
                break;
            case QUEEN:
                addDiagonalMoves(board, myPosition, validMoves);
                addStraightMoves(board, myPosition, validMoves);
                break;
//            case KNIGHT:
//                addKnightMoves(board, myPosition, validMoves);
//                break;
//            case KING:
//                addKingMoves(board, myPosition, validMoves);
//                break;
//            case PAWN:
//                addPawnMoves(board, myPosition, validMoves);
//                break;
        }

        return validMoves;
    }


    private void addDiagonalMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves) {
        int[][] directions = {
                {1, 1},   // Up-right
                {1, -1},  // Up-left
                {-1, 1},  // Down-right
                {-1, -1}  // Down-left
        };

        for (int[] direction : directions) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while (true) {
                row += direction[0];
                col += direction[1];

                // Stop if out of bounds
                if (row <= 0 || row > 8 || col <= 0 || col > 8) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece occupyingPiece = board.getPiece(newPosition);

                // Add the move to valid moves
                validMoves.add(new ChessMove(myPosition, newPosition, null));

                // Stop if a piece is encountered
                if (occupyingPiece != null) {
                    break;
                }
            }
        }
    }

    private void addStraightMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves) {
        int[][] directions = {
                {1, 0},  // Up
                {-1, 0}, // Down
                {0, 1},  // Right
                {0, -1}  // Left
        };

        for (int[] direction : directions) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while (true) {
                row += direction[0];
                col += direction[1];

                // Stop if out of bounds
                if (row <= 0 || row > 8 || col <= 0 || col > 8) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece occupyingPiece = board.getPiece(newPosition);

                // Add the move to valid moves
                validMoves.add(new ChessMove(myPosition, newPosition, null));

                // Stop if a piece is encountered
                if (occupyingPiece != null) {
                    break;
                }
            }
        }
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
