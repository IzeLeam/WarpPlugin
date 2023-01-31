package me.izeleam;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WarpGui implements Listener {

    Main plugin;

    HashMap<Player, String> waitForChat;

    public WarpGui(Main plugin) {
        this.plugin = plugin;
        this.waitForChat = new HashMap<>();
    }

    @EventHandler
    private void onInventoryEvents(InventoryClickEvent event) {

        String currentGui = event.getView().getTitle();
        Inventory inv = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();

        if (currentGui.equals("Warp Home")) {
            if (!checkNotNullEvent(event)) return;
            if (event.getSlot() == 11) {
                event.setCancelled(true);
                player.openInventory(overworldWarpsGui(player, false, 1));
                return;
            } else if (event.getSlot() == 15) {
                event.setCancelled(true);
                player.openInventory(netherWarpsGui(player, false, 1));
                return;
            } else if (event.getSlot() == 27) {
                event.setCancelled(true);
                player.openInventory(myWarpsGui(player));
                return;
            } else if (event.getSlot() == 31) {
                event.setCancelled(true);
                player.closeInventory();
                return;
            } else if (event.getSlot() == 35) {
                event.setCancelled(true);
                String warp = createWarp(player);
                if (warp != null) player.openInventory(editWarpGui(warp, player));
                return;
            } else {
                event.setCancelled(true);
                return;
            }
        }
        if (currentGui.equals("Mes Warps")) {
            if (!checkNotNullEvent(event)) return;
            if (event.getSlot() == 32) {
                event.setCancelled(true);
                player.openInventory(warpHomeGui(player));
                return;
            } else if (event.getCurrentItem().getType().equals(Material.GRASS_BLOCK) || event.getCurrentItem().getType().equals(Material.NETHERRACK)) {
                event.setCancelled(true);
                String warp = createWarp(player);
                if (warp != null) player.openInventory(editWarpGui(warp, player));
                return;
            } else if (event.getCurrentItem().getType().equals(Material.RED_CONCRETE)) {
                HashMap<Integer, Integer> slots = new HashMap<>();
                slots.put(10, 1);
                slots.put(13, 2);
                slots.put(16, 3);
                event.setCancelled(true);
                if (!unlockNewWarp(player, slots.get(event.getSlot()))) {
                    player.closeInventory();
                    return;
                }
                player.openInventory(myWarpsGui(player));
                return;
            } else if (event.getCurrentItem().getType().equals(Material.PAPER)) {
                if (event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                    event.setCancelled(true);
                    player.closeInventory();
                    teleportPlayer(player, event.getCurrentItem().getItemMeta().getDisplayName().substring(2).toLowerCase());
                    return;
                } else if (event.getAction().equals(InventoryAction.PICKUP_HALF)) {
                    event.setCancelled(true);
                    player.openInventory(editWarpGui(event.getCurrentItem().getItemMeta().getDisplayName().substring(2).toLowerCase(), player));
                    return;
                }
            } else {
                event.setCancelled(true);
                return;
            }
        }
        if (currentGui.contains("Overworld Warps") || currentGui.contains("Nether Warps")) {
            if (!checkNotNullEvent(event)) return;
            boolean filter = event.getClickedInventory().getItem(39).getItemMeta().getLore().contains("§a| Activé");
            if(event.getSlot() == 26) {
                event.setCancelled(true);
                if (currentGui.contains("Overworld Warps")) player.openInventory(overworldWarpsGui(player, filter, Integer.parseInt(currentGui.split(" ")[2])+1));
                else player.openInventory(netherWarpsGui(player, filter, Integer.parseInt(currentGui.split(" ")[2])+1));
                return;
            }
            else if (event.getSlot() == 36) {
                event.setCancelled(true);
                if (currentGui.contains("Overworld Warps")) player.openInventory(overworldWarpsGui(player, filter, 1));
                else player.openInventory(netherWarpsGui(player, filter, 1));
                return;
            } else if (event.getSlot() == 39) {
                event.setCancelled(true);
                if (currentGui.contains("Overworld Warps")) player.openInventory(overworldWarpsGui(player, !filter, 1));
                else player.openInventory(netherWarpsGui(player, !filter, 1));
                return;
            } else if (event.getSlot() == 41) {
                event.setCancelled(true);
                player.openInventory(warpHomeGui(player));
                return;
            } else if (event.getCurrentItem().getType().equals(Material.PAPER)) {
                event.setCancelled(true);
                if (event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                    player.closeInventory();
                    teleportPlayer(player, event.getCurrentItem().getItemMeta().getDisplayName().substring(2).toLowerCase());
                    return;
                } else if (event.getAction().equals(InventoryAction.PICKUP_HALF)) {
                    player.openInventory(editWarpGui(event.getCurrentItem().getItemMeta().getDisplayName().substring(2).toLowerCase(), player));
                    return;
                }
            } else {
                event.setCancelled(true);
                return;
            }
        }
        if (currentGui.contains("Edit Warp")) {
            if (!checkNotNullEvent(event)) return;
            if (event.getSlot() == 10) {
                event.setCancelled(true);
                player.closeInventory();
                player.sendMessage("§eSaisissez un nom §c(cancel pour annuler) §e(max 25 caract.) §e:");
                waitForChat.put(player, currentGui.split(" ")[2]);
            } else if (event.getSlot() == 14) {
                event.setCancelled(true);
                changeFacing(currentGui.split(" ")[2]);
                event.getClickedInventory().setItem(14, getFacingEditDisplay(currentGui.split(" ")[2]));
            } else if (event.getSlot() == 16) {
                event.setCancelled(true);
                changeStatus(currentGui.split(" ")[2]);
                event.getClickedInventory().setItem(16, getStatusDisplay(currentGui.split(" ")[2]));
            } else if (event.getSlot() == 32) {
                event.setCancelled(true);
                player.openInventory(warpHomeGui(player));
            } else if (event.getSlot() == 35 && event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                event.setCancelled(true);
                deleteWarp(currentGui.split(" ")[2], player);
                player.openInventory(warpHomeGui(player));
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.getConfig().getString("Players." + player.getName()) != null) return;
        plugin.getConfig().set("Players." + player.getName() + ".maxWarp", 3);
        plugin.getConfig().set("Players." + player.getName() + ".warps", 0);
        plugin.saveConfig();
    }

    @EventHandler
    private void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (waitForChat.containsKey(player)) {
            event.setCancelled(true);
            if (event.getMessage().equals("cancel")) {
                waitForChat.remove(player);
                return;
            }

            String newWarp = formatWarpName(event.getMessage());

            if (plugin.getConfig().getString("Warps." + newWarp) != null) {
                player.sendMessage("§cCe nom n'est pas disponible");
                waitForChat.remove(player);
                return;
            }

            plugin.getConfig().set("Warps." + newWarp + ".World", plugin.getConfig().getString("Warps." + waitForChat.get(player) + ".World"));
            plugin.getConfig().set("Warps." + newWarp + ".X", plugin.getConfig().getInt("Warps." + waitForChat.get(player) + ".X"));
            plugin.getConfig().set("Warps." + newWarp + ".Y", plugin.getConfig().getInt("Warps." + waitForChat.get(player) + ".Y"));
            plugin.getConfig().set("Warps." + newWarp + ".Z", plugin.getConfig().getInt("Warps." + waitForChat.get(player) + ".Z"));
            plugin.getConfig().set("Warps." + newWarp + ".Facing", plugin.getConfig().getString("Warps." + waitForChat.get(player) + ".Facing"));
            plugin.getConfig().set("Warps." + newWarp + ".Status", plugin.getConfig().getBoolean("Warps." + waitForChat.get(player) + ".Status"));
            plugin.getConfig().set("Warps." + newWarp + ".Author", plugin.getConfig().getString("Warps." + waitForChat.get(player) + ".Author"));

            plugin.getConfig().set("Warps." + waitForChat.get(player), null);
            plugin.saveConfig();
            waitForChat.remove(player);
            player.sendMessage("§aNom modifié avec succes");
        }
    }

    @EventHandler
    private void onEntityKilled(EntityDeathEvent event) {
        if (event.getEntityType().equals(EntityType.WARDEN)) {
            ItemStack item = new ItemStack(Material.ECHO_SHARD, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§fWarden's horn");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), item);
        }
    }

    private String formatWarpName(String m) {
        String result = m.replace(" ", "").toLowerCase();
        return result.length() <= 25 ? result : result.substring(0, 25);
    }

    private boolean checkNotNullEvent(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.GRAY_STAINED_GLASS_PANE)) {
            event.setCancelled(true);
            return false;
        }
        if (event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
            event.setCancelled(true);
            return false;
        }
        return true;
    }

    private boolean unlockNewWarp(Player player, int slot) {
        int maxWarp = plugin.getConfig().getInt("Players." + player.getName() + ".maxWarp");
        if (slot > maxWarp + 1) {
            player.sendMessage("§cVous devez d'abord débloquer le warp précédent");
            return false;
        }
        List<Material> toRemove = new ArrayList<>();
        switch (slot) {
            case 1 -> toRemove.add(Material.DRAGON_EGG);
            case 2 -> toRemove.add(Material.NETHER_STAR);
            case 3 -> toRemove.add(Material.ECHO_SHARD);
        }
        if (removeItem(player, toRemove.get(0))) {
            plugin.getConfig().set("Players." + player.getName() + ".maxWarp", maxWarp + 1);
            plugin.saveConfig();
            player.sendMessage("§aWarp débloqué avec succes");
            return true;
        }
        player.sendMessage("§cVous n'avez pas les matériaux requis");
        return false;
    }

    private boolean removeItem(Player player, Material mat) {
        ItemStack item;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            item = player.getInventory().getItem(i);
            if (item == null) continue;
            if (item.getType().equals(mat)) {
                if (mat.equals(Material.ECHO_SHARD) && !item.getItemMeta().isUnbreakable()) continue;
                player.getInventory().setItem(i, new ItemStack(mat, player.getInventory().getItem(i).getAmount() - 1));
                return true;
            }
        }
        return false;
    }

    private String createWarp(Player player) {
        if (plugin.getConfig().getInt("Players." + player.getName() + ".warps") >= plugin.getConfig().getInt("Players." + player.getName() + ".maxWarp")) {
            player.closeInventory();
            player.sendMessage("§cVous ne pouvez plus créer de warp");
            return null;
        }
        String name;
        do {
            name = player.getName().toLowerCase() + (int) (Math.random() * 10000);
        } while (plugin.getConfig().get("Warps." + name) != null);

        Location loc = player.getLocation();
        plugin.getConfig().set("Warps." + name + ".World", loc.getWorld().getName());
        plugin.getConfig().set("Warps." + name + ".X", (int) loc.getX());
        plugin.getConfig().set("Warps." + name + ".Y", (int) loc.getY());
        plugin.getConfig().set("Warps." + name + ".Z", (int) loc.getZ());
        plugin.getConfig().set("Warps." + name + ".Facing", player.getFacing().name().toLowerCase());
        plugin.getConfig().set("Warps." + name + ".Status", false);
        plugin.getConfig().set("Warps." + name + ".Author", player.getName());
        plugin.getConfig().set("Players." + player.getName() + ".warps", plugin.getConfig().getInt("Players." + player.getName() + ".warps") + 1);
        plugin.saveConfig();
        player.sendMessage("§aWarp créé avec succès");
        return name;
    }

    private void deleteWarp(String warp, Player player) {
        plugin.getConfig().set("Warps." + warp, null);
        plugin.getConfig().set("Players." + player.getName() + ".warps", plugin.getConfig().getInt("Players." + player.getName() + ".warps") + -1);
        plugin.saveConfig();
        player.sendMessage("§aWarp supprimé avec succes");
    }

    private void changeStatus(String warp) {
        if (plugin.getConfig().getBoolean("Warps." + warp + ".Status")) {
            plugin.getConfig().set("Warps." + warp + ".Status", false);
        } else {
            plugin.getConfig().set("Warps." + warp + ".Status", true);
        }
        plugin.saveConfig();
    }

    private void changeFacing(String warp) {
        BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
        int i = 0;
        for (BlockFace face : faces) {
            if (face.name().toLowerCase().equals(plugin.getConfig().getString("Warps." + warp + ".Facing"))) {
                break;
            }
            i++;
        }
        plugin.getConfig().set("Warps." + warp + ".Facing", faces[i == 3 ? 0 : i + 1].name().toLowerCase());
        plugin.saveConfig();
    }

    private void teleportPlayer(Player player, String warp) {
        if (!player.getWorld().getName().equals(plugin.getConfig().getString("Warps." + warp + ".World"))) {
            player.sendMessage("§cVous devez être dans la dimension de destination");
            return;
        }
        if (!plugin.getConfig().getBoolean("Warps." + warp + ".Status")) {
            if (!plugin.getConfig().getString("Warps." + warp + ".Author").equals(player.getName())) {
                player.sendMessage("§cCe warp est fermé par son propriétaire");
                return;
            }
        }
        int facing = 0;
        switch (plugin.getConfig().getString("Warps." + warp + ".Facing")) {
            case "north" -> facing = -180;
            case "east" -> facing = -90;
            case "south" -> facing = 0;
            case "west" -> facing = 90;
        }
        Location dest = new Location(Bukkit.getWorld(plugin.getConfig().getString("Warps." + warp + ".World")),
                plugin.getConfig().getDouble("Warps." + warp + ".X"),
                plugin.getConfig().getDouble("Warps." + warp + ".Y"),
                plugin.getConfig().getDouble("Warps." + warp + ".Z"),
                facing, 0);
        player.sendMessage("§aTéléportation en cours...");
        player.teleport(dest);
    }

    public Inventory warpHomeGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 36, "Warp Home");
        setGuiVoid(gui);

        gui.setItem(11, getListWarpInfo(player, "world"));
        gui.setItem(15, getListWarpInfo(player, "world_nether"));
        gui.setItem(27, getMyWarpsDisplay(player));
        gui.setItem(31, getInfoItem());
        gui.setItem(35, getNewWarpButton(player));
        return gui;
    }

    private Inventory myWarpsGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 36, "Mes Warps");
        List<String> warps = getPlayerWarps(player);
        int maxWarp = plugin.getConfig().getInt("Players." + player.getName() + ".maxWarp");
        int[] places = {10, 13, 16};
        int i = 0;
        while (i < maxWarp) {
            if (warps.size() != 0) {
                gui.setItem(places[i], getWarpDisplay(warps.get(0), player));
                warps.remove(0);
            } else gui.setItem(places[i], getNewWarpButton(player));
            i++;
        }
        while (i < 3) {
            gui.setItem(places[i], getBloquedWarp(i, player));
            i++;
        }
        gui.setItem(31, getMyWarpsDisplay(player));
        gui.setItem(32, getBackButton());

        return gui;
    }

    private Inventory overworldWarpsGui(Player player, Boolean filter, int page) {
        List<String> warps = getWarpsList("world", filter, player);
        int maxPages = warps.size()/21 + (warps.size()%21 > 0 ? 1 : 0);
        if(page > maxPages) page = 1;
        getPageWarpsList(warps, page);
        Inventory gui = Bukkit.createInventory(null, 45, "Overworld Warps " + page);
        setGuiVoid(gui);
        int[] places = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        int i = 0;
        for (String warp : warps) {
            gui.setItem(places[i], getWarpDisplay(warp, player));
            i++;
        }
        gui.setItem(26, getWarpsPage(page, (maxPages == 0 ? 1 : 0)));
        gui.setItem(36, getActualizeDisplay());
        gui.setItem(39, getFilterDisplay(filter));
        gui.setItem(40, getListWarpInfo(player, "world"));
        gui.setItem(41, getBackButton());

        return gui;
    }

    private Inventory netherWarpsGui(Player player, Boolean filter, int page) {
        List<String> warps = getWarpsList("world_nether", filter, player);
        int maxPages = warps.size()/21 + (warps.size()%21 > 0 ? 1 : 0);
        if(page > maxPages) page = 1;
        getPageWarpsList(warps, page);
        Inventory gui = Bukkit.createInventory(null, 45, "Nether Warps " + page);
        setGuiVoid(gui);
        int[] places = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        int i = 0;
        for (String warp : warps) {
            gui.setItem(places[i], getWarpDisplay(warp, player));
            i++;
        }
        gui.setItem(26, getWarpsPage(page, maxPages));
        gui.setItem(36, getActualizeDisplay());
        gui.setItem(39, getFilterDisplay(filter));
        gui.setItem(40, getListWarpInfo(player, "world_nether"));
        gui.setItem(41, getBackButton());

        return gui;
    }

    private Inventory editWarpGui(String warp, Player player) {
        if (!plugin.getConfig().getString("Warps." + warp + ".Author").equals(player.getName())) {
            player.sendMessage("§cVous ne pouvez pas modifier un warp qui ne vous appartient pas");
            return warpHomeGui(player);
        }
        if (!plugin.getConfig().getString("Warps." + warp + ".World").equals(player.getWorld().getName())) {
            player.sendMessage("§cVous devez être dans la même dimension que le warp");
            return warpHomeGui(player);
        }
        Inventory gui = Bukkit.createInventory(null, 36, "Edit Warp " + warp);
        setGuiVoid(gui);
        gui.setItem(10, getNameEditDisplay(warp));
        gui.setItem(12, getLocationEditDisplay(warp));
        gui.setItem(14, getFacingEditDisplay(warp));
        gui.setItem(16, getStatusDisplay(warp));
        gui.setItem(31, getEditWarpInfo(warp));
        gui.setItem(32, getBackButton());
        gui.setItem(35, getDeleteWarpButton());

        return gui;
    }

    private void setGuiVoid(Inventory gui){
        ItemStack ivoid = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta imeta = ivoid.getItemMeta();
        imeta.setDisplayName("§r");
        ivoid.setItemMeta(imeta);

        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, ivoid);
        }
    }

    private ItemStack getListWarpInfo(Player player, String world) {
        ItemStack item = new ItemStack(world.equals("world") ? Material.GRASS_BLOCK : Material.NETHERRACK, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bWarps " + (world.equals("world") ? "de §al'overworld" : "du §4nether"));
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Vous trouvez ici la liste des warps");
        lore.add("§7présents dans cette dimension");
        lore.add("");
        lore.add("§7Vous pouvez vous téléportez à l'un d'entre");
        lore.add("§7à condition d'être vous aussi dans");
        lore.add("§7cette même dimension");
        lore.add("");
        lore.add("§b| Votre dimension actuelle : " + (player.getWorld().getName().equals("world") ? "§aOverWorld" : "§4Nether"));
        lore.add("");
        lore.add("§7Ajoutez, éditez ou supprimer vos warps");
        lore.add("§7tant que vous êtes dans la même dimension");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getWarpDisplay(String warp, Player player) {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§d" + warp.substring(0, 1).toUpperCase() + warp.substring(1));
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§f| §bX §f: §e" + plugin.getConfig().getInt("Warps." + warp + ".X") + " §f| §bY §f: §e" + plugin.getConfig().getInt("Warps." + warp + ".Y") + " §f| §bZ §f: §e" + plugin.getConfig().getInt("Warps." + warp + ".Z"));
        lore.add("§f| §bFacing : §e" + plugin.getConfig().getString("Warps." + warp + ".Facing").substring(0, 1).toUpperCase() + plugin.getConfig().getString("Warps." + warp + ".Facing").substring(1));
        lore.add("");
        lore.add("§6Propriétaire : §e" + plugin.getConfig().getString("Warps." + warp + ".Author"));
        lore.add("§bStatut : " + ((plugin.getConfig().getBoolean("Warps." + warp + ".Status") ? "§aOuvert" : "§cFermé")));
        if (plugin.getConfig().getString("Warps." + warp + ".World").equals(player.getWorld().getName())) {
            if (plugin.getConfig().getString("Warps." + warp + ".Author").equals(player.getName())) {
                lore.add("");
                lore.add("§8Clic gauche pour se téléporter");
                lore.add("§8Clic gauche pour éditer le warp");
            } else if (plugin.getConfig().getBoolean("Warps." + warp + ".Status")) {
                lore.add("");
                lore.add("§8Clic gauche pour vous téléporter");
            }
        } else {
            lore.add("");
            lore.add("§cVous n'êtes pas dans la bonne dimension");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getMyWarpsDisplay(Player player) {
        ItemStack item = new ItemStack(Material.ENDER_CHEST, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bMes warps");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Visualisez vos warps et débloquez les");
        lore.add("§7en sacrifiant vos loots de boss");
        lore.add("");
        lore.add("§8Clic gauche pour ouvrir");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getFilterDisplay(Boolean filter) {
        ItemStack item = new ItemStack(Material.COMPARATOR, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§aFiltre warps ouverts");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Activé ce filtre nous n'afficher");
        lore.add("§7uniquement les warps qui vous");
        lore.add("§7sont ouverts");
        lore.add("");
        if (filter) {
            lore.add("§a| Activé");
            lore.add("§8| Désactivé");
        } else {
            lore.add("§8| Activé");
            lore.add("§c| Désactivé");
        }
        lore.add("");
        String tmp;
        lore.add("§8Clic gauche pour " + (filter ? "désactiver" : "activer") + " le filtre");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getWarpsPage(int page, int pages) {
        ItemStack item = new ItemStack(Material.ARROW, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bPage");
        List<String> lore = new ArrayList<>();
        lore.add("");
        StringBuilder str = new StringBuilder();
        str.append("§b|");
        for(int i = 1; i <= pages; i++) {
            if(i == page) str.append(" §e");
            else str.append(" §8");
            str.append(i);
        }
        lore.add(str.toString());
        lore.add("");
        lore.add("§8Clic gauche pour changer de page");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getNewWarpButton(Player player) {
        ItemStack item = null;
        String world = player.getWorld().getName();
        StringBuilder str = new StringBuilder();
        if (world.equals("world")) {
            item = new ItemStack(Material.GRASS_BLOCK, 1);
            str.append("§a");
        } else if (world.equals("world_nether")) {
            item = new ItemStack(Material.NETHERRACK, 1);
            str.append("§4");
        }
        str.append("Créer un warp");
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(str.toString());

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Créez un nouveau warp sur");
        lore.add("§7votre position actuelle");
        lore.add("");
        lore.add("§f| §bX §f: §e" + (int) player.getLocation().getX() + " §f| §bY §f: §e" + (int) player.getLocation().getY() + " §f| §bZ §f: §e" + (int) player.getLocation().getZ());
        lore.add("§f| §bFacing : §e" + player.getFacing().name().charAt(0) + player.getFacing().name().substring(1).toLowerCase());
        lore.add("§f| §bMonde actuel : §e" + (player.getWorld().getName().equals("world") ? "§aOverWorld" : "§4Nether"));
        lore.add("");
        lore.add("§8Clic gauche pour créer un warp");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getActualizeDisplay() {
        ItemStack item = new ItemStack(Material.GREEN_CONCRETE, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§aActualiser");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§8Clic gauche");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getEditWarpInfo(String warp) {
        ItemStack item = new ItemStack(plugin.getConfig().getString("Warps." + warp + ".World").equals("world") ? Material.GRASS_BLOCK : Material.NETHERRACK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bEdition du warp");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Modifiez le nom, la direction et");
        lore.add("§7son statut d'ouverture au public");
        lore.add("");
        lore.add("§7La dimension du warp ainsi que ses");
        lore.add("§7coordonnées ne peuvent pas être modifiés");
        lore.add("");
        lore.add("§7La sauvegarde est automatique");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getNameEditDisplay(String warp) {
        ItemStack item = new ItemStack(Material.NAME_TAG, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bNom");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§f| §dNom actuel : §e" + warp);
        lore.add("");
        lore.add("§8Clic gauche pour modifier le nom");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getLocationEditDisplay(String warp) {
        ItemStack item = new ItemStack(Material.COMPASS, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bLocalisation");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(plugin.getConfig().getString("Warps." + warp + ".World").equals("world") ? "§a| Overworld" : "§8| Overworld");
        lore.add(plugin.getConfig().getString("Warps." + warp + ".World").equals("world") ? "§8| Nether" : "§4| Nether");
        lore.add("");
        lore.add("§f| §bX §f: §e" + plugin.getConfig().getInt("Warps." + warp + ".X") + " §f| §bY §f: §e" + plugin.getConfig().getInt("Warps." + warp + ".Y") + " §f| §bZ §f: §e" + plugin.getConfig().getInt("Warps." + warp + ".Z"));
        lore.add("");
        lore.add("§8Impossible de modifier ces informations");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getFacingEditDisplay(String warp) {
        ItemStack item = new ItemStack(Material.ARROW, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bFacing");
        List<String> lore = new ArrayList<>();
        lore.add("");
        StringBuilder str = new StringBuilder();
        BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
        for (BlockFace face : faces) {
            str.delete(0, str.length());
            if (plugin.getConfig().getString("Warps." + warp + ".Facing").equals(face.toString().toLowerCase())) {
                str.append("§f| §b");
            } else str.append("§8| ");
            str.append(face.toString().substring(0, 1).toUpperCase() + face.toString().substring(1).toLowerCase());
            lore.add(str.toString());
        }
        lore.add("");
        lore.add("§8Clic gauche pour modifier");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getStatusDisplay(String warp) {
        ItemStack item = new ItemStack(Material.OAK_DOOR, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§eStatut");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Si le warp est fermé, vous seul");
        lore.add("§7pourrez vous y téléporter");
        lore.add("");
        lore.add(plugin.getConfig().getBoolean("Warps." + warp + ".Status") ? "§a| Ouvert" : "§8| Ouvert");
        lore.add(plugin.getConfig().getBoolean("Warps." + warp + ".Status") ? "§8| Fermé" : "§c| Fermé");
        lore.add("");
        lore.add("§8Clic gauche pour " + (plugin.getConfig().getBoolean("Warps." + warp + ".Status") ? "fermer" : "ouvrir") + " le warp");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getBackButton() {
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

    private ItemStack getDeleteWarpButton() {
        ItemStack item = new ItemStack(Material.LAVA_BUCKET, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cSupprimer");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Cette action sera sans retour possible");
        lore.add("");
        lore.add("§cShift + Clic gauche pour supprimer ce warp");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getBloquedWarp(int n, Player player) {
        int maxWarp = plugin.getConfig().getInt("Players." + player.getName() + ".maxWarp");
        ItemStack item = new ItemStack(Material.RED_CONCRETE, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cNon débloqué");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§eCout du warp");
        lore.add("");
        String[] tmp = {"§3| 1 Oeuf de dragon", "§f| 1 Nether Star", "§b| 1 Warden's horn"};
        lore.add(tmp[n]);
        lore.add("");
        if (maxWarp < n) lore.add("§cNécessite de débloquer le warp précédent");
        else lore.add("§8Clic gauche pour débloquer");
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
        lore.add("§7pouvez créer contre des loots de boss");
        lore.add("");
        lore.add("§8Clic gauche pour fermer");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private List<String> getPlayerWarps(Player player) {
        List<String> warps = new ArrayList<>();
        for (String name : plugin.getConfig().getConfigurationSection("Warps").getKeys(false)) {
            if (plugin.getConfig().getString("Warps." + name + ".Author").equals(player.getName())) {
                warps.add(name);
            }
        }
        return warps;
    }

    private List<String> getWarpsList(String world, Boolean filter, Player player) {
        List<String> warps = new ArrayList<>();
        for (String name : plugin.getConfig().getConfigurationSection("Warps").getKeys(false)) {
            if (!plugin.getConfig().getString("Warps." + name + ".World").equals(world)) continue;
            if (!filter || (filter && plugin.getConfig().getBoolean("Warps." + name + ".Status")) || plugin.getConfig().getString("Warps." + name + ".Author").equals(player.getName())) {
                warps.add(name);
            }
        }
        return warps;
    }

    private void getPageWarpsList(List<String> warps, int page) {
        int start = (page-1)*21;
        for(int i = 0; i < start; i++){
            warps.remove(0);
        }
        while(warps.size() > 21) warps.remove(21);
    }

}
