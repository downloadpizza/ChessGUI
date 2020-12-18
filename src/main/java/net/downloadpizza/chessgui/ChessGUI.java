package net.downloadpizza.chessgui;

import net.downloadpizza.chessgui.board.ChessPiece;
import net.downloadpizza.chessgui.renderer.ChessBoardRenderer;
import net.downloadpizza.chessgui.renderer.PizzaScreen;
import net.downloadpizza.chessgui.renderer.ShowBoardGui;
import net.downloadpizza.chessgui.selection.ChangeBoardGui;
import net.downloadpizza.chessgui.selection.CornerDirection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class ChessGUI implements ModInitializer {
    private static final String KEYBIND_CATEGORY = "Chess Keybindings";

    private final ChangeBoardGui cbg = new ChangeBoardGui();
    private final ChessBoardRenderer cbr = new ChessBoardRenderer();
    private final ShowBoardGui sbg = new ShowBoardGui(cbr);

    private boolean showDiff = false;

    private ChessPiece[][] lastBoard = null;

    @Override
    public void onInitialize() {
        KeyBinding selectionMenu = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Change Board",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_I,
                KEYBIND_CATEGORY));

        KeyBinding displayBoard = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Show Board",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                KEYBIND_CATEGORY));

        KeyBinding toggleDiff = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Toggle Diff",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                KEYBIND_CATEGORY));

        sbg.setWhite(cbg.isWhiteSide());

        ClientTickEvents.END_CLIENT_TICK.register(e ->
        {
            if (selectionMenu.isPressed())
                MinecraftClient.getInstance().openScreen(new PizzaScreen(cbg));

            if (displayBoard.isPressed())
                MinecraftClient.getInstance().openScreen(new PizzaScreen(sbg));

            if (toggleDiff.wasPressed()) {
                showDiff = !showDiff;
                assert MinecraftClient.getInstance().player != null;
                MinecraftClient.getInstance().player.sendMessage(Text.of("Diff toggled " + (showDiff ? "on" : "off")), true);
            }

            ChessPiece[][] newBoard = getBoard();
            boolean white = cbg.isWhiteSide();
            if(white != sbg.isWhite()) {
                sbg.setWhite(white);
            }

            if(newBoard == null) {
                lastBoard = null;
                return;
            }

            if(lastBoard == null) {
                cbr.redrawNormal(newBoard);
                lastBoard = newBoard;
                return;
            }

            if(!checkBoardsEqual(newBoard, lastBoard)) {
                System.out.println(showDiff);
                if(showDiff) {
                    boolean[][] changes = createBoardDiff(newBoard, lastBoard);
                    cbr.redrawDiff(newBoard, changes);
                    System.out.println(Arrays.deepToString(changes));
                } else {
                    cbr.redrawNormal(newBoard);
                }

                lastBoard = newBoard;
            }
        });
    }

    public static boolean checkBoardsEqual(ChessPiece[][] board1, ChessPiece[][] board2) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (!Objects.equals(board1[x][y], board2[x][y]))
                    return false;
            }
        }
        return true;
    }

    public static boolean[][] createBoardDiff(ChessPiece[][] board1, ChessPiece[][] board2) {
        boolean[][] diff = new boolean[8][8];

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                diff[x][y] = !Objects.equals(board1[x][y], board2[x][y]);
            }
        }
        return diff;
    }

    ChessPiece[][] getBoard() {
        if(cbg.getSpacing() == -1)
            return null;

        World world = MinecraftClient.getInstance().world;
        if(world == null)
            return null;

        ChessPiece[][] board = new ChessPiece[8][8];

        cbg.getDirection();
        final int spacing = cbg.getSpacing();
        CornerDirection cornerDirection = cbg.getDirection();

        for (int x = 0; x < 8; x++) {
            BlockPos current = cbg.getBlock();
            for (int i = 0; i < x*spacing; i++) {
                current = current.add(cornerDirection.getXBoardOffset().getVector());
            }
            for (int y = 0; y < 8; y++) {
                board[y][7-x] = null;
                BlockEntity blockEntity = world.getBlockEntity(current);
                if(blockEntity != null && blockEntity.getType() == BlockEntityType.BANNER) {
                    BannerBlockEntity bannerBlockEntity = (BannerBlockEntity) blockEntity;
                    Text name = bannerBlockEntity.getCustomName();
                    if(name != null)
                        board[y][7-x] = ChessPiece.pieceOrNull(name.getString());
                }
                for (int i = 0; i < spacing; i++) {
                    current = current.add(cornerDirection.getYBoardOffset().getVector());
                }
            }
        }
        return board;
    }
}
