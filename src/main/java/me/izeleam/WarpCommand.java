package me.izeleam;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Locale;

public class WarpCommand implements CommandExecutor {

    private WarpGui obj;

    public WarpCommand(WarpGui obj) {
        this.obj = obj;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        ((Player) sender).getPlayer().openInventory(obj.warpHomeGui(((Player) sender).getPlayer()));
        return true;
    }
}