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

import java.util.Locale;

public class WarpCommand implements CommandExecutor {

    private Main plugin;

    public WarpCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if(args.length == 0 || args[0].equalsIgnoreCase("help")){
            player.sendMessage("§6Liste des commandes warp :\n"+
                                "§f/warp help : Voir la liste des commandes disponibles\n" +
                                "/warp set <name> : Définir un nouveau warp\n" +
                                "/warp list : Voir la liste des warps disponibles\n" +
                                "/warp <name> : Se téléporter à un warp\n" +
                                "/warp delete <name> : Supprimer un warp");
            return true;
        }
        if(args[0].equals("set")) {
            if(args.length == 1) {
                player.sendMessage("§cMissing argument : /warp set <name>");
                return false;
            }
            String name = args[1].toLowerCase();
            if(plugin.getConfig().get("Warps." + name) != null) {
                player.sendMessage("§cCe warp existe déjà");
                return false;
            }
            Location loc = player.getLocation();
            plugin.getConfig().set("Warps." + name + ".World", loc.getWorld().getName());
            plugin.getConfig().set("Warps." + name + ".X", (int) loc.getX());
            plugin.getConfig().set("Warps." + name + ".Y", (int) loc.getY());
            plugin.getConfig().set("Warps." + name + ".Z", (int) loc.getZ());
            plugin.getConfig().set("Warps." + name + ".Yaw", getFacingYaw((int) loc.getYaw()));
            plugin.getConfig().set("Warps." + name + ".Author", player.getName());
            plugin.saveConfig();
            player.sendMessage("§aWarp §e" + name.substring(0,1).toUpperCase() + name.substring(1) + " §acréé avec succes");
            return true;
        }
        if(args[0].equals("delete")) {
            if(args.length == 1) {
                player.sendMessage("§cMissing argument : /warp delete <name>");
                return false;
            }
            String name = args[1].toLowerCase();
            if(plugin.getConfig().get("Warps." + name) == null) {
                player.sendMessage("§cAucun warp avec ce nom");
                return false;
            }
            if(!plugin.getConfig().getString("Warps." + name + ".Author").equals(player.getName())) {
                player.sendMessage("§cVous n'êtes pas le propriétaire de ce warp");
                return false;
            }
            plugin.getConfig().set("Warps." + name,null);
            plugin.saveConfig();
            player.sendMessage("§aWarp §e" + name.substring(0,1).toUpperCase() + name.substring(1) + " supprimé avec succes");
            return true;
        }
        if(args[0].equals("list")) {
            if (plugin.getConfig().getConfigurationSection("Warps").getKeys(false).size() == 0){
                player.sendMessage("§cAucun warps de configuré pour le moment\n");
                return true;
            }
            player.sendMessage("§6Liste des warps disponibles\n");
            StringBuilder s= new StringBuilder();
            for(String name : plugin.getConfig().getConfigurationSection("Warps").getKeys(false)) {
                s.delete(0,s.length());
                s.append("§dDimension ");
                switch (plugin.getConfig().getString("Warps." + name + ".World")) {
                    case "world" -> s.append("§2OverWorld §f| ");
                    case "world_nether" -> s.append("§4Nether    §f| ");
                }
                s.append("§bNom du warp : " + name.substring(0,1).toUpperCase() + name.substring(1) + " §f| " + plugin.getConfig().getString("Warps." + name + ".Author"));
                player.sendMessage(s.toString());
            }
            return true;
        }
        if(plugin.getConfig().get("Warps." + args[0]) == null) {
            player.sendMessage("§cAucun warp avec ce nom");
            return false;
        }
        String name = args[0].toLowerCase();
        double X = plugin.getConfig().getInt("Warps." + name + ".X");
        double Y = plugin.getConfig().getInt("Warps." + name + ".Y");
        double Z = plugin.getConfig().getInt("Warps." + name + ".Z");
        float yaw = (float) plugin.getConfig().getDouble("Warps." + name + ".Yaw");
        String world = plugin.getConfig().getString("Warps." + name + ".World");
        Location loc = new Location(Bukkit.getWorld(world), X, Y, Z, yaw, 0);
        player.teleport(loc);
        player.sendMessage("§aTéléportation au warp §e" + name.substring(0,1).toUpperCase() + name.substring(1));
        return true;
    }

    private float getFacingYaw(int yaw) {
        int sign = yaw/Math.abs(yaw);
        int deg = Math.abs(yaw)/45;
        return Math.abs(yaw)%45 < 22.5 ? deg*45*sign : (deg+1)*45*sign;
    }
}
