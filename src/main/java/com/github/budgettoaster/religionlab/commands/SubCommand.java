package com.github.budgettoaster.religionlab.commands;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public abstract class SubCommand {
    private final boolean playerOnly;
    private final String label, usage, permission, description;
    private final List<String> aliases;

    public SubCommand(boolean playerOnly, String label, String usage, String permission, String description) {
        this.playerOnly = playerOnly;
        this.label = label;
        this.usage = usage;
        this.permission = permission;
        this.description = description;
        this.aliases = Collections.emptyList();
    }

    public SubCommand(boolean playerOnly, String label, String usage, String permission, String description, List<String> aliases) {
        this.playerOnly = playerOnly;
        this.label = label;
        this.usage = usage;
        this.permission = permission;
        this.description = description;
        this.aliases = aliases;
    }

    public boolean init() { return true; }

    abstract boolean execute(CommandSender sender, List<String> args);

    boolean isPlayerOnly() { return this.playerOnly; }
    String getLabel() { return this.label; }
    String getUsage() { return this.usage; }
    String getPermission() { return this.permission; }
    String getDescription() { return this.description; }
    public List<String> getAliases() { return aliases; }
}
