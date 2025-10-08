package com.km.lastwar.vpbot.domain.stats;

import java.io.Serializable;
import java.util.Date;

public record Stats(
        long start,
        Date lastFlAction,
        boolean flInitialized,
        int countRecovery,
        int countFL,
        int moyenneFl,
        int countFLAdd,
        int countFLKick,
        int countStrat,
        int countSecu,
        int countDev,
        int countScience,
        int countInter,
        int countKickStrat,
        int countKickSecu,
        int countKickDev,
        int countKickScience,
        int countKickInter,
        int countMilitary,
        int countAdmin,
        int countKickMilitary,
        int countKickAdmin,
        int countFLTitle,
        long flTime,
        Date nextStratKickCheck,
        Date nextSecuKickCheck,
        Date nextDevKickCheck,
        Date nextScienceKickCheck,
        Date nextInterKickCheck,
        Date nextMilitaryKickCheck,
        Date nextAdminKickCheck,
        Date startDateStats
) implements Serializable {

    private static final long serialVersionUID = 1L;

    public static Stats initial() {

        long start = System.currentTimeMillis();
        Date now = new Date();
        return new Stats(
                start, now, false, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, now, now, now, now, now, now, now, now
        );
    }

    public Stats incrementRecovery() {

        return new Stats(start, lastFlAction, flInitialized, countRecovery + 1, countFL, moyenneFl, countFLAdd, countFLKick,
                         countStrat, countSecu, countDev, countScience, countInter, countKickStrat, countKickSecu, countKickDev,
                         countKickScience, countKickInter, countMilitary, countAdmin, countKickMilitary, countKickAdmin, countFLTitle,
                         flTime, nextStratKickCheck, nextSecuKickCheck, nextDevKickCheck, nextScienceKickCheck, nextInterKickCheck,
                         nextMilitaryKickCheck, nextAdminKickCheck, startDateStats
        );
    }
}