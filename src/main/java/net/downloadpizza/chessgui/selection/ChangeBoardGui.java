package net.downloadpizza.chessgui.selection;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ChangeBoardGui extends LightweightGuiDescription {
    private final WNumberField xTextField = new WNumberField();
    private final WNumberField yTextField = new WNumberField();
    private final WNumberField zTextField = new WNumberField();

    private final WNumberField spacingField = new WNumberField();
    private final WSlider directionSlider = new WSlider(0, 3, Axis.HORIZONTAL);

    private final WToggleButton whiteToggle = new WToggleButton();

    public ChangeBoardGui() {
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setSize(64, 32);

        WLabel xLabel = new WLabel(new LiteralText("x"));
        WLabel yLabel = new WLabel(new LiteralText("y"));
        WLabel zLabel = new WLabel(new LiteralText("z"));
        WLabel sliderLabel = new WLabel(new LiteralText("NE/SE/SW/NW"));
        WLabel spacingLabel = new WLabel(new LiteralText("spacing"));
        WLabel whiteLabel = new WLabel(new LiteralText("White side"));

        whiteToggle.setToggle(true);

        root.add(xLabel, 0, 0, 3, 1);
        root.add(xTextField, 0, 1, 3, 1);
        root.add(yLabel, 4, 0, 3, 1);
        root.add(yTextField, 4, 1, 3, 1);
        root.add(zLabel, 8, 0, 3, 1);
        root.add(zTextField, 8, 1, 3, 1);

        root.add(sliderLabel, 0, 3, 4, 1);
        root.add(directionSlider, 0, 4, 4, 1);

        root.add(spacingLabel, 6, 3, 3, 1);
        root.add(spacingField, 6, 4, 3, 1);

        root.add(whiteLabel, 0, 6, 3, 1);
        root.add(whiteToggle, 0, 7, 1, 1);

        WButton button = new WButton(new LiteralText("From POS"));
        root.add(button, 3, 9, 4, 1);

        button.setOnClick(() -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            assert player != null;
            BlockPos bp = player.getBlockPos();
            xTextField.setText(String.valueOf(bp.getX()));
            yTextField.setText(String.valueOf(bp.getY()));
            zTextField.setText(String.valueOf(bp.getZ()));

            int cd;

            switch (player.getHorizontalFacing()) {
                case NORTH:
                    cd = 0;
                    break;
                case EAST:
                    cd = 1;
                    break;
                case SOUTH:
                    cd = 2;
                    break;
                case WEST:
                    cd = 3;
                    break;
                default:
                    throw new IllegalStateException("Shouldnt face up or down");
            }

            directionSlider.setValue(cd);
        });

        root.validate(this);
    }

    public BlockPos getBlock() {
        try {
            int x = xTextField.getValue();
            int y = yTextField.getValue();
            int z = zTextField.getValue();

            return new BlockPos(x, y, z);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public CornerDirection getDirection() {
        switch(directionSlider.getValue()) {
            case 0:
                return CornerDirection.NORTH_EAST;
            case 1:
                return CornerDirection.SOUTH_EAST;
            case 2:
                return CornerDirection.SOUTH_WEST;
            case 3:
                return CornerDirection.NORTH_WEST;
            default:
                throw new IllegalStateException("Outside of definition range");
        }
    }

    public int getSpacing() {
        try {
            if(spacingField.getValue() < 1) {
                return -1;
            }
            return spacingField.getValue();
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public boolean isWhiteSide() {
        return whiteToggle.getToggle();
    }
}

class WNumberField extends WTextField {
    private Consumer<Integer> changeListener;

    public WNumberField() {
        super(new LiteralText("0"));
        this.setTextPredicate(s -> {
            if (s.equals("")) {
                return true;
            } else {
                try {
                    Integer.parseInt(s);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        });
        this.setChangedListener(text -> {
            if (changeListener != null) {
                try {
                    changeListener.accept(Integer.parseInt(text));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public int getValue() throws NumberFormatException {
        return Integer.parseInt(this.getText());
    }

    public void setNumberChangeListener(Consumer<Integer> f) {
        changeListener = f;
    }
}

