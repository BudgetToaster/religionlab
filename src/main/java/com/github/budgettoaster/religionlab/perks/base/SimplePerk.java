package com.github.budgettoaster.religionlab.perks.base;

import com.github.budgettoaster.religionlab.ReligionLab;
import com.github.budgettoaster.religionlab.perks.PerkType;
import com.udojava.evalex.Expression;
import org.apache.commons.lang.WordUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.logging.Logger;

public abstract class SimplePerk implements FollowerPerk, FounderPerk {
    public enum VarType {
        PROBABILITY {
            @Override
            boolean Validate(double min, double max, double slope) {
                return Math.abs(min - 0.5) <= 0.5 && Math.abs(max - 0.5) <= 0.5;
            }
        },
        SCALAR {
            @Override
            boolean Validate(double min, double max, double slope) {
                return true;
            }
        };

        abstract boolean Validate(double min, double max, double slope);
    }

    public static Expression LoadPerkExpression(String perkName, String variableName, PerkType perkType, VarType varType) throws InvalidConfigException {
        FileConfiguration config = ReligionLab.get().getConfig();
        Logger logger = ReligionLab.get().getLogger();

        String varMinPath = MessageFormat.format("{0} perks.{1}.min {2}", perkType, perkName, variableName);
        String varMaxPath = MessageFormat.format("{0} perks.{1}.max {2}", perkType, perkName, variableName);
        String varSlopePath = MessageFormat.format("{0} perks.{1}.{2} increase per follower", perkType, perkName, variableName);
        String expressionPath = MessageFormat.format("{0} perks.{1}.{2} expression", perkType, perkName, variableName);
        double varMin = config.getDouble(varMinPath, -1.0);
        double varMax = config.getDouble(varMaxPath, -1.0);
        double varSlope = config.getDouble(varSlopePath, -1.0);
        String expressionStr = config.getString(expressionPath, "none");

        if(expressionStr.equals("none")) {
            if(varMin == -1 || varMax == -1 || varSlope == -1 || varMax < varMin || !varType.Validate(varMin, varMax, varSlope)) {
                String pattern = "Invalid configuration for {0} perk ''{1}''! Disabling perk.";
                logger.warning(MessageFormat.format(pattern, perkType, perkName));
                throw new InvalidConfigException();
            }
            expressionStr = MessageFormat.format("max(min({0}+{1}*x,{2}), {0})", varMin, varSlope, varMax);
        }
        else ReligionLab.get().getLogger().info(MessageFormat.format("Using {0} {1} perk equation: {2}", perkName, perkType, expressionStr));

        Expression expression = new Expression(expressionStr);
        if(isInvalidExpression(expression)) {
            ReligionLab.get().getLogger().warning(MessageFormat.format("Invalid equation for {0} perk ''{1}''. Disabling perk.", perkType, perkName));
            throw new InvalidConfigException();
        }

        return expression;
    }

    private static boolean isInvalidExpression(Expression expression) {
        HashSet<String> variables = new HashSet<>(expression.getUsedVariables());
        variables.removeAll(expression.getDeclaredVariables());
        return variables.size() != 1 || !variables.contains("x");
    }

    private final String name;
    private Expression founderExpression = null;
    private Expression followerExpression = null;
    private final String inGameFounderName;
    private final String inGameFollowerName;
    private boolean followerEnabled = true;
    private boolean founderEnabled = true;

    public SimplePerk(String name, String varName, VarType varType) {
        this.name = name;
        try {
            founderExpression = LoadPerkExpression(name.toLowerCase(Locale.ROOT), varName, PerkType.FOUNDER, varType);
        } catch (InvalidConfigException ignored) { }
        try {
            followerExpression = LoadPerkExpression(name.toLowerCase(Locale.ROOT), varName, PerkType.FOLLOWER, varType);
        } catch (InvalidConfigException ignored) { }
        inGameFounderName = ReligionLab.get().getConfig().getString(MessageFormat.format("founder perks.{0}.name", name), WordUtils.capitalizeFully(name));
        inGameFollowerName = ReligionLab.get().getConfig().getString(MessageFormat.format("follower perks.{0}.name", name), WordUtils.capitalizeFully(name));
        followerEnabled = ReligionLab.get().getConfig().getBoolean(MessageFormat.format("follower perks.{0}.enabled", name), true);
        founderEnabled = ReligionLab.get().getConfig().getBoolean(MessageFormat.format("founder perks.{0}.enabled", name), true);
    }

    protected Expression getFounderExpression() {
        return founderExpression;
    }

    protected Expression getFollowerExpression() {
        return followerExpression;
    }

    @Override
    public void setFollowerEnabled(boolean enabled) {
        followerEnabled = enabled;
    }

    @Override
    public boolean isFollowerEnabled() {
        return followerEnabled;
    }

    @Override
    public void setFounderEnabled(boolean enabled) {
        founderEnabled = enabled;
    }

    @Override
    public boolean isFounderEnabled() {
        return founderEnabled;
    }

    @Override
    public String getInGameName(PerkType type) {
        return type == PerkType.FOLLOWER ? inGameFollowerName : inGameFounderName;
    }

    @Override
    public String toString() {
        return WordUtils.capitalizeFully(this.name);
    }
}
