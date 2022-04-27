package relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import aussagenlogik.Formel;
import aussagenlogik.Interpretation;
import aussagenlogik.Typ;
import term.Variable;
import term.Wert;
import term.Term;
import term.VarTyp;

public class ForAll extends Formel {
    private Variable var;

    public ForAll(Variable var, Formel... formeln) {
        super(formeln);
        if (var == null) {
            throw new IllegalArgumentException("Variable bei FORALL darf nicht null sein.");
        }
        this.var = var;
        this.ersetzeVariablenGleichenNamens(var);

        super.typ = Typ.FORALL;
    }

    private void ersetzeVariablenGleichenNamens(Variable var) {
        Formel rumpf = this.operanden.get(0);
        List<Variable> zuErsetzen = new ArrayList<>();
        for (Variable v : rumpf.frei()) {
            if (v != var && v.getName().equals(var.getName())) {
                zuErsetzen.add(v);
            }
        }
        for (Variable v : zuErsetzen) {
            rumpf.substituierenTermFuerVariable(var, v);
        }
    }

    @Override
    public Set<Variable> frei() {
        Set<Variable> erg = this.operanden.get(0).frei();
        erg.remove(this.var);
        return erg;
    }

    @Override
    public List<Variable> gebunden() {
        List<Variable> erg = this.operanden.get(0).gebunden();
        erg.add(this.var);
        return erg;
    }

    @Override
    public void substituierenTermFuerVariable(Term neu, Variable alt) {
        if (alt != this.var) {
            this.operanden.get(0).substituierenTermFuerVariable(neu, alt);
        }
    }

    @Override
    public boolean evaluieren(Interpretation inter) {
	    return false;
    }

    @Override
    public boolean validieren(List<Formel> operanden) {
        if (operanden == null || operanden.size() != 1) {
            throw new IllegalArgumentException("Forall benoetigt genau eine Formel: "
                    + operanden);
        }
        return true;
    }

    @Override
    public String zeigen() {
        return "(\u2200" + this.var.zeigen() + " " + super.operanden.get(0).zeigen() + ")";
    }

    public Variable getVar() {
        return var;
    }

    public void setVar(Variable var) {
        this.var = var;
    }

    @Override
    public String toString() {
        return "ForAll [var=" + var + ", typ=" + typ + ", operanden="
                + operanden + "]";
    }
}