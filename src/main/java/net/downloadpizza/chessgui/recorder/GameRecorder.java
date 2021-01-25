package net.downloadpizza.chessgui.recorder;

import net.downloadpizza.chessgui.board.ChessPiece;

import java.io.*;

public class GameRecorder {
    private final PrintStream output;

    public GameRecorder(File outputFile) {
        try {
            outputFile.createNewFile();
            output = new PrintStream(outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeMove(ChessPiece[][] board, boolean white) {
        if(white) {
            for (int y = 0; y < 8; y++) {
                int emptyCount = 0;

                for (int x = 0; x < 8; x++) {
                    ChessPiece piece = board[x][y];
                    if (piece != null) {
                        if (emptyCount != 0) {
                            output.print(emptyCount);
                            emptyCount = 0;
                        }
                        output.print(piece.fenString());
                    } else {
                        emptyCount += 1;
                    }
                }
                if (emptyCount != 0) {
                    output.print(emptyCount);
                }
                if(y != 7)
                    output.print("/");
            }
        } else {
            for (int y = 7; y >= 0; y--) {
                int emptyCount = 0;

                for (int x = 7; x >= 0; x--) {
                    ChessPiece piece = board[x][y];
                    if (piece != null) {
                        if (emptyCount != 0) {
                            output.print(emptyCount);
                            emptyCount = 0;
                        }
                        output.print(piece.fenString());
                    } else {
                        emptyCount += 1;
                    }
                }
                if (emptyCount != 0) {
                    output.print(emptyCount);
                }
                if(y != 0)
                    output.print("/");
            }
        }
        output.println();
    }

    public void close() {
        output.close();
    }
}
