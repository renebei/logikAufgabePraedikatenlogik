package relation;

import java.util.*;

import aussagenlogik.Formel;
import aussagenlogik.Interpretation;
import aussagenlogik.Typ;
import term.Funktion;
import term.Term;
import term.Variable;
import term.Wert;

public class Exists extends Formel {
    private Variable var;

    public Exists(Variable var, Formel... formeln) {
        super(formeln);
        if (var == null) {
            throw new IllegalArgumentException("Variable bei FORALL darf nicht null sein.");
        }
        this.ersetzeVariablenGleichenNamens(var);
        this.var = var;
        super.typ = Typ.EXISTS;
    }

    public Formel praenexnormalformSchritt1() {
        if(operanden.get(0).variablen().contains(var)) {
            return this;
        }

        return operanden.get(0);
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

    public boolean evaluieren(Interpretation inter) {
        switch (this.getVar().getTyp()) {
            case BOOL:
                inter.setWert(new Wert(this.getVar().getName(), false));
                if (this.getOperanden().get(0).evaluieren(inter)) return true;

                inter.setWert(new Wert(this.getVar().getName(), true));
                if (this.getOperanden().get(0).evaluieren(inter)) return true;
                break;
            case INT:
                for (int i = -1000; i < 1000; i++) {
                    inter.setWert(new Wert(this.getVar().getName(), i));
                    if (this.getOperanden().get(0).evaluieren(inter)) return true;
                }
                break;
        }
        return false;
    }

    @Override
    public boolean validieren(List<Formel> operanden) {
        if (operanden == null || operanden.size() != 1) {
            throw new IllegalArgumentException("Exists benoetigt genau eine Formel: "
                    + operanden);
        }
        return true;
    }

    @Override
    public Formel praenexnormalformSchritt2(){
        Variable vneu = new Variable(String.valueOf(new Random().nextInt(10000)));
        this.operanden.get(0).substituierenTermFuerVariable(vneu,var);
        this.var = vneu;
        this.operanden.set(0,this.operanden.get(0).praenexnormalformSchritt2());
        return this;
    }

    @Override
    public String zeigen() {
        return "(\u2203" + this.var.zeigen() + " " + super.operanden.get(0).zeigen() + ")";
    }

    public Variable getVar() {
        return var;
    }

    public void setVar(Variable var) {
        this.var = var;
    }

    @Override
    public String toString() {
        return "Exists [var=" + var + ", typ=" + typ + ", operanden="
                + operanden + "]";
    }




    @Override
    public Formel skolemnormalform() {
        this.operanden.get(0).substituierenTermFuerVariable(new Wert("1", 1), var);
        return this.operanden.get(0).skolemnormalform();
    }

    @Override
    public Formel skolemnormalformhelp(Variable... variables) {
        substituierenTermFuerVariable(new Funktion("fun" + this.var.getName(), var.getTyp(), variables), var);

        //iwie die allquantoren hier so reinpacken oder so??????????????????ß idk
        //allquantoren gönnen sich in der oberklasse
        return this.operanden.get(0);
    }
}
