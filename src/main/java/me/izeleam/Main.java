package me.izeleam;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    WarpGui obj;

    @Override
    public void onEnable() {

         obj = new WarpGui(this);

        getCommand("warp").setExecutor(new WarpCommand(obj));
        getServer().getPluginManager().registerEvents(obj, this);
        loadConfing();
    }

    private void loadConfing(){
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
