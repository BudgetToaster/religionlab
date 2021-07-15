package com.github.budgettoaster.religionlab;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.budgettoaster.religionlab.perks.Perks;
import com.github.budgettoaster.religionlab.perks.base.Perk;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Religion {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final HashSet<Religion> religions = new HashSet<>();
    private static final HashMap<OfflinePlayer, Religion> religionFollowers = new HashMap<>();
    private static final HashMap<Religion, Integer> numFollowers = new HashMap<>();

    public static void setReligion(OfflinePlayer player, Religion religion) {
        Religion oldReligion = religionFollowers.get(player);

        if(religion == null) religionFollowers.remove(player);
        else religionFollowers.put(player, religion);

        if(oldReligion != null)
            numFollowers.put(oldReligion, oldReligion.getNumFollowers() - 1);

        if(religion != null)
            numFollowers.put(religion, religion.getNumFollowers() + 1);
    }

    public static Religion getReligion(OfflinePlayer player) {
        return religionFollowers.get(player);
    }

    public static Iterable<Religion> getReligions() {
        return religions;
    }

    public static void save() throws IOException {
        File f = new File(ReligionLab.get().getDataFolder(), "religions.json");
        ArrayNode root = mapper.createArrayNode();
        for(Religion r : religions) {
            ObjectNode node = mapper.createObjectNode();
            node.put("name", r.getName());
            node.put("founder", r.getFounder().getUniqueId().toString());
            ArrayNode followerPerksNode = mapper.createArrayNode();
            for(Perk p : r.followerPerks)
                followerPerksNode.add(p.toString());
            node.set("followerPerks", followerPerksNode);
            node.put("founderPerk", r.founderPerk.toString());
            ArrayNode followersNode = mapper.createArrayNode();
            for(Map.Entry<OfflinePlayer, Religion> entry : religionFollowers.entrySet()) {
                if(entry.getValue() == r)
                    followersNode.add(entry.getKey().getUniqueId().toString());
            }
            node.set("followers", followersNode);
            root.add(node);
        }
        mapper.writeValue(f, root);
    }

    public static void load() throws IOException {
        File f = new File(ReligionLab.get().getDataFolder(), "religions.json");
        f.createNewFile();
        ArrayNode root;
        try {
            JsonNode node = mapper.readTree(f);
            if(node == MissingNode.getInstance())
                return;
            root = (ArrayNode) node;
        }
        catch(JsonProcessingException ignored) {
            return;
        }

        for(JsonNode religionNode : root) {
            String name = religionNode.get("name").asText();
            OfflinePlayer founder = ReligionLab.get().getServer().getOfflinePlayer(UUID.fromString(religionNode.get("founder").asText()));
            ArrayList<Perk> followerPerks = new ArrayList<>();
            for(JsonNode followerPerkNode : religionNode.get("followerPerks"))
                followerPerks.add(Perks.getPerk(followerPerkNode.asText()));
            Perk founderPerk = Perks.getPerk(religionNode.get("founderPerk").asText());

            Religion r = new Religion(name, founder, founderPerk, followerPerks);
            r.register();

            for(JsonNode playerNode : religionNode.get("followers")) {
                OfflinePlayer player = ReligionLab.get().getServer().getOfflinePlayer(UUID.fromString(playerNode.asText()));
                religionFollowers.put(player, r);
            }
        }

        for(Map.Entry<OfflinePlayer, Religion> entry : religionFollowers.entrySet()) {
            if(!numFollowers.containsKey(entry.getValue()))
                numFollowers.put(entry.getValue(), 0);
            numFollowers.put(entry.getValue(), numFollowers.get(entry.getValue()) + 1);
        }
    }


    private String name;
    private final OfflinePlayer founder;
    private final HashSet<Perk> followerPerks = new HashSet<>();
    private Perk founderPerk;

    public Religion(String name, OfflinePlayer founder, Perk founderPerk, List<Perk> followerPerks) {
        religions.add(this);
        this.name = name;
        this.founder = founder;
        this.founderPerk = founderPerk;
        this.followerPerks.addAll(followerPerks);
    }

    public int getNumFollowers() {
        Integer num = numFollowers.get(this);
        return num == null ? 0 : num;
    }

    public Perk getFounderPerk() {
        return founderPerk;
    }

    public void setFounderPerk(Perk founderPerk) {
        this.founderPerk = founderPerk;
    }

    public HashSet<Perk> getFollowerPerks() {
        return followerPerks;
    }

    public OfflinePlayer getFounder() {
        return founder;
    }

    public void register() {
        religions.add(this);
    }

    public void unregister() {
        religions.remove(this);
        numFollowers.remove(this);
        ArrayList<OfflinePlayer> toRemove = new ArrayList<>();
        for(Map.Entry<OfflinePlayer, Religion> entry : religionFollowers.entrySet()) {
            if(entry.getValue() == this)
                toRemove.add(entry.getKey());
        }
        for(OfflinePlayer p : toRemove)
            religionFollowers.remove(p);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
