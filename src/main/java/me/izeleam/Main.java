package me.izeleam;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    @Override
    public void onEnable() {

        getCommand("warp").setExecutor(new WarpCommand(this));
        getServer().getPluginManager().registerEvents(new WarpGui(this), this);
        loadConfing();
    }

    private void loadConfing(){
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
