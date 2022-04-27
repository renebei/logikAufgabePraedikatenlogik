package main;

import aussagenlogik.Interpretation;
import aussagenlogik.Negation;
import aussagenlogik.Oder;
import aussagenlogik.Und;
import relation.Exists;
import relation.ForAll;
import relation.InterRelation;
import relation.Relation;
import term.*;

public class BeispielProgramme {
    public static void main(String[] args) {
        //i)
        //erstesBsp();

        //zweitesBsp();
        beispielDrei();
        //interpretation.setRelation("relation", )
        //ii)
    }

    private static void erstesBsp() {
        InterRelation relation = werte -> {
            boolean lol = false;
            if(werte[0].getTyp() != VarTyp.INT || werte[1].getTyp() != VarTyp.INT){
                throw new IllegalArgumentException("keine Integer in diesem array");
            }

            if (werte[0].getIntegerWert() < werte[1].getIntegerWert()){
                return true;

            }
            return false;
        };

        Wert x = new Wert("x_true", 1);
        Wert y = new Wert("y_true", 2);
        Wert z = new Wert("z_true", 3);
        Und undTrue = new Und(new Relation("KleinerAls",x,z),
                new Relation("KleinerAls",y,z));

        Wert xF = new Wert("x_false", 1);
        Wert yF = new Wert("y_false", 8);
        Wert zF = new Wert("z_false", 3);
        Und undFalse = new Und(new Relation("KleinerAls",xF,zF),
                new Relation("KleinerAls",yF,zF));

        Interpretation interpretation = new Interpretation();
        interpretation.setRelation("KleinerAls", relation);
        interpretation.setWert(x);
        interpretation.setWert(y);
        interpretation.setWert(z);
        interpretation.setWert(xF);
        interpretation.setWert(yF);
        interpretation.setWert(zF);


        System.out.println(undTrue.evaluieren(interpretation));
        System.out.println(undFalse.evaluieren(interpretation));
    }

    private static void zweitesBsp() {
        InterRelation istZweiteZahl = werte -> {
            if(werte[0].getTyp() != VarTyp.INT || werte[1].getTyp() != VarTyp.INT){
                throw new IllegalArgumentException("keine Integer in diesem array");
            }
            if(werte[1].getIntegerWert() == 0) {
                return false;
            }
            return werte[0].getIntegerWert() / werte[1].getIntegerWert() == werte[0].getIntegerWert();
        };

        Wert x = new Wert("x", 5);
        Wert y = new Wert("y", 5);
        Wert z = new Wert("z", 5);


        Und inneresUnd = new Und(new Relation("istZweite",x,z),new Relation("istZweite",y,z));
        Und auesseresUnd = new Und(new Exists(z,inneresUnd),new ForAll(x,inneresUnd),new ForAll(y,inneresUnd));

        Interpretation interpretation = new Interpretation();
        interpretation.setRelation("istZweite",istZweiteZahl);
        interpretation.setWert(x);
        interpretation.setWert(y);
        interpretation.setWert(z);

        System.out.println(auesseresUnd.evaluieren(interpretation));
    }

    private static void beispielDrei() {
        InterRelation istGerade = werte -> {
            if (werte[0].getTyp() != VarTyp.INT && werte.length > 1) throw new IllegalArgumentException();
            return werte[0].getIntegerWert() % 2 == 0;
        };

        InterFunktion hoch2 = werte -> {
            if (werte[0].getTyp() != VarTyp.INT && werte.length > 1) throw new IllegalArgumentException();
            Wert w = new Wert("hoch2erg", werte[0].getIntegerWert() * werte[0].getIntegerWert());
            return w;
        };
        InterFunktion hoch2Plus1 = werte -> {
            if (werte[0].getTyp() != VarTyp.INT && werte.length > 1) throw new IllegalArgumentException();
            Wert w = new Wert("hoch2plus1erg", (werte[0].getIntegerWert() * werte[0].getIntegerWert()) + 1);
            return w;
        };

        Wert x = new Wert("x", 34);
        Wert y = new Wert("y", 34);


        ForAll fr = new ForAll(x, new Negation(new Und(new Relation("istGerade", new Funktion("hoch2",VarTyp.INT,x)), new Relation("istGerade", x))));
        ForAll fr2 = new ForAll(y, new Negation(new Und(new Relation("istGerade", new Funktion("hoch2Plus1",VarTyp.INT,y)), new Relation("istGerade", y))));

        Interpretation inter = new Interpretation();
        inter.setRelation("istGerade", istGerade);
        inter.setWert(x);
        inter.setWert(y);
        inter.setFunktion("hoch2", hoch2);
        inter.setFunktion("hoch2Plus1", hoch2Plus1);

        System.out.println(fr.evaluieren(inter));
        System.out.println(fr2.evaluieren(inter));
    }

    private static void viertesBeispiel() {
        InterRelation relation = werte -> {
            if (werte[0].getTyp() != VarTyp.INT || werte[1].getTyp() != VarTyp.INT || werte[2].getTyp() != VarTyp.INT) {
                throw new IllegalArgumentException("keine Integer in diesem array");
            }
            if ((werte[0].getIntegerWert() + werte[1].getIntegerWert() + werte[2].getIntegerWert() % 2) == 0){
                return true;
            }
            return false;
        };

        InterFunktion interFunktion = werte -> {
            if (werte[0].getTyp() != VarTyp.INT || werte[1].getTyp() != VarTyp.INT || werte[2].getTyp() != VarTyp.INT) {
                throw new IllegalArgumentException("keine Integer in diesem array");
            }

            return new Wert("ergebnis", werte[0].getIntegerWert() + werte[1].getIntegerWert() + werte[2].getIntegerWert());
        };

        Wert x = new Wert("4_x_true", 1);
        Wert y = new Wert("4_y_true", 8);
        Wert z = new Wert("4_z_true", 7);
        Oder oderTrue = new Oder(new Relation("gerade", new Funktion("f(x)", VarTyp.BOOL, x, y, z),
                new Funktion("f(x)", VarTyp.BOOL, y, z, x), new Funktion("f(x)", VarTyp.BOOL, z, x, y)));

        Wert xF = new Wert("4_x_false", 1);
        Wert yF = new Wert("4_y_false", 8);
        Wert zF = new Wert("4_z_false", 6);
        Oder oderFalse = new Oder(new Relation("ungerade", new Funktion("f(x)_", VarTyp.BOOL, xF, yF, zF),
                new Funktion("f(x)_", VarTyp.BOOL, yF, zF, xF), new Funktion("f(x)_", VarTyp.BOOL, zF, xF, yF)));

        Interpretation inter = new Interpretation();
        inter.setRelation("gerade", relation);
        inter.setRelation("ungerade", relation);
        inter.setFunktion("f(x)", interFunktion);
        inter.setFunktion("f(x)_", interFunktion);
        inter.setWert(x);
        inter.setWert(y);
        inter.setWert(z);
        inter.setWert(xF);
        inter.setWert(yF);
        inter.setWert(zF);

        System.out.println(oderTrue.evaluieren(inter));
        System.out.println(oderFalse.evaluieren(inter));
    }
}
