package com.github.budgettoaster.religionlab;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class AncientTextGenerator implements Listener {
    private double ancientTextGenChance;
    private int minAncientTextYear;
    private int maxAncientTextYear;
    private boolean useCommonEra;

    public ItemStack createAncientText() {
        ItemStack stack = new ItemStack(Material.ENCHANTED_BOOK, 1);
        ItemMeta meta = Objects.requireNonNull(stack.getItemMeta());
        meta.setLocalizedName("Ancient Text");
        meta.setDisplayName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Ancient Text");
        int yearCreated = new Random().nextInt(maxAncientTextYear - minAncientTextYear + 1) + minAncientTextYear;
        String eraCreated = useCommonEra ? (yearCreated < 0 ? "BCE" : "CE") : (yearCreated < 0 ? "BC" : "AD");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW.toString() + Math.abs(yearCreated) + eraCreated);
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    public boolean isAncientText(ItemStack itemStack) {
        return itemStack != null &&
                itemStack.hasItemMeta() &&
                itemStack.getItemMeta().hasLocalizedName() &&
                itemStack.getItemMeta().getLocalizedName().equals("Ancient Text");
    }

    public boolean init() {
        FileConfiguration config = ReligionLab.get().getConfig();
        ancientTextGenChance = config.getDouble("ancient text generation chance", -1.0);
        maxAncientTextYear = config.getInt("max ancient text creation year", 2147483647);
        minAncientTextYear = config.getInt("min ancient text creation year", 2147483647);
        useCommonEra = config.getBoolean("use common era", true);

        if(ancientTextGenChance == -1.0)
            ReligionLab.get().getLogger().severe("Invalid ancient text generation chance! Make sure your config file is valid!");
        else if(minAncientTextYear == 2147483647)
            ReligionLab.get().getLogger().severe("Invalid max ancient text creation year! Make sure your config file is valid!");
        else if(maxAncientTextYear == 2147483647)
            ReligionLab.get().getLogger().severe("Invalid min ancient text creation year! Make sure your config file is valid!");
        else if(maxAncientTextYear < minAncientTextYear)
            ReligionLab.get().getLogger().severe("max ancient text year is smaller than min ancient text year! Make sure your config file is valid!");
        else {
            ReligionLab.get().getServer().getPluginManager().registerEvents(this, ReligionLab.get());
            return true;
        }
        return false;
    }

    /**
     * Creates a small chance for a zombie to drop a sacred text on death.
     * @param ev Event data
     */
    @EventHandler
    private void onLootGenerate(LootGenerateEvent ev) {
        Random random = new Random();
        if(random.nextFloat() < ancientTextGenChance)
            ev.getLoot().add(createAncientText());
    }
}
