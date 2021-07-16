package com.github.budgettoaster.religionlab.commands;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.budgettoaster.religionlab.Religion;
import com.github.budgettoaster.religionlab.ReligionLab;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.WorldSaveEvent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class JoinCommand extends SubCommand {
    private static final ObjectMapper mapper = new ObjectMapper();
    private HashMap<UUID, Long> lastJoin = new HashMap<>();
    private File saveFile = new File(ReligionLab.get().getDataFolder(), "lastjoin.json");
    private boolean oneJoinPerDay;

    private void save() throws IOException {
        saveFile.createNewFile();
        mapper.writeValue(saveFile, lastJoin);
    }

    public JoinCommand() {
        super(true,
                "join",
                "/religion join [Name]",
                "religionlab.basic.join",
                "Join a religion.");
        oneJoinPerDay = ReligionLab.get().getConfig().getBoolean("one join per day", true);
        try {
            saveFile.createNewFile();
            lastJoin = mapper.readValue(saveFile, new TypeReference<>() {
                @Override
                public Type getType() {
                    return super.getType();
                }
            });
        } catch (JsonParseException | JsonMappingException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    boolean execute(CommandSender sender, List<String> args) {
        if(args.size() == 0) return false;

        String name = String.join(" ", args);
        Religion religion = null;
        for(Religion r : Religion.getReligions())
            if(r.getName().equalsIgnoreCase(name)) {
                religion = r;
                break;
            }

        UUID uuid = ((OfflinePlayer) sender).getUniqueId();
        if(lastJoin.containsKey(uuid)) {
            if(System.currentTimeMillis() - lastJoin.get(uuid) < 86400000) {
                if(oneJoinPerDay) {
                    sender.sendMessage(ChatColor.RED + "You can only join a religion once per day.");
                    return true;
                }
            }
            else {
                lastJoin.remove(uuid);
            }
        }

        if(Religion.getReligion((OfflinePlayer) sender) != null) {
            sender.sendMessage(ChatColor.RED + "You are already in a religion.");
            return true;
        }

        if(religion == null) {
            sender.sendMessage(ChatColor.RED + "That is not a valid religion.");
            return true;
        }

        Religion.setReligion(((OfflinePlayer) sender), religion);
        lastJoin.put(uuid, System.currentTimeMillis());
        try {
            this.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sender.sendMessage(ChatColor.YELLOW + "You have joined " + religion.getName() + ".");
        return true;
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent ev) {
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onDisable(PluginDisableEvent ev) {
        if(ev.getPlugin() == ReligionLab.get()) {
            try {
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
