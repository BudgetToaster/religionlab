package com.github.budgettoaster.religionlab.perks;

public enum PerkType {
    FOLLOWER {
        @Override
        public String toString() {
            return "follower";
        }
    },
    FOUNDER {
        @Override
        public String toString() {
            return "founder";
        }
    }
}
