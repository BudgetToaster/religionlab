package com.github.budgettoaster.religionlab.perks;

import com.github.budgettoaster.religionlab.Religion;
import com.github.budgettoaster.religionlab.ReligionLab;
import com.github.budgettoaster.religionlab.perks.base.SimplePerk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.Random;

public class FortunePerk extends SimplePerk {
    private final Random random = new Random();
    private final boolean founderWorksOnNetherite;
    private final boolean followerWorksOnNetherite;

    FortunePerk() {
        super("fortune", "probability", VarType.PROBABILITY);
        FileConfiguration config = ReligionLab.get().getConfig();
        founderWorksOnNetherite = config.getBoolean("founder perks.fortune.works on netherite", false);
        followerWorksOnNetherite = config.getBoolean("follower perks.fortune.works on netherite", false);
    }

    @EventHandler
    public void onMineBlock(BlockBreakEvent ev) {
        Material drop = null;
        Material blockType =  ev.getBlock().getType();

        ItemStack hand = ev.getPlayer().getInventory().getItemInMainHand();
        if(hand.hasItemMeta() && hand.getItemMeta().getEnchantLevel(Enchantment.SILK_TOUCH) > 0 ||
                ev.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        // Ugly, but cant think of a better way to do this
        if(blockType == Material.DIAMOND_ORE || blockType == Material.DEEPSLATE_DIAMOND_ORE)
            drop = Material.DIAMOND;
        else if(blockType == Material.EMERALD_ORE || blockType == Material.DEEPSLATE_EMERALD_ORE)
            drop = Material.EMERALD;
        else if(blockType == Material.COAL_ORE || blockType == Material.DEEPSLATE_COAL_ORE)
            drop = Material.COAL;
        else if(blockType == Material.REDSTONE_ORE || blockType == Material.DEEPSLATE_REDSTONE_ORE)
            drop = Material.REDSTONE;
        else if(blockType == Material.GOLD_ORE || blockType == Material.DEEPSLATE_GOLD_ORE)
            drop = Material.RAW_GOLD;
        else if(blockType == Material.NETHER_GOLD_ORE)
            drop = Material.GOLD_NUGGET;
        else if(blockType == Material.IRON_ORE || blockType == Material.DEEPSLATE_IRON_ORE)
            drop = Material.RAW_IRON;
        else if(blockType == Material.NETHER_QUARTZ_ORE)
            drop = Material.QUARTZ;

        Religion religion = Religion.getReligion(ev.getPlayer());
        if(religion == null) return;
        BigDecimal numFollowers = BigDecimal.valueOf(religion.getNumFollowers());

        if(isFounderEnabled() && getFounderExpression() != null &&
                religion.getFounderPerk() == this && religion.getFounder().equals(ev.getPlayer())) {
            if(founderWorksOnNetherite && blockType == Material.ANCIENT_DEBRIS)
                drop = Material.NETHERITE_SCRAP;
            if(drop != null) {
                double chance = getFounderExpression().with("x", numFollowers).eval().doubleValue();
                if(random.nextDouble() < chance)
                    ev.getBlock().getWorld().dropItemNaturally(ev.getBlock().getLocation(), new ItemStack(drop, 1));
            }
        }
        if(isFollowerEnabled() && getFollowerExpression() != null &&
                religion.getFollowerPerks().contains(this)) {
            if(drop == Material.NETHERITE_SCRAP) drop = null;
            if(followerWorksOnNetherite && blockType == Material.ANCIENT_DEBRIS)
                drop = Material.NETHERITE_SCRAP;
            if(drop != null) {
                double chance = getFollowerExpression().with("x", numFollowers).eval().doubleValue();
                if (random.nextDouble() < chance)
                    ev.getBlock().getWorld().dropItemNaturally(ev.getBlock().getLocation(), new ItemStack(drop, 1));
            }
        }
    }

    @Override
    public String getDescription() {
        return "Small chance to get one extra ore when mining.";
    }
};
