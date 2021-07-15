package com.github.budgettoaster.religionlab.perks;

import com.github.budgettoaster.religionlab.ReligionLab;
import dev.xanhub.religionlab.perks.*;
import com.github.budgettoaster.religionlab.perks.base.FollowerPerk;
import com.github.budgettoaster.religionlab.perks.base.FounderPerk;
import com.github.budgettoaster.religionlab.perks.base.Perk;

import java.util.ArrayList;
import java.util.List;

public class Perks {
    public static FortunePerk fortune;
    public static StrengthPerk strength;
    public static PrecisionPerk precision;
    public static ExperiencePerk experience;
    public static SpeedPerk speed;
    public static HealthPerk health;
    public static final List<Perk> all = new ArrayList<>();
    public static final List<FounderPerk> founder = new ArrayList<>();
    public static final List<FollowerPerk> follower = new ArrayList<>();

    public static boolean init() {
        fortune = new FortunePerk();
        strength = new StrengthPerk();
        precision = new PrecisionPerk();
        experience = new ExperiencePerk();
        speed = new SpeedPerk();
        health = new HealthPerk();

        all.add(fortune);
        all.add(strength);
        all.add(precision);
        all.add(experience);
        all.add(speed);
        all.add(health);

        for(Perk p : all) {
            if(p instanceof FounderPerk) founder.add((FounderPerk) p);
            if(p instanceof FollowerPerk) follower.add((FollowerPerk) p);
        }

        for(Perk p : all)
            ReligionLab.get().getServer().getPluginManager().registerEvents(p, ReligionLab.get());
        return true;
    }

    public static Perk getPerk(String name) {
        return all.stream().filter(i -> i.toString().equals(name)).findFirst().orElse(null);
    }
}
