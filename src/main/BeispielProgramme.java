package main;

import aussagenlogik.Interpretation;
import aussagenlogik.Typ;
import aussagenlogik.Und;
import relation.InterRelation;
import relation.Relation;
import term.*;

public class BeispielProgramme {
    public static void main(String[] args) {
        //i)
        kleinerAls();

        //interpretation.setRelation("relation", )
        //ii)
    }

    private static void kleinerAls() {
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
        interpretation.setVariable(x);
        interpretation.setVariable(y);
        interpretation.setVariable(z);
        interpretation.setVariable(xF);
        interpretation.setVariable(yF);
        interpretation.setVariable(zF);


        System.out.println(undTrue.evaluieren(interpretation));
        System.out.println(undFalse.evaluieren(interpretation));
    }
}
