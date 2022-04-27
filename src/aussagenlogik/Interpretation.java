package aussagenlogik;

import java.util.HashMap;
import java.util.Map;

import relation.InterRelation;
import relation.Relation;
import term.InterFunktion;
import term.Wert;

public class Interpretation {

    private Map<String, Wert> werte;
    private Map<String, InterFunktion> funktionen;
    private Map<String, InterRelation> relationen;

    public Interpretation() {
        werte = new HashMap<>();
        funktionen = new HashMap<>();
        relationen = new HashMap<>();
    }

    public Interpretation setVariable(Wert wert) {
        werte.put(wert.getName(), wert);
        return this;
    }

    public Interpretation setFunktion(String name, InterFunktion f) {
        funktionen.put(name, f);
        return this;
    }

    public Interpretation setRelation(String name, InterRelation f) {
        relationen.put(name, f);
        return this;
    }

    public InterFunktion getFunktion(String name) {
        if (!funktionen.containsKey(name)) {
            throw new IllegalArgumentException();
        }
        return funktionen.get(name);
    }

    public InterRelation getRelation(String name) {
        if (!relationen.containsKey(name)) {
            throw new IllegalArgumentException();
        }
        return relationen.get(name);
    }

    public Wert getWert(String name) {
        if (!werte.containsKey(name)) {
            throw new IllegalArgumentException();
        }
        return werte.get(name);
    }
}
