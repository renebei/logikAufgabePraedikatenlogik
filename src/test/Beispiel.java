package test;

import aussagenlogik.Formel;
import relation.Exists;
import relation.Relation;
import term.Funktion;
import term.VarTyp;
import term.Variable;
import term.Wert;

public class Beispiel {
  public static void main(String[] args) {
    Variable v1 = new Variable("v1", VarTyp.INT);
    Variable v2 = new Variable("v2", VarTyp.INT);
    Wert w = new Wert("egal", 42);
    Funktion fu = new Funktion("f", VarTyp.INT, v1, w);
    Relation r = new Relation("P", fu, v2);
    Formel f = new Exists(new Variable("v1", VarTyp.INT), r);
    System.out.println(f.zeigen() + "\n" + f);
  }
}
