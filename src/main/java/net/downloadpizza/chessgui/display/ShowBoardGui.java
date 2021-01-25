package net.downloadpizza.chessgui.display;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import static net.downloadpizza.chessgui.ChessGUI.NAMESPACE;

@Environment(EnvType.CLIENT)
public class ShowBoardGui extends LightweightGuiDescription {
    private static final Identifier[] ROWS = {
            new Identifier(NAMESPACE, "font/1.png"),
            new Identifier(NAMESPACE, "font/2.png"),
            new Identifier(NAMESPACE, "font/3.png"),
            new Identifier(NAMESPACE, "font/4.png"),
            new Identifier(NAMESPACE, "font/5.png"),
            new Identifier(NAMESPACE, "font/6.png"),
            new Identifier(NAMESPACE, "font/7.png"),
            new Identifier(NAMESPACE, "font/8.png")
    };
    private static final Identifier[] COLUMNS = {
            new Identifier(NAMESPACE, "font/a.png"),
            new Identifier(NAMESPACE, "font/b.png"),
            new Identifier(NAMESPACE, "font/c.png"),
            new Identifier(NAMESPACE, "font/d.png"),
            new Identifier(NAMESPACE, "font/e.png"),
            new Identifier(NAMESPACE, "font/f.png"),
            new Identifier(NAMESPACE, "font/g.png"),
            new Identifier(NAMESPACE, "font/h.png")
    };
    
    private static final Identifier EMPTY_FIELD = new Identifier(NAMESPACE, "pieces/empty.png");

    public static final int pieceSize = 25;

    private final WSprite[] leftLabels = newSpriteArray();
    private final WSprite[] rightLabels = newSpriteArray();
    private final WSprite[] topLabels = newSpriteArray();
    private final WSprite[] bottomLabels = newSpriteArray();

    private static WSprite[] newSpriteArray() {
        WSprite[] sprites = new WSprite[8];
        for (int i = 0; i < 8; i++) {
            sprites[i] = new WSprite(EMPTY_FIELD);
        }
        return sprites;
    }

    private boolean white = false;

    ChessBoardRenderer cbr;
    public ShowBoardGui(ChessBoardRenderer cbr) {
        setWhite(false);
        this.cbr = cbr;
        WGridPanel root = new WGridPanel(pieceSize);
        setRootPanel(root);

        root.setSize(pieceSize * 10, pieceSize * 10);

        for (int i = 0; i < 8; i++) {
            root.add(leftLabels[i], 0, i + 1, 1, 1);
            root.add(rightLabels[i], 9, i + 1, 1, 1);

            root.add(topLabels[i], i + 1, 0, 1, 1);
            root.add(bottomLabels[i], i + 1, 9, 1, 1);
        }

        root.add(cbr, 1, 1, 8, 8);
        root.validate(this);
    }

    public void setWhite(boolean white) {
        this.white = white;

        for (int i = 0; i < 8; i++) {
            int rowIndex = white ? 7 - i : i;
            int columnIndex = white ? i : 7 - i;
            leftLabels[i].setImage(ROWS[rowIndex]);
            rightLabels[i].setImage(ROWS[rowIndex]);

            topLabels[i].setImage(COLUMNS[columnIndex]);
            bottomLabels[i].setImage(COLUMNS[columnIndex]);
        }
    }

    public boolean isWhite() {
        return white;
    }
}

