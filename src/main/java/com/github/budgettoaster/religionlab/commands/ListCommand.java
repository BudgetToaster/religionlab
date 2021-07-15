package com.github.budgettoaster.religionlab.commands;

import com.github.budgettoaster.religionlab.Religion;
import com.github.budgettoaster.religionlab.ReligionLab;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListCommand extends SubCommand {
    public ListCommand() {
        super(true,
                "list",
                "/religion list (page)",
                "religion.basic.list",
                "List religions by popularity.");
    }

    @Override
    boolean execute(CommandSender sender, List<String> args) {
        final int numPerPage = 10;

        if(args.size() != 1 && args.size() != 0)
            return false;

        int page = 1;
        if(args.size() == 1) {
            try {
                page = Integer.parseInt(args.get(0));
            }
            catch (NumberFormatException e) {
                return false;
            }
        }
        if(page < 1) {
            sender.sendMessage(ChatColor.RED + "Page cannot be below 1.");
            return true;
        }

        ArrayList<Religion> religions = new ArrayList<>();
        for(Religion r : Religion.getReligions())
            religions.add(r);
        int maxPages = (int)Math.ceil((double) religions.size() / numPerPage);
        if(maxPages == 0) {
            sender.sendMessage(ChatColor.YELLOW + "There are no religions yet.");
            return true;
        }
        if(page > maxPages) {
            sender.sendMessage(ChatColor.RED + "Max page is " + maxPages);
            return true;
        }

        religions.sort(Comparator.comparingInt(Religion::getNumFollowers));


        StringBuilder str = new StringBuilder();
        str.append(MessageFormat.format("{0}--------- {1}Religions ({2}/{3}) {4}---------",
                ChatColor.YELLOW, ChatColor.RESET, page, maxPages, ChatColor.YELLOW));

        List<Religion> slice = religions.subList((page - 1) * numPerPage, Math.min(religions.size(), (page) * numPerPage));
        slice.sort(Comparator.comparingInt(Religion::getNumFollowers));
        for (Religion r : slice) {
            str.append(MessageFormat.format("\n{0}{1}: {2}{3} Followers", ChatColor.YELLOW, r.getName(), ChatColor.RESET, r.getNumFollowers()));
        }
        sender.sendMessage(str.toString().split("\n"));
        return true;
    }
}
