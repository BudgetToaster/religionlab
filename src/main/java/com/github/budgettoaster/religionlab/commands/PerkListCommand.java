package com.github.budgettoaster.religionlab.commands;

import com.github.budgettoaster.religionlab.perks.Perks;
import com.github.budgettoaster.religionlab.perks.base.Perk;
import com.github.budgettoaster.religionlab.perks.PerkType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

public class PerkListCommand extends SubCommand {
    public PerkListCommand() {
        super(
                false,
                "perks",
                "/religion perks [founder|follower]",
                "religionlab.basic.perks",
                "Shows the sender a list of perks.",
                List.of("beliefs")
        );
    }

    @Override
    boolean execute(CommandSender sender, List<String> args) {
        if(args.size() != 1)
            return false;

        StringBuilder str = new StringBuilder();
        if(args.get(0).equalsIgnoreCase("founder")) {
            str.append(MessageFormat.format("{0}--------- {1}Founder Perks {2}---------",
                    ChatColor.YELLOW, ChatColor.RESET, ChatColor.YELLOW));
            int id = 1;
            for(Perk perk : Perks.founder)
                str.append(MessageFormat.format("\n{0}{1} ({2}): {3}{4}",
                        ChatColor.GOLD, perk.getInGameName(PerkType.FOUNDER), id++, ChatColor.RESET, perk.getDescription()));
            sender.sendMessage(str.toString());
            return true;
        }
        else if(args.get(0).equalsIgnoreCase("follower")) {
            str.append(MessageFormat.format("{0}--------- {1}Follower Perks {2}---------",
                    ChatColor.YELLOW, ChatColor.RESET, ChatColor.YELLOW));
            int id = 1;
            for(Perk perk : Perks.follower)
                str.append(MessageFormat.format("\n{0}{1} ({2}): {3}{4}",
                        ChatColor.GOLD, perk.getInGameName(PerkType.FOLLOWER), id++, ChatColor.RESET, perk.getDescription()));
            sender.sendMessage(str.toString());
            return true;
        }
        else return false;
    }
}
