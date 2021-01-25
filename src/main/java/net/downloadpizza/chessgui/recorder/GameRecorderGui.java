package net.downloadpizza.chessgui.recorder;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

import java.nio.file.Path;

@Environment(EnvType.CLIENT)
public class GameRecorderGui extends LightweightGuiDescription {
    private boolean running = false;
    private GameRecorder rcd = null;

    private final Path basePath = FabricLoader.getInstance().getConfigDir().resolve("chessrecordings");

    public GameRecorderGui() {
        basePath.toFile().mkdirs();
        WGridPanel wGridPanel = new WGridPanel();
        this.setRootPanel(wGridPanel);
        WTextField wTextField = new WTextField();
        wGridPanel.add(wTextField, 1, 1, 6, 1);
        WButton button = new WButton(Text.of(running ? "Stop" : "Start"));
        wGridPanel.add(button, 1, 4, 3, 1);
        button.setOnClick(() -> {
            String fileName = wTextField.getText();
            if(!running) {
                if(fileName.isEmpty() || fileName.contains("/"))
                    return;
                rcd = new GameRecorder(basePath.resolve(fileName).toFile());
                running = true;
            } else {
                rcd.close();
                running = false;
            }
            button.setLabel(Text.of(running ? "Stop" : "Start"));
        });

        wGridPanel.validate(this);
    }

    public GameRecorder getRcd() {
        return rcd;
    }
}
