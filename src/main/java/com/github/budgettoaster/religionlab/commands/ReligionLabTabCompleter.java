package com.github.budgettoaster.religionlab.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReligionLabTabCompleter implements TabCompleter {
    final ArrayList<String> list = new ArrayList<>();

    public ReligionLabTabCompleter() {
        for(SubCommand command : ReligionBaseCommand.getSubCommands()) {
            list.add(command.getLabel());
            list.addAll(command.getAliases());
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 0)
            return list;
        else if(args.length > 1)
            return Collections.emptyList();
        else
            return list.stream().filter(str -> str.startsWith(args[0])).collect(Collectors.toList());
    }
}
