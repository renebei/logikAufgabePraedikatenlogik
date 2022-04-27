package term;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import aussagenlogik.Formel;
import aussagenlogik.Interpretation;

public class Variable extends Term {
    protected String name;

    public Variable(String n, VarTyp v) {
        super(v);
        this.name = n;
        super.termTyp = TermTyp.VARIABLE;
        //this.typ = v;
    }

    public Variable(String n) {
        this(n, VarTyp.OPEN);
    }


    @Override
    public Wert evaluieren(Interpretation inter) {
        return inter.getWert(name);
    }

    @Override
    public List<Variable> variablen() {
        return List.of(this);
    }

    @Override
    public String zeigen() {
        return this.name;
    }

    @Override
    public List<Substitution> unifiziereMit(Term t) {
        //TODO
        return null;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "Variable [name=" + name + ", typ=" + typ + ", teilterme=" + teilterme + ", hashCode()=" + hashCode()
                + "]";
    }

    // kein equals und hashCode, da nur auf Identitaet geprueft wird


}
