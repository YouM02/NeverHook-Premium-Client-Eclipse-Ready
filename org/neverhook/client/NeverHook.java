package org.neverhook.client;

import baritone.api.BaritoneAPI;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.neverhook.client.cmd.CommandManager;
import org.neverhook.client.components.DiscordRPC;
import org.neverhook.client.components.SplashProgress;
import org.neverhook.client.event.EventManager;
import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.game.EventShutdownClient;
import org.neverhook.client.event.events.impl.input.EventInputKey;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.FeatureManager;
import org.neverhook.client.files.FileManager;
import org.neverhook.client.files.impl.FriendConfig;
import org.neverhook.client.files.impl.HudConfig;
import org.neverhook.client.files.impl.MacroConfig;
import org.neverhook.client.files.impl.XrayConfig;
import org.neverhook.client.friend.FriendManager;
import org.neverhook.client.helpers.Helper;
import org.neverhook.client.helpers.math.RotationHelper;
import org.neverhook.client.helpers.render.BlurUtil;
import org.neverhook.client.macro.Macro;
import org.neverhook.client.macro.MacroManager;
import org.neverhook.client.settings.config.ConfigManager;
import org.neverhook.client.ui.GuiCapeSelector;
import org.neverhook.client.ui.components.changelog.ChangeManager;
import org.neverhook.client.ui.components.draggable.DraggableManager;
import org.neverhook.client.ui.newclickgui.ClickGuiScreen;

import java.io.IOException;

public class NeverHook implements Helper {

    public static NeverHook instance = new NeverHook();

    public String name = "NeverHook", type = "Premium", version = "1.4", status = "Release", build = "161021";
    public FeatureManager featureManager = new FeatureManager();
    public org.neverhook.client.ui.clickgui.ClickGuiScreen clickGui;
    public ClickGuiScreen newClickGui;
    public CommandManager commandManager;
    public ConfigManager configManager;
    public MacroManager macroManager;
    public FileManager fileManager;
    public DraggableManager draggableManager;
    public FriendManager friendManager;
    public RotationHelper.Rotation rotation;
    public BlurUtil blurUtil;
    public ChangeManager changeManager;

    public void load() throws IOException {

        SplashProgress.setProgress(1);

        Display.setTitle(name + " " + type + " " + version);

        /*net.aal.protection.Main.xza();
        new LicenseUtil().check();
        net.aal.protection.Main.xza();
        new HwidCheck().check();
        net.aal.protection.Main.xza();
        new VersionCheck().check();
        net.aal.protection.Main.xza();

        net.aal.protection.Main.xza();*/
        new DiscordRPC().init();

        GuiCapeSelector.Selector.setCapeName("neverhookcape3");
        (fileManager = new FileManager()).loadFiles();
        featureManager = new FeatureManager();
        clickGui = new org.neverhook.client.ui.clickgui.ClickGuiScreen();
        newClickGui = new ClickGuiScreen();
        commandManager = new CommandManager();
        configManager = new ConfigManager();
        macroManager = new MacroManager();
        draggableManager = new DraggableManager();
        friendManager = new FriendManager();
        rotation = new RotationHelper.Rotation();
        blurUtil = new BlurUtil();
        changeManager = new ChangeManager();

        BaritoneAPI.getProvider().getPrimaryBaritone();

        try {
            viamcp.ViaMCP.getInstance().start();
        } catch (Exception e) {

        }

        try {
            fileManager.getFile(FriendConfig.class).loadFile();
        } catch (Exception e) {
        }

        try {
            fileManager.getFile(MacroConfig.class).loadFile();
        } catch (Exception e) {

        }

        try {
            fileManager.getFile(HudConfig.class).loadFile();
        } catch (Exception e) {
        }

        try {
            fileManager.getFile(XrayConfig.class).loadFile();
        } catch (Exception e) {
        }

        EventManager.register(rotation);
        EventManager.register(this);
    }

    @EventTarget
    public void shutDown(EventShutdownClient event) {
        EventManager.unregister(this);
        (fileManager = new FileManager()).saveFiles();
        new DiscordRPC().shutdown();
    }

    @EventTarget
    public void onInputKey(EventInputKey event) {
        for (Feature feature : featureManager.getFeatureList()) {
            if (feature.getBind() == event.getKey()) {
                feature.toggle();
            }
        }
        for (Macro macro : macroManager.getMacros()) {
            if (macro.getKey() == Keyboard.getEventKey()) {
                if (mc.player.getHealth() > 0 && mc.player != null) {
                    mc.player.sendChatMessage(macro.getValue());
                }
            }
        }
    }
}
