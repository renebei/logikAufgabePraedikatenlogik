package term;

import java.util.List;
import java.util.Set;

import aussagenlogik.Interpretation;

public class Wert extends Variable {
    //private VarTyp typ;
    private boolean booleanWert;
    private int integerWert;
    private String stringWert;

    public Wert(String n, boolean b) {
        super(n, VarTyp.BOOL);
        super.typ = VarTyp.BOOL;
        this.booleanWert = b;
        super.termTyp = TermTyp.KONSTANTE;
    }

    public Wert(String n, int i) {
        super(n, VarTyp.INT);
        super.typ = VarTyp.INT;
        this.integerWert = i;
        super.termTyp = TermTyp.KONSTANTE;
    }

    public Wert(String n, String s) {
        super(n, VarTyp.STRING);
        super.typ = VarTyp.STRING;
        this.stringWert = s;
        super.termTyp = TermTyp.KONSTANTE;
    }

    @Override
    public Wert evaluieren(Interpretation inter) {
        return inter.getWert(name);
    }

    @Override
    public String zeigen() {
        return this.toString();
    }

    @Override
    public List<Substitution> unifiziereMit(Term t) {
        //TODO
        return null;
    }

    public VarTyp getTyp() {
        return typ;
    }

    public void setTyp(VarTyp typ) {
        this.typ = typ;
    }

    @Override
    public List<Variable> variablen() {
        return List.of();
    }

    public boolean getBooleanWert() {
        if (this.typ != VarTyp.BOOL) {
            throw new IllegalArgumentException("falscher Zugriffstyp, erwartet " + this.typ + " abgefragt als BOOL");
        }
        return booleanWert;
    }

    public void setBooleanWert(boolean booleanWert) {
        this.typ = VarTyp.BOOL;
        this.booleanWert = booleanWert;
    }

    public int getIntegerWert() {
        if (this.typ != VarTyp.INT) {
            throw new IllegalArgumentException("falscher Zugriffstyp, erwartet " + this.typ + " abgefragt als INT");
        }
        return integerWert;
    }

    public void setIntegerWert(int integerWert) {
        this.typ = VarTyp.INT;
        this.integerWert = integerWert;
    }

    public String getStringWert() {
        if (this.typ != VarTyp.STRING) {
            throw new IllegalArgumentException("falscher Zugriffstyp, erwartet " + this.typ + " abgefragt als STRING");
        }
        return stringWert;
    }

    public void setStringWert(String stringWert) {
        this.typ = VarTyp.STRING;
        this.stringWert = stringWert;
    }

    @Override
    public String toString() {
        switch (this.typ) {
            case BOOL:
                return "" + this.booleanWert;
            case INT:
                return "" + this.integerWert;
            default:
                return this.stringWert;
        }
    }

}
