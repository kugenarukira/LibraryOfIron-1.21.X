package net.ironedge.libraryofiron.combat.system;

import net.ironedge.libraryofiron.combat.data.HitProfile;

import java.util.HashMap;
import java.util.Map;

public final class SpecialMoveRegistry {

    private static final Map<String, HitProfile> moves = new HashMap<>();

    public static void register(String id, HitProfile profile) {
        moves.put(id, profile);
    }

    public static HitProfile get(String id) {
        return moves.get(id);
    }
}
