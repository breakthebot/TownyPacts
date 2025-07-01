package org.breakthebot.townyPacts.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import org.breakthebot.townyPacts.TownyPacts;
import org.breakthebot.townyPacts.object.Pact;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

public class MetaData {
    private static final String ACTIVE_METAKEY = "active_pacts";
    private static final String PENDING_METAKEY = "pending_pacts";
    private static final Gson gson = new Gson();
    private static MetaData instance;
    private static final Logger logger = TownyPacts.getInstance().getLogger();
    private static final Type PACT_LIST_TYPE = new TypeToken<List<Pact>>() {}.getType();

    private MetaData() {}

    public static MetaData getInstance() {
        if (instance == null)
            instance = new MetaData();
        return instance;
    }



    private static List<Pact> getPacts(Nation nation, String metaKey) {
        try {
            if (!nation.hasMeta(metaKey)) return new ArrayList<>();

            StringDataField field = (StringDataField) nation.getMetadata(metaKey);
            if (field == null || field.getValue() == null || field.getValue().isEmpty()) return new ArrayList<>();

            String json = new String(Base64.getDecoder().decode(field.getValue()), StandardCharsets.UTF_8);
            List<Pact> pacts = gson.fromJson(json, PACT_LIST_TYPE);
            return pacts != null ? pacts : new ArrayList<>();
        } catch (Exception e) {
            logger.warning("[TownyPacts] Failed to load pacts from '" + metaKey + "': " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static void savePacts(Nation nation, String metaKey, List<Pact> pacts) {
        try {
            if (nation.hasMeta(metaKey)) {
                nation.removeMetaData(metaKey);
            }

            if (!pacts.isEmpty()) {
                String json = gson.toJson(pacts);
                String encoded = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
                StringDataField dataField = new StringDataField(metaKey, encoded);
                nation.addMetaData(dataField);
            }

            nation.save();
        } catch (Exception e) {
            logger.severe("[TownyPacts] Failed to save pacts to '" + metaKey + "': " + e.getMessage());
        }
    }

    private static boolean hasPact(Nation nationA, Nation nationB, String metaKey) {
        List<Pact> pacts = getPacts(nationA, metaKey);
        return pacts.stream().anyMatch(p -> {
            String target = p.getTargetNation(nationA.getName());
            return target != null && target.equalsIgnoreCase(nationB.getName());
        });
    }

    private static Pact getPact(Nation nationA, Nation nationB, String metaKey) {
        List<Pact> pacts = getPacts(nationA, metaKey);
        return pacts.stream()
                .filter(p -> {
                    String target = p.getTargetNation(nationA.getName());
                    return target != null && target.equalsIgnoreCase(nationB.getName());
                })
                .findFirst()
                .orElse(null);
    }


    private static void removePact(Nation nationA, Nation nationB, String metaKey) {
        List<Pact> pacts = getPacts(nationA, metaKey);
        pacts.removeIf(p -> {
            String target = p.getTargetNation(nationA.getName());
            return target != null && target.equalsIgnoreCase(nationB.getName());
        });
        savePacts(nationA, metaKey, pacts);
    }

    private static void addOrUpdatePact(Nation nation, Pact newPact, String metaKey) {
        List<Pact> pacts = getPacts(nation, metaKey);
        String own = nation.getName();

        pacts.removeIf(p -> {
            String target = p.getTargetNation(own);
            String newTarget = newPact.getTargetNation(own);
            return target != null && target.equalsIgnoreCase(newTarget);
        });

        pacts.add(newPact);
        savePacts(nation, metaKey, pacts);
    }


    // Active
    public static List<Pact> getActivePacts(Nation nation) {
        return getPacts(nation, ACTIVE_METAKEY);
    }

    public static void saveActivePacts(Nation nation, List<Pact> pacts) {
        savePacts(nation, ACTIVE_METAKEY, pacts);
    }

    public static boolean hasActivePact(Nation a, Nation b) {
        return hasPact(a, b, ACTIVE_METAKEY);
    }

    public static Pact getActivePact(Nation a, Nation b) { return getPact(a, b, ACTIVE_METAKEY); }

    public static void removeActivePact(Nation a, Nation b) {
        removePact(a, b, ACTIVE_METAKEY);
    }

    public static void updateActivePact(Nation nation, Pact pact) {
        addOrUpdatePact(nation, pact, ACTIVE_METAKEY);
    }

    // Pending
    public static List<Pact> getPendingPacts(Nation nation) {
        return getPacts(nation, PENDING_METAKEY);
    }

    public static void savePendingPacts(Nation nation, List<Pact> pacts) {
        savePacts(nation, PENDING_METAKEY, pacts);
    }

    public static boolean hasPendingPact(Nation a, Nation b) {
        return hasPact(a, b, PENDING_METAKEY);
    }

    public static Pact getPendingPact(Nation a, Nation b) { return getPact(a, b, PENDING_METAKEY); }


    public static void removePendingPact(Nation a, Nation b) {
        removePact(a, b, PENDING_METAKEY);
    }

    public static void updatePendingPact(Nation nation, Pact pact) {
        addOrUpdatePact(nation, pact, PENDING_METAKEY);
    }
}