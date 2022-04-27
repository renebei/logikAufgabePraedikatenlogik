package relation;

import term.Term;
import term.Wert;

public interface InterRelation {
    public boolean berechnen(Term... terme);
}
