package net.downloadpizza.chessgui.board;

import net.minecraft.util.Identifier;

import static net.downloadpizza.chessgui.ChessGUI.NAMESPACE;

public class ChessPiece {
    private final Piece piece;
    private final Color color;

    public Identifier getPath() {
        return new Identifier(NAMESPACE, "pieces/" + color.name().toLowerCase()+"/"+piece.name().toLowerCase()+".png");
    }

    public static ChessPiece pieceOrNull(String name) {
        try {
            return new ChessPiece(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public ChessPiece(String name) {
        String[] nameParts = name.split(" ");
        color = Color.valueOf(nameParts[0].toUpperCase());
        piece = Piece.valueOf(nameParts[1].toUpperCase());
    }

    public Piece getPiece() {
        return piece;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChessPiece that = (ChessPiece) o;

        if (piece != that.piece) return false;
        return color == that.color;
    }

    public String fenString() {
        String pieceString;

        switch(piece) {
            case PAWN:
                pieceString = "p";
                break;
            case ROOK:
                pieceString = "r";
                break;
            case KNIGHT:
                pieceString = "n";
                break;
            case BISHOP:
                pieceString = "b";
                break;
            case QUEEN:
                pieceString = "q";
                break;
            case KING:
                pieceString = "k";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + piece);
        }

        if(color == Color.BLACK) {
            return pieceString.toLowerCase();
        } else {
            return pieceString.toUpperCase();
        }
    }
}

