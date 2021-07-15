package com.github.budgettoaster.religionlab.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class SubCommand {
    private final boolean playerOnly;
    private final String label, usage, permission, description;

    public SubCommand(boolean playerOnly, String label, String usage, String permission, String description) {
        this.playerOnly = playerOnly;
        this.label = label;
        this.usage = usage;
        this.permission = permission;
        this.description = description;
    }

    public boolean init() { return true; }

    abstract boolean execute(CommandSender sender, List<String> args);

    boolean isPlayerOnly() { return this.playerOnly; }
    String getLabel() { return this.label; }
    String getUsage() { return this.usage; }
    String getPermission() { return this.permission; }
    String getDescription() { return this.description; }
}
