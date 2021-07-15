package com.github.budgettoaster.religionlab.commands;

import com.github.budgettoaster.religionlab.perks.Perks;
import com.github.budgettoaster.religionlab.perks.base.Perk;
import com.github.budgettoaster.religionlab.Religion;
import com.github.budgettoaster.religionlab.ReligionLab;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EnhanceCommand extends SubCommand {
    private int levelsNeededToEnhance;
    private int maxFollowerPerks = -1;

    public EnhanceCommand() {
        super(
                true,
                "enhance",
                "/religion enhance [Follower Perk ID]",
                "religion.basic.enhance",
                "Adds a follower perk to your religion."
        );
    }

    @Override
    public boolean init() {
        levelsNeededToEnhance = ReligionLab.get().getConfig().getInt("levels needed to enhance", 2147483647);
        if(levelsNeededToEnhance == 2147483647) {
            ReligionLab.get().getLogger().warning("Invalid levels needed to enhance! Make sure your config file is valid!");
            return false;
        }
        maxFollowerPerks = ReligionLab.get().getConfig().getInt("max follower perks per religion", -1);
        if(maxFollowerPerks < 0) {
            ReligionLab.get().getLogger().warning("Invalid 'max follower perks per religion'! Players will not be able to enhance their religion.");
        }
        return true;
    }

    @Override
    boolean execute(CommandSender sender, List<String> args) {
        Religion religion = Religion.getReligion((Player)sender);

        if(maxFollowerPerks == -1) {
            sender.sendMessage(ChatColor.RED + "Cannot enhance religions until configuration is fixed. Please message an admin to check logs.");
            return true;
        }

        if(religion == null || !Religion.getReligion((Player)sender).getFounder().equals(sender)) {
            sender.sendMessage(ChatColor.RED + "You must be the founder of a religion to do that.");
            return true;
        }

        if(findAncientText((Player) sender) == null) {
            sender.sendMessage(ChatColor.RED + "Cannot enhance a religion without an ancient text!");
            return true;
        }

        if(args.size() != 1)
            return false;

        Perk perk;
        try {
            int perkIdx = Integer.parseInt(args.get(0));
            perk = Perks.follower.get(perkIdx - 1);
        }
        catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            sender.sendMessage(ChatColor.RED + "Invalid follower perk id. For a list of perks use /religion perks follower.");
            return true;
        }

        if(religion.getFollowerPerks().contains(perk)) {
            sender.sendMessage(ChatColor.RED + "Your religion already has that perk.");
            return true;
        }

        if(religion.getFollowerPerks().size() >= maxFollowerPerks) {
            sender.sendMessage(ChatColor.RED + "Your religion already has the maximum number of perks.");
            return true;
        }

        ItemStack ancientText = findAncientText((Player) sender);
        if(ancientText == null)
            sender.sendMessage(ChatColor.RED + "Cannot enhance a religion without an ancient text.");
        else if(((Player) sender).getLevel() < levelsNeededToEnhance)
            sender.sendMessage(ChatColor.RED + "Not enough levels! You need " + levelsNeededToEnhance);
        else  {
            religion.getFollowerPerks().add(perk);
            ancientText.setAmount(ancientText.getAmount() - 1);
            ((Player) sender).setLevel(((Player) sender).getLevel() - levelsNeededToEnhance);
            sender.sendMessage(ChatColor.GOLD + "Religion enhanced!");
        }
        return true;
    }

    private static ItemStack findAncientText(Player sender) {
        for(ItemStack s : sender.getInventory()) {
            if(ReligionLab.get().getAncientTextGenerator().isAncientText(s)) {
                return s;
            }
        }
        return null;
    }
}
