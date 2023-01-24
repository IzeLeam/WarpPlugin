package me.izeleam;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public class WarpGui implements Listener{

    Main plugin;

    public WarpGui(Main plugin) {
        this.plugin = plugin;
    }

    private void onInventoryEvents(InventoryClickEvent event) {
        InventoryView view = event.getView();
        Inventory inv = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();

        //ca pas sur (this.plugin.equals("WarpPlugin") ???
        if(this.plugin.getName().equals("WarpPlugin")) {

            if (event.getCurrentItem().getType().equals(Material.GRAY_STAINED_GLASS_PANE) || event.getCurrentItem() == null) {
                event.setCancelled(true);
                return;
            }
            if (inv.getType().equals(InventoryType.PLAYER)) {
                event.setCancelled(true);
                return;
            }

            if (view.equals("Warp Home")) {
                if (event.getCurrentItem().equals(Material.GRASS)) {
                    player.openInventory(overworldWarpsGui(player));
                    event.setCancelled(true);
                    return;
                }
                if (event.getCurrentItem().equals(Material.NETHERRACK)) {
                    player.openInventory(netherWarpsGui(player));
                    event.setCancelled(true);
                    return;
                }
            }
            if(view.equals("Overworld Warps")) {
                //creer warp
                //clic droit edit si proprio
                //clic gauche tp
                //page ?
            }
            if(view.equals("Nether Warps")) {

            }
            if(view.equals("New Warp")) {

            }
            if(view.equals("Edit Warp")) {

            }
            if(view.equals("Confirmation")) {

            }
        }
    }

    private Inventory warpHomeGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45, "Warp Home");
        setGuiVoid(gui);

        gui.setItem(14, getOverWorldButtonWarps());
        gui.setItem(17, getNetherButtonWarps());
        gui.setItem(41, getInfoItem());
        return gui;
    }

    private Inventory overworldWarpsGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45, "Overworld warps");
        setGuiVoid(gui);
        int [] places = {11, 12, 13, 14, 15, 16, 17, 20, 21, 22, 23, 24, 25 ,26, 29, 30, 31, 32, 33, 34, 35};
        int i = 0;
        for(String warps : getWarpsList("world")) {
            gui.setItem(places[i], getWarpDisplay(warps, player));
        }
        gui.setItem(41, getNewWarpButton(player, "world"));

        //bouton ameliorer jusqu'a 2 warps

        return gui;
    }

    private Inventory netherWarpsGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45, "Nether warps");
        setGuiVoid(gui);
        for(String warps : getWarpsList("world_nether")) {
            //lister sur les bons emplacements
        }

        //bouton ameliorer jusqu'a 2 warps => oeil de l'end
        //clic gauche => tp si dans le bon monde
        //clic droit => edit si propriétaire du warp

        return gui;
    }

    private Inventory newWarpGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45, "New warp");
        setGuiVoid(gui);

        Location loc = player.getLocation();
        //Bouton sauvegarder
        //annuler
        //nom
        //Location indiquée (loc du joueur au moment)
        //couleur ?
        //Description
        //Facing indiqué
        //auteur indiqué
        return gui;
    }

    private Inventory editWarpGui(Player player){
        //Seulement si propriétaire du warp
        Inventory gui = Bukkit.createInventory(null, 45, "Edit Warp");
        setGuiVoid(gui);

        //delete => confirmation ? => non : return editGui => oui : menu gui ou precedent si liste faisable(overworld ou nether)


        return gui;
    }

    private Inventory confirmGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45, "Confirmation");
        setGuiVoid(gui);



        return gui;
    }

    private void setWarpsOnGui(Inventory gui, String world) {

    }

    private void setGuiVoid(Inventory gui){
        ItemStack ivoid = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta imeta = ivoid.getItemMeta();
        imeta.setDisplayName("§r");
        ivoid.setItemMeta(imeta);

        for(int i = 0; i < gui.getSize(); i++){
            gui.setItem(i, ivoid);
        }
    }

    private ItemStack getWarpDisplay(String warp, Player player) {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(warp);
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("X : " + plugin.getConfig().getInt("Warps." + warp + ".X") + " | Y = " + plugin.getConfig().getInt("Warps." + warp + ".Y") + " | Z = " + plugin.getConfig().getInt("Warps." + warp + ".Z"));
        lore.add("Facing : ");
        lore.add("");
        lore.add("Propriétaire : " + plugin.getConfig().getString("Warps." + warp + ".author"));
        lore.add("");
        lore.add("§8Clic gauche pour se téléporter");
        if(Bukkit.getWorld(plugin.getConfig().getString("Warps." + warp + ".World")).equals(player.getWorld())) {
            if(plugin.getConfig().getString("Warps." + warp + ".author").equals(player.getName())) {
                lore.add("§8Clic droit pour modifier votre warp");
            }
        }
        else {
            lore.add("");
            lore.add("§cVous n'êtes pas dans la bonne dimension");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getNewWarpButton(Player player, String world) {
        ItemStack item = null;
        StringBuilder str = null;
        if(world.equals("world")) {
            item = new ItemStack(Material.GRASS, 1);
            str.append("§a");
        }
        else if(world.equals("world_nether")) {
            item = new ItemStack(Material.NETHERRACK, 1);
            str.append("§4");
        }
        str.append("Créer un warp");
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(str.toString());

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Créez un nouveau warp ici :");
        lore.add("X : " + player.getLocation().getX() + " | Y : " + player.getLocation().getY() + " | Z : " + player.getLocation().getZ());
        lore.add("Facing : ");
        lore.add("Monde actuel : " + player.getWorld().getName());
        lore.add("");
        lore.add("§8Clic gauche pour créer un warp");
        if(!player.getWorld().getName().equals(world)) {
            lore.add("");
            lore.add("§cVous n'êtes pas dans la bonne dimension");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getBackButton(){
        ItemStack item = new ItemStack(Material.STONE_BUTTON, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§4Retour");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§8Clic gauche pour revenir");
        lore.add("§8à la page précédente");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getInfoItem() {
        ItemStack item = new ItemStack(Material.END_PORTAL_FRAME, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§dWarp");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Ajoutez un warp pour vous y");
        lore.add("§7téléporter à n'importe quel moment");
        lore.add("");
        lore.add("§7Votre position et le monde dans");
        lore.add("§7lequel vous êtes définissent le warp");
        lore.add("§7que vous créez");
        lore.add("");
        lore.add("§7Augmentez le nombre de warps que vous");
        lore.add("§7pouvez créer grâce aux améliorations");
        lore.add("§7disponibles dans les 2 dimensions");
        lore.add("");
        lore.add("§8Clic gauche pour fermer");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getNetherButtonWarps(){
        ItemStack but = new ItemStack(Material.NETHERRACK);
        ItemMeta meta = but.getItemMeta();
        meta.setDisplayName("§4Dimension Nether");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Warps du Nether");
        lore.add("");
        lore.add("§8Clic gauche pour ouvrir");
        meta.setLore(lore);
        but.setItemMeta(meta);
        return but;
    }

    private ItemStack getOverWorldButtonWarps(){
        ItemStack but = new ItemStack(Material.DIRT);
        ItemMeta meta = but.getItemMeta();
        meta.setDisplayName("§4Dimension OverWorld");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Warps de l'OverWorld");
        lore.add("");
        lore.add("§8Clic gauche pour ouvrir");
        meta.setLore(lore);
        but.setItemMeta(meta);
        return but;
    }

    private List<String> getWarpsList(String world){
        List<String> warps = new ArrayList<>();
        for(String name : plugin.getConfig().getConfigurationSection("Warps").getKeys(false)) {
            if(plugin.getConfig().getString("Warps." + name + ".World").equals(world)){
                warps.add(name);
            }
        }
        return warps;
    }

}
