package me.sat7.dynamicshop.events;

import me.sat7.dynamicshop.DynamicShop;
import me.sat7.dynamicshop.DynaShopAPI;
import me.sat7.dynamicshop.guis.StartPage;
import me.sat7.dynamicshop.utilities.LangUtil;
import me.sat7.dynamicshop.utilities.ShopUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class OnChat implements Listener {

    private static final Map<UUID, Integer> runnableMap = new HashMap<>();

    public static void WaitForInput(Player player)
    {
        if(runnableMap.containsKey(player.getUniqueId()))
        {
            cancelRunnable(player);
        }

        BukkitTask taskID = Bukkit.getScheduler().runTaskLater(DynamicShop.plugin, () -> {

            if(Objects.requireNonNull(DynamicShop.ccUser.get().getString(player.getUniqueId() + ".tmpString")).equals("waitforPalette"))
            {
                DynamicShop.ccUser.get().set(player.getUniqueId()+".tmpString","");
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("SEARCH_CANCELED"));
            }
            else if(Objects.requireNonNull(DynamicShop.ccUser.get().getString(player.getUniqueId() + ".tmpString")).contains("waitforInput"))
            {
                DynamicShop.ccUser.get().set(player.getUniqueId()+".tmpString","");
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("INPUT_CANCELED"));
            }
            else if(Objects.requireNonNull(DynamicShop.ccUser.get().getString(player.getUniqueId() + ".tmpString")).equals("waitforPageDelete"))
            {
                DynamicShop.ccUser.get().set(player.getUniqueId()+".tmpString","");
                player.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("INPUT_CANCELED"));
            }

        },400);
        runnableMap.put(player.getUniqueId(), taskID.getTaskId());
    }

    private static void cancelRunnable(Player player) {
        if(runnableMap.containsKey(player.getUniqueId())) {
            Bukkit.getScheduler().cancelTask(runnableMap.get(player.getUniqueId()));
        }
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent e)
    {
        Player p = e.getPlayer();

        if(Objects.requireNonNull(DynamicShop.ccUser.get().getString(p.getUniqueId() + ".tmpString")).equals("waitforPalette"))
        {
            e.setCancelled(true);

            DynamicShop.ccUser.get().set(p.getUniqueId()+".tmpString","");
            DynaShopAPI.openItemPalette(p,1,e.getMessage());
            cancelRunnable(p);
        }
        else if(Objects.requireNonNull(DynamicShop.ccUser.get().getString(p.getUniqueId() + ".tmpString")).contains("waitforInput"))
        {
            e.setCancelled(true);

            String s = Objects.requireNonNull(DynamicShop.ccUser.get().getString(p.getUniqueId() + ".tmpString")).replace("waitforInput","");
            String[] temp = Objects.requireNonNull(DynamicShop.ccUser.get().getString(p.getUniqueId() + ".interactItem")).split("/");

            switch (s) {
                case "btnName":
                    StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".displayName", e.getMessage());
                    break;
                case "btnLore":
                    StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".lore", e.getMessage());
                    break;
                case "btnIcon":
                    try {
                        Material tempMat = Material.getMaterial(ChatColor.stripColor(e.getMessage()).toUpperCase());
                        StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".icon", Objects.requireNonNull(tempMat).name());
                    } catch (Exception exception) {
                        p.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_ITEMNAME"));
                    }
                    break;
                case "btnAction":
                    StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".action", ChatColor.stripColor(e.getMessage()));
                    break;
                case "shopname":
                    if (!ShopUtil.ccShop.get().contains(e.getMessage())) {
                        p.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.SHOP_NOT_FOUND"));
                        return;
                    }

                    StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".displayName", "§3" + e.getMessage());
                    StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".lore", LangUtil.ccLang.get().getString("STARTPAGE.DEFAULT_SHOP_LORE"));
                    StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".icon", Material.EMERALD.name());
                    StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".action", "ds shop " + e.getMessage());
                    break;
                case "deco":
                    try {
                        //Material.RED_STAINED_GLASS_PANE;
                        Material mat = Material.valueOf(e.getMessage().toUpperCase() + "_STAINED_GLASS_PANE");
                        StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".displayName", null);
                        StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".lore", null);
                        StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".icon", mat.name());
                        StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".action", "");
                    } catch (Exception exception) {
                        p.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("ERR.WRONG_USAGE"));
                    }
                    break;
            }

            StartPage.ccStartPage.save();

            DynamicShop.ccUser.get().set(p.getUniqueId()+".tmpString","");
            DynaShopAPI.openStartPage(p);
            cancelRunnable(p);
        }
        else if(Objects.requireNonNull(DynamicShop.ccUser.get().getString(p.getUniqueId() + ".tmpString")).contains("waitforPageDelete"))
        {
            e.setCancelled(true);

            if(e.getMessage().equals("delete"))
            {
                String[] temp = Objects.requireNonNull(DynamicShop.ccUser.get().getString(p.getUniqueId() + ".interactItem")).split("/");
                ShopUtil.deleteShopPage(temp[0],Integer.parseInt(temp[1]));
                DynaShopAPI.openShopGui(p,temp[0],1);
            }
            else
            {
                p.sendMessage(DynamicShop.dsPrefix + LangUtil.ccLang.get().getString("INPUT_CANCELED"));
            }

            DynamicShop.ccUser.get().set(p.getUniqueId()+".interactItem","");
            DynamicShop.ccUser.get().set(p.getUniqueId()+".tmpString","");
            cancelRunnable(p);
        }
    }
}
