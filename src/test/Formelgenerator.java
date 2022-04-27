package test;

import java.util.ArrayList;
import java.util.List;

import aussagenlogik.Formel;
import aussagenlogik.Negation;
import aussagenlogik.Oder;
import aussagenlogik.Und;
import relation.Exists;
import relation.ForAll;
import relation.Relation;
import term.Funktion;
import term.Term;
import term.VarTyp;
import term.Variable;



public class Formelgenerator {
  private static int id;
  
  private static List<Variable> variablen = List.of(new Variable("a", VarTyp.INT)
      , new Variable("b", VarTyp.INT)
      , new Variable("c", VarTyp.INT), new Variable("d", VarTyp.INT)
      , new Variable("e", VarTyp.INT));
  
  private static List<Funktion> funktionen = List.of(new Funktion("f1", VarTyp.INT)
      , new Funktion("f2", VarTyp.INT)
      , new Funktion("f3", VarTyp.INT)
      , new Funktion("f4", VarTyp.INT)
      , new Funktion("f5", VarTyp.INT)
      );
  
  private static List<Relation> relationen = List.of( new Relation("r1")
      , new Relation("r2"), new Relation("r3"), new Relation("r4")
      , new Relation("r5")
      );
  
  public static Formel generate() {
     return generate(5);
  }
  
  public static Variable variable() {
    return variablen.get((int)(Math.random() * variablen.size()));
  }
  
  public static Funktion funktion() {
    //return funktionen.get((int)(Math.random() * funktionen.size()));
    return new Funktion("f"+(id++), VarTyp.INT);
  }
  
  public static Relation relation() {
    //Relation r = (Relation)relationen.get((int)(Math.random() * relationen.size())).deepClone();
    Relation r = new Relation("r"+(id++));
    int paramZahl = (int)(Math.random() * 4) + 1;
    List<VarTyp> pars = new ArrayList<>();
    List<Term> teilterme = new ArrayList<>();
    for(int i = 0; i < paramZahl; i++) {
      pars.add(VarTyp.INT);
      teilterme.add(term(3));
    }
    r.setStelligkeit(pars);
    r.setTerme(teilterme);
    return r;
  }
  
  public static Term term() {
    return term(3);
  }
  
  public static Term term(int ebene) {
    if (ebene <= 0 || Math.random() > 0.6) {
      return variable();
    }
    Funktion f = (Funktion)funktion().deepClone();
    int paramZahl = (int)(Math.random() * 4) + 1;
    List<VarTyp> pars = new ArrayList<>();
    List<Term> teilterme = new ArrayList<>();
    for(int i = 0; i < paramZahl; i++) {
      pars.add(VarTyp.INT);
      teilterme.add(term(ebene -1));
    }
    f.setStelligkeit(pars);
    f.setTeilterme(teilterme);
    return f;
  }

  
  public static Formel generate(int ebene) {
    if (ebene <= 0) {
      switch ((int)(Math.random() * 4)){
        case 0 : return relation();
        case 1 : return new Negation(relation());
        case 2 : return new Und(relation(), relation());
        case 3 : return new Oder(relation(), relation());
        default : throw new IllegalStateException("Generatorproblem"); 
      }     
    }
    switch ((int)(Math.random() * 5)){
      case 0 : return new Negation(generate(ebene - 1));
      case 1 : return new Und(generate(ebene - 1), generate(ebene - 1));
      case 2 : return new Oder(generate(ebene - 1), generate(ebene - 1));
      case 3 : return new Exists(variable(), generate(ebene - 1));
      case 4 : return new ForAll(variable(), generate(ebene - 1));
      default : throw new IllegalStateException("Generatorproblem"); 
    }
    
  }
  
  public static void main(String[] s) {
    for(int i = 0; i < 10; i++) {
      System.out.println(i + ": " + generate().zeigen());
    }
  }
}
