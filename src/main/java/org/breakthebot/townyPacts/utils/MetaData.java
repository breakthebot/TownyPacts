package org.breakthebot.townyPacts.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import org.breakthebot.townyPacts.TownyPacts;
import org.breakthebot.townyPacts.pact.pactObject;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

public class MetaData {
    private static final String META_KEY = "nation_pacts";
    private static final Gson gson = new Gson();
    private static MetaData instance;
    private static final Logger logger = TownyPacts.getInstance().getLogger();
    private static final Type PACT_LIST_TYPE = new TypeToken<List<pactObject>>() {}.getType();

    private MetaData() {}

    public static MetaData getInstance() {
        if (instance == null)
            instance = new MetaData();
        return instance;
    }

    public static String getMetaDataKey() {
        return META_KEY;
    }

    public static List<pactObject> getPacts(Nation nation) {
        try {
            if (!nation.hasMeta(META_KEY)) return new ArrayList<>();

            StringDataField field = (StringDataField) nation.getMetadata(META_KEY);
            assert field != null;
            String base64 = field.getValue();

            if (base64 == null || base64.isEmpty()) return new ArrayList<>();

            String json = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
            List<pactObject> pacts = gson.fromJson(json, PACT_LIST_TYPE);

            return pacts != null ? pacts : new ArrayList<>();
        } catch (Exception e) {
            logger.warning("[TownyPacts] Failed to load pacts for nation '" + nation.getName() + "': " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void savePacts(Nation nation, List<pactObject> pacts) {
        try {
            if (nation.hasMeta(META_KEY)) {
                nation.removeMetaData(META_KEY);
            }

            if (!pacts.isEmpty()) {
                String json = gson.toJson(pacts);
                String encoded = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
                StringDataField dataField = new StringDataField(META_KEY, encoded);
                nation.addMetaData(dataField);
            }

            nation.save();
        } catch (Exception e) {
            logger.severe("[TownyPacts] Failed to save pacts for nation '" + nation.getName() + "': " + e.getMessage());
        }
    }

    public static boolean hasPact(Nation nationA, Nation nationB) {
        List<pactObject> pacts = getPacts(nationA);
        return pacts.stream()
                .anyMatch(p -> {
                    String target = p.getTargetNation(nationA.getName());
                    return target != null && target.equalsIgnoreCase(nationB.getName());
                });
    }

    public static void removePact(Nation nationA, Nation nationB) {
        List<pactObject> pacts = getPacts(nationA);
        pacts.removeIf(p -> {
            String target = p.getTargetNation(nationA.getName());
            return target != null && target.equalsIgnoreCase(nationB.getName());
        });
        savePacts(nationA, pacts);
    }

    public static void addOrUpdatePact(Nation nation, pactObject newPact) {
        List<pactObject> pacts = getPacts(nation);
        String ownNationName = nation.getName();

        pacts.removeIf(p -> {
            String target = p.getTargetNation(ownNationName);
            String newTarget = newPact.getTargetNation(ownNationName);
            return target != null && target.equalsIgnoreCase(newTarget);
        });

        pacts.add(newPact);
        savePacts(nation, pacts);
    }
}
