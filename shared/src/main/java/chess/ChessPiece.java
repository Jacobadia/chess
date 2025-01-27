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
        this.teamColor = pieceColor;
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
        return teamColor;
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
                if (occupyingPiece == null || occupyingPiece.getTeamColor() != this.getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }

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
                if (occupyingPiece == null || occupyingPiece.getTeamColor() != this.getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }

                // Stop if a piece is encountered
                if (occupyingPiece != null) {
                    break;
                }
            }
        }
    }

    private void addKnightMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves) {
        int[][] moves = {
                {2, 1},   // Move 2 up, 1 right
                {2, -1},  // Move 2 up, 1 left
                {1, 2},   // Move 1 up, 2 right
                {1, -2},  // Move 1 up, 2 left
                {-1, 2},  // Move 1 down, 2 right
                {-1, -2}, // Move 1 down, 2 left
                {-2, 1},  // Move 2 down, 1 right
                {-2, -1}  // Move 2 down, 1 left
        };

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int[] move : moves) {
            int newRow = row + move[0];
            int newCol = col + move[1];

            if (newRow > 0 && newRow <= 8 && newCol > 0 && newCol <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece occupyingPiece = board.getPiece(newPosition);

                if (occupyingPiece == null || occupyingPiece.getTeamColor() != this.getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
    }

    private void addKingMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves) {
        int[][] moves = {
                {1, 0},
                {1, 1},
                {0, 1},
                {-1, 1},
                {-1, 0},
                {-1, -1},
                {0, -1},
                {1, -1}
        };

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int[] move : moves) {
            int newRow = row + move[0];
            int newCol = col + move[1];

            if (newRow > 0 && newRow <= 8 && newCol > 0 && newCol <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece occupyingPiece = board.getPiece(newPosition);

                if (occupyingPiece == null || occupyingPiece.getTeamColor() != this.getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
    }

    private void addPawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // Determine movement direction based on team color
        int direction = (this.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;

        // Forward movement by one square
        ChessPosition forward = new ChessPosition(row + direction, col);
        if (board.getPiece(forward) == null) {
            validMoves.add(new ChessMove(myPosition, forward, null));

            // Double forward movement on the pawn's first move
            if ((this.getTeamColor() == ChessGame.TeamColor.WHITE && row == 2) ||
                    (this.getTeamColor() == ChessGame.TeamColor.BLACK && row == 7)) {
                ChessPosition doubleForward = new ChessPosition(row + 2 * direction, col);
                if (board.getPiece(doubleForward) == null) {
                    validMoves.add(new ChessMove(myPosition, doubleForward, null));
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
