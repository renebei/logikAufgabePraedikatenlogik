package relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
        switch (this.getVar().getTyp()) {
            case BOOL:
                inter.setWert(new Wert(this.getVar().getName(), false));
                if(!this.getOperanden().get(0).evaluieren(inter)) return false;

                inter.setWert(new Wert(this.getVar().getName(), true));
                if(!this.getOperanden().get(0).evaluieren(inter)) return false;
                break;
            case INT:
                for(int i = -1000; i < 1000; i++) {
                    inter.setWert(new Wert(this.getVar().getName(), i));
                    if(!this.getOperanden().get(0).evaluieren(inter)) return false;
                }
                break;
        }
        return true;
    }

    public Formel praenexnormalformSchritt1() {
        if(operanden.get(0).variablen().contains(var)) {
            return this;
        }

        return operanden.get(0);
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


    @Override
    public Formel skolemnormalform() {
        if (operanden.get(0).getTyp() == Typ.EXISTS) {
            operanden.set(0,operanden.get(0).skolemnormalformhelp(var));
        } else if(operanden.get(0).getTyp() == Typ.FORALL){
            operanden.get(0).skolemnormalformhelp(var);
        }
        return this;
    }

    @Override
    public Formel skolemnormalformhelp(Variable... variables){
        Variable variable[] = new Variable[variables.length +1];
        for (int i = 0; i < variables.length; i++) {
            variable[i] = variables[i];
        }
        variable[variables.length] = var;
        return operanden.get(0).skolemnormalformhelp(variable);
    }
}
