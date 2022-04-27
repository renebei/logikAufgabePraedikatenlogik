package relation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import aussagenlogik.Formel;
import aussagenlogik.Interpretation;
import aussagenlogik.Typ;
import term.Funktion;
import term.Substitution;
import term.Term;
import term.TermTyp;
import term.VarTyp;
import term.Variable;
import term.Wert;

public class Relation extends Formel {
    private String name;
    private List<Term> terme;
    private List<VarTyp> stelligkeit;

    public Relation(String name, Term... terme) {
        super();
        super.typ = Typ.RELATION;
        this.terme = List.of(terme);
        this.name = name;
        this.stelligkeit = new ArrayList<>();
        for (int i = 0; i < terme.length; i++) {
            this.stelligkeit.add(terme[i].getTyp());
        }
        try {
            this.variablennamenInTermenNurEinmal();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("darf nur eine Variable pro"
                    + " Namen in Relation geben: " + e.getMessage());
        }
        this.funktionsnamenNurEinmal();
    }

    @Override
    public boolean istLiteral() {
        return true;
    }

    @Override
    public List<Variable> variablen() {
        List<Variable> vars = new ArrayList<>();
        for (Term t : this.terme) {
            List<Variable> tmp = t.variablen();
            for (Variable v : tmp) {
                if (!vars.contains(v)) {
                    vars.add(v);
                }
            }
        }
        return vars;
    }

    public void variablennamenInTermenNurEinmal() {
        List<Variable> vars = this.variablen();

        Set<String> mehrfach = new HashSet<>();
        for (int i = 0; i < vars.size(); i++) {
            for (int j = i + 1; j < vars.size(); j++) {
                if (vars.get(i).getName().equals(vars.get(j).getName())) {
                    mehrfach.add(vars.get(i).getName());
                }
            }
        }
        if (!mehrfach.isEmpty()) {
            for (String n : mehrfach) {
                Variable erster = null;
                for (int i = 0; i < vars.size(); i++) {
                    if (vars.get(i).getName().equals(n)) {
                        if (erster == null) {
                            erster = vars.get(i);
                        } else {
                            for (Term t : this.terme) {
                                this.substituierenTermFuerVariable(erster, vars.get(i));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<String> funktionsnamen() {
        List<String> ergebnis = new ArrayList<>();
        for (Term t : this.terme) {
            ergebnis.addAll(t.funktionsnamen());
        }
        return ergebnis;
    }

    @Override
    public List<Funktion> alleFunktionen() {
        List<Funktion> ergebnis = new ArrayList<>();
        for (Term t : this.terme) {
            ergebnis.addAll(t.alleFunktionen());
        }
        return ergebnis;
    }

    @Override
    public List<Relation> alleRelationen() {
        return List.of(this);
    }

    @Override
    public List<String> relationsnamen() {
        return List.of(this.name);
    }

    @Override
    public void substituierenTermFuerVariable(Term neu, Variable alt) {
        if (alt.getTyp() == VarTyp.OPEN) {
            alt.setTyp(neu.getTyp());
        }
        if (neu.getTyp() == VarTyp.OPEN) {
            neu.setTyp(alt.getTyp());
        }
        if (alt.getTyp() != neu.getTyp()) {
            throw new IllegalArgumentException("Typ " + alt.getTyp() + " kann nicht durch "
                    + neu.getTyp() + " ersetzt werden.");
        }
        List<Term> neueTeilterme = new ArrayList<>();
        for (Term t : this.terme) {

            if (t.getTermTyp() == TermTyp.VARIABLE) {
                if (t == alt) {
                    neueTeilterme.add(neu);
                } else {
                    neueTeilterme.add(t);
                }
            } else {
                if (t.getTermTyp() == TermTyp.KONSTANTE) {
                    neueTeilterme.add(t);
                } else {
                    if (t.getTermTyp() == TermTyp.FUNKTION) {
                        t.substituierenTermFuerVariable(neu, alt);
                        neueTeilterme.add(t);
                    } else {
                        throw new IllegalArgumentException("Term muesste Funktion sein");
                    }
                }
            }
        }
        this.terme = neueTeilterme;
    }

    @Override
    public Set<Variable> frei() {
        Set<Variable> erg = new HashSet<>();
        for (Term t : this.terme) {
            erg.addAll(t.variablen());
        }
        return erg;
    }

    @Override
    public boolean evaluieren(Interpretation inter) {
    	Wert[] werteArray = new Wert[terme.size()];
		for (int i = 0; i < terme.size(); i++) {
            werteArray[i] = terme.get(i).evaluieren(inter);
		}
        return inter.getRelation(name).berechnen(werteArray);
    }

    @Override
    public boolean validieren(List<Formel> operanden) {
        return true;
    }

    @Override
    public String zeigen() {
        StringBuilder sb = new StringBuilder(this.name + "(");
        for (int i = 0; i < this.terme.size() - 1; i++) {
            sb.append(this.terme.get(i).zeigen() + ", ");
        }
        if (this.terme.size() > 0) {
            sb.append(this.terme
                    .get(this.terme.size() - 1)
                    .zeigen());
        }
        return sb.append(")").toString();
    }

    @Override
    public List<Substitution> unifiziereMit(Formel formel) {
        // TODO
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Term> getTerme() {
        return terme;
    }

    public void setTerme(List<Term> terme) {
        this.terme = terme;
    }

    public List<VarTyp> getStelligkeit() {
        return stelligkeit;
    }

    public void setStelligkeit(List<VarTyp> stelligkeit) {
        this.stelligkeit = stelligkeit;
    }

    @Override
    public String toString() {
        return "Relation [name=" + name + ", terme=" + terme + ", stelligkeit=" + stelligkeit + ", typ=" + typ
                + ", operanden=" + operanden + "]";
    }


//	public Funktion getFun() {
//		return fun;
//	}
//
//	public void setFun(Funktion fun) {
//		this.fun = fun;
//	}
//
//	public String getFunName() {
//		return funName;
//	}
//
//	public void setFunName(String funName) {
//		this.funName = funName;
//	}


}
