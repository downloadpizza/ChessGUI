package net.downloadpizza.chessgui.renderer;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import net.downloadpizza.chessgui.board.ChessPiece;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ChessBoardRenderer extends WGridPanel {
    private static final Identifier EMPTY_FIELD = new Identifier("pieces", "empty.png");
    //                                 aarrggbb
    private static final int BLACK = 0xff333333;
    private static final int WHITE = 0xffffffff;
    private static final int RED =   0xffe01111;


    private static final BackgroundPainter CHECKER_BOARD = (left, top, panel) -> {
        int size = ShowBoardGui.pieceSize;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                boolean whiteField = (x % 2) == (y % 2);
                ScreenDrawing.coloredRect(left + x * size, top + y * size, size, size, whiteField ? WHITE : BLACK);
            }
        }
    };

    private static BackgroundPainter createHighlightingBackgroundPainter(boolean[][] highlight) {
        return (left, top, panel) -> {
            int size = ShowBoardGui.pieceSize;

            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    boolean whiteField = (x % 2) == (y % 2);
                    int color = whiteField ? WHITE : BLACK;
                    if (highlight[x][y]) {
                        ScreenDrawing.coloredRect(left + x * size, top + y * size, size, size, RED);
                        ScreenDrawing.coloredRect(2 + left + x * size, 2 + top + y * size, size - 4, size - 4, color);
                    } else {
                        ScreenDrawing.coloredRect(left + x * size, top + y * size, size, size, color);
                    }
                }
            }
        };
    }

    private final WSprite[][] sprites = new WSprite[8][8];

    public ChessBoardRenderer() {
        super(ShowBoardGui.pieceSize);
        this.setSize(8 * ShowBoardGui.pieceSize, 8 * ShowBoardGui.pieceSize);
        this.setBackgroundPainter(CHECKER_BOARD);

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                WSprite field = new WSprite(EMPTY_FIELD);
                sprites[x][y] = field;
                this.add(field, x, y);
            }
        }
    }

    public void redrawNormal(ChessPiece[][] board) {
        this.setBackgroundPainter(CHECKER_BOARD);
        redrawBoard(board);
    }

    public void redrawDiff(ChessPiece[][] board, boolean[][] changes) {
        this.setBackgroundPainter(createHighlightingBackgroundPainter(changes));
        redrawBoard(board);
    }

    private void redrawBoard(ChessPiece[][] board) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                ChessPiece piece = board[x][y];
                Identifier icon;
                if (piece == null) {
                    icon = EMPTY_FIELD;
                } else {
                    icon = piece.getPath();
                }

                sprites[x][y].setImage(icon);
            }
        }
    }
}
