package legitish.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import legitish.main.Legitish;
import legitish.module.Module;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigManager {
    private final File configDirectory = new File(Minecraft.getMinecraft().mcDataDir + File.separator + "config" + File.separator + "legitish");
    private final ArrayList<Config> configs = new ArrayList<>();
    private Config config;

    public ConfigManager() {
        if (!configDirectory.isDirectory()) {
            configDirectory.mkdirs();
        }

        discoverConfigs();
        File defaultFile = new File(configDirectory, "default.lcfg");
        this.config = new Config(defaultFile);

        if (!defaultFile.exists()) {
            save();
        }

    }

    public static boolean isOutdated(File file) {
        JsonParser jsonParser = new JsonParser();
        try (FileReader reader = new FileReader(file)) {
            Object obj = jsonParser.parse(reader);
            JsonObject data = (JsonObject) obj;
            return false;
        } catch (JsonSyntaxException | ClassCastException | IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void discoverConfigs() {
        configs.clear();
        if (configDirectory.listFiles() == null || !(Objects.requireNonNull(configDirectory.listFiles()).length > 0))
            return;  // nothing to discover if there are no files in the directory

        for (File file : Objects.requireNonNull(configDirectory.listFiles())) {
            if (file.getName().endsWith(".lcfg")) {
                if (!isOutdated(file)) {
                    configs.add(new Config(
                            new File(file.getPath())
                    ));
                }
            }
        }
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
        JsonObject data = config.getData().get("modules").getAsJsonObject();
        List<Module> knownModules = new ArrayList<>(Legitish.moduleManager.moduleList());
        for (Module module : knownModules) {
            if (data.has(module.getName())) {
                module.applyConfigFromJson(data.get(module.getName()).getAsJsonObject());
            } else {
                module.resetToDefaults();
            }
        }
    }

    public void save() {
        JsonObject data = new JsonObject();
        data.addProperty("title", "");
        data.addProperty("author", "Unknown");
        data.addProperty("notes", "");
        data.addProperty("lastEditTime", System.currentTimeMillis());

        JsonObject modules = new JsonObject();
        for (Module module : Legitish.moduleManager.moduleList()) {
            modules.add(module.getName(), module.getConfigAsJson());
        }
        data.add("modules", modules);

        config.save(data);
    }

    public void loadConfigByName(String replace) {
        discoverConfigs();
        for (Config config : configs) {
            if (config.getName().equals(replace)) {
                setConfig(config);
            }
        }
    }

    public ArrayList<Config> getConfigs() {
        discoverConfigs();
        return configs;
    }

    public void copyConfig(Config config, String s) {
        File file = new File(configDirectory, s);
        Config newConfig = new Config(file);
        newConfig.save(config.getData());
    }

    public void resetConfig() {
        for (Module module : Legitish.moduleManager.moduleList())
            module.resetToDefaults();
        save();
    }

    public void deleteConfig(Config config) {
        config.file.delete();
        if (config.getName().equals(this.config.getName())) {
            discoverConfigs();
            if (this.configs.size() < 2) {
                this.resetConfig();
                File defaultFile = new File(configDirectory, "default.lcfg");
                this.config = new Config(defaultFile);
                save();
            } else {
                this.config = this.configs.get(0);
            }

            this.save();
        }
    }
}
