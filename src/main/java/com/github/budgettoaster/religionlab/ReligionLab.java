package com.github.budgettoaster.religionlab;

import com.github.budgettoaster.religionlab.perks.Perks;
import com.github.budgettoaster.religionlab.commands.ReligionBaseCommand;
import com.github.budgettoaster.religionlab.commands.ReligionLabTabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Objects;

public final class ReligionLab extends JavaPlugin implements Listener {
    private static ReligionLab instance;
    public static ReligionLab get() { return instance; }

    private AncientTextGenerator ancientTextGenerator;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        ReligionBaseCommand baseCommand = new ReligionBaseCommand();
        baseCommand.init();
        Objects.requireNonNull(this.getCommand("religion")).setExecutor(baseCommand);
        Objects.requireNonNull(this.getCommand("religion")).setTabCompleter(new ReligionLabTabCompleter());

        Perks.init();

        ancientTextGenerator = new AncientTextGenerator();
        if(!ancientTextGenerator.init()) return;

        try {
            Religion.load();
        } catch (IOException e) {
            getLogger().severe("IO Error trying to load religions! Stopping server to prevent data corruption.");
            getServer().shutdown();
            return;
        }

        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("ReligionLab loaded.");
    }

    @EventHandler
    public void worldSaveEvent(WorldSaveEvent ev) {
        try {
            Religion.save();
        } catch (IOException e) {
            getLogger().warning("Failed to save religions file.");
        }
    }

    public AncientTextGenerator getAncientTextGenerator() {
        return ancientTextGenerator;
    }

    @Override
    public void onDisable() {
        try {
            Religion.save();
        } catch (IOException e) {
            getLogger().severe("Failed to save religions file! An IO error occurred.");
            e.printStackTrace();
        }
        getLogger().info("ReligionLab disabled.");
    }
}
