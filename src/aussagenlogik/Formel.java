package aussagenlogik;

import java.util.*;

import relation.Exists;
import relation.ForAll;
import relation.Relation;
import term.Funktion;
import term.Substitution;
import term.Term;
import term.VarTyp;
import term.Variable;
import term.Wert;
import util.DeepClone;

public abstract class Formel implements DeepClone<Formel> {
    private static final long serialVersionUID = 1L;
    protected Typ typ;
    protected List<Formel> operanden;

    protected Formel(Formel... formeln) {
        super();
        var tmp = new ArrayList<Formel>();
        for (Formel f : formeln) {
            tmp.add(f);
        }

        this.setOperanden(tmp);
        this.variablennamenNurEinmal();
        this.relationsnamenNurEinmal();
    }

    public Formel(Typ typ, Formel... formeln) {
        this(formeln);
        this.typ = typ;
        this.funktionsnamenNurEinmal();
        this.relationsnamenNurEinmal();
    }

    public abstract boolean evaluieren(Interpretation inter);

    public boolean istLiteral() {
        return false;
    }

    public abstract boolean validieren(List<Formel> operanden);

    public abstract String zeigen();

    /**
     * Methode zum vereinfachten Zugriff auf den ersten Operanden,
     * ist er nicht vorhanden, wird eine Exception geworfen.
     *
     * @return erster Operand
     */
    public Formel erste() {
        Formel erg = this.operanden.get(0);
        if (erg == null) {
            throw new IllegalArgumentException("erste Teilformel nicht gefunden: "
                    + this.zeigen());
        }
        return erg;
    }

    /**
     * Methode zum vereinfachten Zugriff auf den ersten Operanden,
     * ist er nicht vorhanden, wird eine Exception geworfen.
     *
     * @return zweiter Operand
     */
    public Formel zweite() {
        Formel erg = this.operanden.get(1);
        if (erg == null) {
            throw new IllegalArgumentException("zweite Teilformel nicht gefunden: "
                    + this.zeigen());
        }
        return erg;
    }

    /**
     * Liste der Variablen der Formel in der Reihenfolge in der sie
     * in der Formel vorkommen. Die Reihenfolge wird bei der Unifikation
     * benoetigt
     *
     * @return Variablen der Formel in der Reihenfolge, in der sie
     * vorkommen.
     */
    public List<Variable> variablen() { // List, da Reihenfolge benoetigt wird
        List<Variable> ergebnis = new ArrayList<>();
        for (Formel t : this.operanden) {
            List<Variable> tmp = t.variablen();
            for (Variable v : tmp) {
                if (!ergebnis.contains(v)) {
                    ergebnis.add(v);
                }
            }
        }
        return ergebnis;
    }

    private void variablennamenNurEinmal() {
        List<Variable> vars = new ArrayList<>();
        for (Formel t : this.operanden) {
            List<Variable> tmp = t.variablen();
            for (Variable v : tmp) {
                if (!vars.contains(v)) {
                    vars.add(v);
                }
            }
        }

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
                            this.substituierenTermFuerVariable(erster, vars.get(i));
                        }
                    }
                }
            }
        }
    }

    /**
     * Prueft, ob die Kombination aus Funktionsnamen und Stelligkeit
     * nur einmal vorkommt. Wenn nicht, wird eine Exception geworfen.
     */
    protected void funktionsnamenNurEinmal() {
        List<Funktion> fkt = this.alleFunktionen();
        for (int i = 0; i < fkt.size(); i++) {
            for (int j = i + 1; j < fkt.size(); j++) {
                if (fkt.get(i).getName().equals(fkt.get(j).getName())
                        &&
                        !fkt.get(i).gleicheStelligkeit(fkt.get(j))
                ) {
                    throw new IllegalArgumentException("darf nicht zwei Funktionen "
                            + "mit gleichem Namen " + fkt.get(i).getName()
                            + " und unterschiedlicher Stelligkeit geben. "
                            + fkt.get(i).getStelligkeit() + " - " + fkt.get(j).getStelligkeit());
                }
            }
        }
    }

    /**
     * Prueft, ob die Kombination aus Relationsnamen und Stelligkeit
     * nur einmal vorkommt. Wenn nicht, wird eine Exception geworfen.
     */
    protected void relationsnamenNurEinmal() {
        List<Relation> fkt = this.alleRelationen();
        for (int i = 0; i < fkt.size(); i++) {
            for (int j = i + 1; j < fkt.size(); j++) {
                if (fkt.get(i).getName().equals(fkt.get(j).getName())
                        &&
                        !fkt.get(i).gleicheStelligkeit(fkt.get(j))
                ) {
                    throw new IllegalArgumentException("darf nicht zwei Relationen "
                            + "mit gleichem Namen " + fkt.get(i).getName() + " und unterschiedlicher Stelligkeit geben.");
                }
            }
        }
    }

    public boolean gleicheStelligkeit(Relation f1) {
        return true;
    }

    /**
     * Alle freien Vorkommen der Variablen alt werden durch den Term neu ersetzt.
     *
     * @param neu neuer Term
     * @param alt zu ersetzende Variable
     */
    public void substituierenTermFuerVariable(Term neu, Variable alt) {
        if (alt.getTyp() == VarTyp.OPEN) {
            alt.setTyp(neu.getTyp());
        }
        if (alt.getTyp() != neu.getTyp()) {
            throw new IllegalArgumentException("Typ " + alt.getTyp() + " kann nicht durch "
                    + neu.getTyp() + " ersetzt werden.");
        }
        for (Formel f : this.operanden) {
            f.substituierenTermFuerVariable(neu, alt);
        }
    }

    public List<Substitution> unifiziereMit(Formel formel) {
        // TODO
        return null;
    }

    /**
     * Berechnet die Menge aller in der Formel frei vorkommenden Variablen.
     *
     * @return Menge der frei vorkommenden Variablen der Formel
     */
    public Set<Variable> frei() {
        Set<Variable> erg = new HashSet<>();
        for (Formel f : this.operanden) {
            erg.addAll(f.frei());
        }
        return erg;
    }

    /**
     * Berechnet die Liste aller in der Formel gebunden vorkommenden Variablen.
     * Da ein Variablenname oder ein Variablenobjekt mehrfach genutzt werden
     * kann, ist das Ergebnis eine Liste, in der Objekte mit gleichen Namen und
     * identische Objekte mehrfach vorkommen koennen.
     *
     * @return Liste der gebunden vorkommenden Variablen der Formel
     */
    public List<Variable> gebunden() {
        List<Variable> erg = new ArrayList<>();
        for (Formel f : this.operanden) {
            erg.addAll(f.gebunden());
        }
        return erg;
    }

    public Formel praenexnormalformSchritt1() {
        for (int i = 0; i < operanden.size(); i++) {
            if (operanden.get(i).typ == Typ.FORALL || operanden.get(i).typ == Typ.EXISTS) {
                operanden.set(i, operanden.get(i).praenexnormalformSchritt1());
            }
        }
        return this;
    }


    // nutzbar als eindeutiger Anhang fuer Variablennamen
    private static int id;

    public static int nextId() {
        return id++;
    }


    /*
    public Formel praenexnormalformSchritt2() {
        Formel clone = this.deepClone();
        Set<Variable> frei = clone.frei();
        List<Variable> gebunden = clone.gebunden();
        //Set<Variable> freiClone = new HashSet<>();
        //List<Variable> gebundenClone = new ArrayList<>();


        for (Variable v : gebunden) {
            v.setName(String.valueOf(new Random().nextInt(10000)));
        }

        for (Variable v : frei) {
            //Variable neuV = new Variable(String.valueOf(new Random().nextInt(10000)));
            //freiClone.add(neuV);
            v.setName(String.valueOf(new Random().nextInt(10000)));
        }
        return clone;
    }*/

    public Formel praenexnormalformSchritt2(){
        if(this.typ != Typ.RELATION){
            for (int i = 0; i < operanden.size(); i++){
                operanden.set(i,operanden.get(i).praenexnormalformSchritt2());
            }
        }
        return this;
    }


    public Formel praenexnormalformSchritt3() {
        Formel zwischenstand = this.deepClone();
        boolean fertig = false;
        while (!fertig) {
            Formel aktuell = zwischenstand.deepClone();
            if (aktuell.typ == Typ.NEGATION)
                aktuell = negieren(aktuell);
            for (int i = 0; i < aktuell.operanden.size(); i++)
                aktuell.operanden.set(i, aktuell.operanden.get(i).praenexnormalformSchritt3());
            if (zwischenstand.equals(aktuell))
                fertig = true;
            else
                zwischenstand = aktuell;
        }

        return zwischenstand;
    }

    private Formel negieren(Formel aktuell) {
        List<Formel> uebernehmen = new ArrayList<>();
        for (Formel operand : aktuell.operanden.get(0).operanden)
            uebernehmen.add(new Negation(operand));
        Formel[] tmp = new Formel[uebernehmen.size()];

        if (aktuell.operanden.get(0).typ == Typ.UND)
            aktuell = new Oder(uebernehmen.toArray(tmp));
        else if (aktuell.operanden.get(0).typ == Typ.ODER)
            aktuell = new Und(uebernehmen.toArray(tmp));
        else if (aktuell.operanden.get(0).typ == Typ.NEGATION)
            aktuell = aktuell.operanden.get(0).operanden.get(0);
        else if (aktuell.operanden.get(0).typ == Typ.FORALL) {
            ForAll temp = (ForAll) aktuell.operanden.get(0);
            aktuell = new Exists(temp.getVar(), uebernehmen.toArray(tmp));
        } else if (aktuell.operanden.get(0).typ == Typ.EXISTS) {
            Exists temp = (Exists) aktuell.operanden.get(0);
            aktuell = new ForAll(temp.getVar(), uebernehmen.toArray(tmp));
        }
        return aktuell;
    }


    public Formel praenexnormalformSchritt4() {
        Formel erg = this;
        Boolean fix = true;
        while (fix) {
            Formel tmp = schritt4(erg);
            if (!tmp.equals(erg)) {
                fix = true;
                erg = tmp;
            } else {
                fix = false;
            }
        }
        return erg;
    }

    private Formel schritt4(Formel form) {
        Formel formel = form.deepClone();
        if (formel.typ == Typ.UND || formel.typ == Typ.ODER || formel.typ == Typ.NEGATION) {
            for (int i = 0; i < formel.operanden.size(); i++) {
                Formel operand = formel.operanden.get(i);
                if (formel.schritt4machbar(operand)) {
                    if (operand.typ == Typ.EXISTS) {
                        Exists ex = (Exists) operand;
                        formel.operanden.set(i, operand.operanden.get(0));
                        formel = new Exists(ex.getVar(), formel);
                    } else {
                        ForAll fa = (ForAll) operand;
                        formel.operanden.set(i, operand.operanden.get(0));
                        formel = new ForAll(fa.getVar(), formel);
                    }
                    return formel;
                }
            }
        }
        for (int i = 0; i < formel.operanden.size(); i++) {
            formel.operanden.set(i, schritt4(formel.operanden.get(i)));

        }
        return formel;
    }

    private Boolean schritt4machbar(Formel operand) {
        Variable var;
        switch (operand.typ) {
            case EXISTS:
                Exists ex = (Exists) operand;
                var = ex.getVar();
                break;
            case FORALL:
                ForAll fa = (ForAll) operand;
                var = fa.getVar();
                break;
            default:
                return false;
        }
        for (Formel andererOperand : this.operanden) {
            if (operand != andererOperand) {
                if (andererOperand.gebunden().contains(var)) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<String> funktionsnamen() {
        List<String> ergebnis = new ArrayList<>();
        for (Formel t : this.operanden) {
            ergebnis.addAll(t.funktionsnamen());
        }
        return ergebnis;
    }

    public List<Funktion> alleFunktionen() {
        List<Funktion> ergebnis = new ArrayList<>();
        for (Formel t : this.operanden) {
            ergebnis.addAll(t.alleFunktionen());
        }
        return ergebnis;
    }

    public List<Relation> alleRelationen() {
        List<Relation> ergebnis = new ArrayList<>();
        for (Formel t : this.operanden) {
            ergebnis.addAll(t.alleRelationen());
        }
        return ergebnis;
    }

    public List<String> relationsnamen() {
        List<String> ergebnis = new ArrayList<>();
        for (Formel t : this.operanden) {
            ergebnis.addAll(t.relationsnamen());
        }
        return ergebnis;
    }

    private String laengesterFunktionsname() {
        String max = "";
        for (String s : this.funktionsnamen()) {
            if (s.length() > max.length()) {
                max = s;
            }
        }
        return max;
    }


    //angenommen: diese funktion wird am ??u??erten element aufgerufen
    public Formel skolemnormalform() {
        /*
        if (this.typ != Typ.EXISTS || this.typ != Typ.FORALL) {
            for (Formel f : operanden) {
                f.skolemnormalform();
            }
        }*/
        return this;
    }
/*
    //angenommen: diese funktion wird am ??u??erten element aufgerufen
    //jeder hat nur einen operanden!
    public Formel skolemnormalform() {
        return this;
    }*/

    public Formel skolemnormalformhelp(Variable... variables) {
        return this;
    }

    public Formel cleanUpExists(){
        return this;
    }


    public void addOperand(Formel f) {
        this.operanden.add(f);
        this.validieren(this.operanden);
    }

    public Typ getTyp() {
        return typ;
    }

    public void setTyp(Typ typ) {
        this.typ = typ;
    }

    public List<Formel> getOperanden() {
        return operanden;
    }

    public void setOperanden(List<Formel> operanden) {
        this.validieren(operanden);
        this.operanden = operanden;
    }

    @Override
    public int hashCode() {
        return Objects.hash(operanden, typ);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Formel other = (Formel) obj;
        return Objects.equals(operanden, other.operanden) && typ == other.typ;
    }

    @Override
    public String toString() {
        return "Formel [typ=" + typ + ", operanden=" + operanden + "]";
    }

}
