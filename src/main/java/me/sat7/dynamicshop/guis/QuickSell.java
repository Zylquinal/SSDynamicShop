package me.sat7.dynamicshop.guis;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.sat7.dynamicshop.utilities.LangUtil;

import java.util.Objects;

public class QuickSell {

    public Inventory getGui(Player player) {
        return Bukkit.createInventory(player,9, Objects.requireNonNull(LangUtil.ccLang.get().getString("QUICKSELL_TITLE")));
    }
}
