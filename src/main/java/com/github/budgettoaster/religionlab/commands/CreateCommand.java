package com.github.budgettoaster.religionlab.commands;

import com.github.budgettoaster.religionlab.perks.Perks;
import com.github.budgettoaster.religionlab.perks.base.Perk;
import com.github.budgettoaster.religionlab.Religion;
import com.github.budgettoaster.religionlab.ReligionLab;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CreateCommand extends SubCommand {
    private int levelsNeededToFound;

    public CreateCommand() {
        super(
                true,
                "create",
                "/religion create [Founder Perk ID] [Name]",
                "religion.basic.create",
                "Creates a religion."
        );
    }

    @Override
    public boolean init() {
        levelsNeededToFound = ReligionLab.get().getConfig().getInt("levels needed to found", 2147483647);
        if(levelsNeededToFound == 2147483647) {
            ReligionLab.get().getLogger().severe("Invalid levels needed to found! Make sure your config file is valid!");
            return false;
        }
        return true;
    }

    public boolean execute(CommandSender sender, List<String> args) {
        if(Religion.getReligion((Player)sender) != null) {
            sender.sendMessage(ChatColor.RED + "Can't create a religion when you are already in one.");
            return true;
        }

        if(findAncientText((Player) sender) == null) {
            sender.sendMessage(ChatColor.RED + "Cannot create a religion without an ancient text!");
            return true;
        }

        if(args.size() < 2)
            return false;

        String name = String.join(" ", args.subList(1, args.size()));
        Perk perk;
        try {
            int perkIdx = Integer.parseInt(args.get(0));
            perk = Perks.founder.get(perkIdx - 1);
        }
        catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            sender.sendMessage(ChatColor.RED + "Invalid founder perk id. For a list of perks use /religion perks founder.");
            return true;
        }

        ItemStack ancientText = findAncientText((Player) sender);
        if(ancientText == null)
            sender.sendMessage(ChatColor.RED + "Cannot create a religion without an ancient text.");
        else if(((Player) sender).getLevel() < levelsNeededToFound)
            sender.sendMessage(ChatColor.RED + "Not enough levels! You need " + levelsNeededToFound);
        else  {
            Religion religion = new Religion(name, (OfflinePlayer) sender, perk, new ArrayList<>());
            religion.register();
            ancientText.setAmount(ancientText.getAmount() - 1);
            ((Player) sender).setLevel(((Player) sender).getLevel() - levelsNeededToFound);
            sender.sendMessage(ChatColor.GOLD + "Religion created!");
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
