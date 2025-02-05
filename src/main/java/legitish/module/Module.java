package legitish.module;

import com.google.gson.JsonObject;
import legitish.module.modules.client.Notifications;
import legitish.module.modulesettings.ModuleDesc;
import legitish.module.modulesettings.ModuleSettingsList;
import legitish.module.modulesettings.ModuleTickSetting;
import net.minecraft.client.Minecraft;
import net.weavemc.loader.api.event.EventBus;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class Module {
    protected static Minecraft mc;
    private final String moduleName;
    private final Module.category moduleCategory;
    protected ArrayList<ModuleSettingsList> settings;
    private boolean isToggled = false;
    private boolean enabled;
    private int keycode;
    protected int defaultKeyCode = keycode;

    public Module(String moduleName, Module.category moduleCategory, int keycode) {
        this.moduleName = moduleName;
        this.moduleCategory = moduleCategory;
        this.keycode = keycode;
        this.enabled = false;
        mc = Minecraft.getMinecraft();
        this.settings = new ArrayList<>();
    }

    public void keybind() {
        if (this.keycode != 0) {
            if (!this.isToggled && Keyboard.isKeyDown(this.keycode)) {
                this.toggle();
                this.isToggled = true;
            } else if (!Keyboard.isKeyDown(this.keycode)) {
                this.isToggled = false;
            }

        }
    }

    public void enable() {
        if (this.moduleCategory() != category.Client) {
            Notifications.sendNotification(Notifications.NotificationTypes.INFO, this.moduleName + " was enabled!", 2000);
        }
        this.setEnabled(true);
        ModuleManager.enabledModuleList.add(this);
        if (ModuleManager.arrayList.isEnabled()) {
            ModuleManager.sort();
        }
        EventBus.subscribe(this);
        this.onEnable();
    }

    public void disable() {
        if (this.moduleCategory() != category.Client) {
            Notifications.sendNotification(Notifications.NotificationTypes.INFO, this.moduleName + " was disabled!", 2000);
        }
        this.setEnabled(false);
        ModuleManager.enabledModuleList.remove(this);
        EventBus.unsubscribe(this);
        this.onDisable();
    }

    public String getName() {
        return this.moduleName;
    }

    public ArrayList<ModuleSettingsList> getSettings() {
        return this.settings;
    }

    public void registerSetting(ModuleSettingsList Setting) {
        this.settings.add(Setting);
    }

    public Module.category moduleCategory() {
        return this.moduleCategory;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void toggle() {
        if (this.isEnabled()) {
            this.disable();
        } else {
            this.enable();
        }

    }

    public void update() {
    }

    public void guiUpdate() {
    }

    public void guiButtonToggled(ModuleTickSetting b) {
    }

    public int getKeycode() {
        return this.keycode;
    }

    public void setbind(int keybind) {
        this.keycode = keybind;
    }

    public JsonObject getConfigAsJson() {
        JsonObject settings = new JsonObject();

        for (ModuleSettingsList setting : this.settings) {
            if (!(setting instanceof ModuleDesc) && setting != null) {
                JsonObject settingData = setting.getConfigAsJson();
                settings.add(setting.n, settingData);
            }
        }
        JsonObject data = new JsonObject();
        data.addProperty("keycode", keycode);
        data.add("settings", settings);

        return data;
    }

    public void applyConfigFromJson(JsonObject data) {
        try {
            this.keycode = data.get("keycode").getAsInt();
            JsonObject settingsData = data.get("settings").getAsJsonObject();
            for (ModuleSettingsList setting : this.settings) {
                if (settingsData.has(setting.get())) {
                    setting.applyConfigFromJson(settingsData.get(setting.get()).getAsJsonObject());
                }
            }
        } catch (NullPointerException ignored) {

        }
    }

    public enum category {
        Combat,
        Minigames,
        Movement,
        Player,
        Visual,
        Client
    }
}
