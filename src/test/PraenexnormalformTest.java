package test;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import aussagenlogik.Formel;
import aussagenlogik.Negation;
import aussagenlogik.Oder;
import aussagenlogik.Typ;
import aussagenlogik.Und;
import relation.Exists;
import relation.ForAll;
import relation.Relation;
import term.Funktion;
import term.VarTyp;
import term.Variable;
import term.Wert;

public class PraenexnormalformTest {

  private Variable vi;
  private Variable vi2;
  private Wert wi;
  private Funktion f1;
  private Relation r1;
  private Relation r2;

  @BeforeEach
  public void setUp() throws Exception {
    vi = new Variable("b", VarTyp.INT);
    vi2 = new Variable("b2", VarTyp.INT);
    wi = new Wert("wi", 0);
    f1 = new Funktion("f1", VarTyp.INT, vi, vi);
    r1 = new Relation("r1", f1, vi2, wi);
    r2 = new Relation("r2", f1, vi2, wi);
  }

  @Test
  public void testPraenexnormalformSchrittVier1() {
    Exists fu1 = new Exists(vi, r1);
    ForAll fu2 = new ForAll(vi2, r2);
    Formel fu3 = new Und(new Negation(fu1), fu2);
    fu3 = fu3.praenexnormalformSchritt4();
    Assertions.assertTrue(
        fu3.getTyp() == Typ.FORALL || fu3.getTyp() == Typ.EXISTS);
    Assertions.assertTrue(
        fu3.erste().getTyp() == Typ.FORALL
            || fu3.erste().getTyp() == Typ.EXISTS,
        "gefunden: " + fu3.erste().getTyp());
    Assertions.assertTrue(fu3.getTyp() != fu3.erste().getTyp());
    Formel tmp = fu3.erste().erste();
    Assertions.assertEquals(Typ.UND, tmp.getTyp());
    Assertions.assertEquals(Typ.NEGATION, tmp.erste().getTyp());
    Assertions.assertEquals(r1, tmp.erste().erste());
    Assertions.assertEquals(r2, tmp.zweite());
  }

  public void testPraenexnormalformSchrittVier2() {
    Exists fu1 = new Exists(vi, r1);
    ForAll fu2 = new ForAll(vi2, r2);
    Formel fu3 = new Exists(new Variable("q", VarTyp.INT),
        new Oder(new Negation(fu1), fu2));
    fu3 = new ForAll(new Variable("p", VarTyp.INT), fu3);
    fu3 = fu3.praenexnormalformSchritt4();
    Assertions.assertEquals(Typ.FORALL, fu3.getTyp());
    Assertions.assertEquals("p", ((ForAll) fu3).getVar().getName());
    Assertions.assertEquals(Typ.EXISTS, fu3.erste().getTyp());
    Assertions.assertEquals("q",
        ((Exists) fu3.erste()).getVar().getName());

    Formel fu33 = fu3.erste().erste();
    Assertions.assertTrue(
        fu33.getTyp() == Typ.FORALL || fu33.getTyp() == Typ.EXISTS);
    Assertions.assertTrue(
        fu33.erste().getTyp() == Typ.FORALL
            || fu33.erste().getTyp() == Typ.EXISTS,
        "gefunden: " + fu33.erste().getTyp());
    Assertions.assertTrue(fu33.getTyp() != fu33.erste().getTyp());
    Formel tmp = fu33.erste().erste();
    Assertions.assertEquals(Typ.UND, tmp.getTyp());
    Assertions.assertEquals(Typ.NEGATION, tmp.erste().getTyp());
    Assertions.assertEquals(r1, tmp.erste().erste());
    Assertions.assertEquals(r2, tmp.zweite());
  }

  @Test
  public void testPraenexnormalformSchrittDrei1() {
    Exists fu1 = new Exists(vi, r1);
    ForAll fu2 = new ForAll(vi2, r2);
    Formel fu3 = new Negation(new Und(new Negation(fu1), fu2));
    fu3 = fu3.praenexnormalformSchritt3();
    Assertions.assertEquals(Typ.ODER, fu3.getTyp());
    Assertions.assertEquals(fu1, fu3.getOperanden().get(0));
    Assertions.assertEquals(Typ.EXISTS,
        fu3.getOperanden().get(1).getTyp());
    Assertions.assertEquals(Typ.NEGATION,
        fu3.getOperanden().get(1).getOperanden().get(0).getTyp());
    Assertions.assertEquals(fu2.getOperanden().get(0),
        fu3.getOperanden().get(1).getOperanden().get(0).getOperanden()
            .get(0));
  }

  @Test
  public void testPraenexnormalformSchrittDrei2() {
    Formel fu1 = new ForAll(vi, r1);
    Formel fu2 = new Exists(vi2, r2);
    Formel fu3 = new Negation(new Oder(new Negation(fu1), fu2));
    fu3 = fu3.praenexnormalformSchritt3();
    Assertions.assertEquals(Typ.UND, fu3.getTyp());
    Assertions.assertEquals(fu1, fu3.getOperanden().get(0));
    Assertions.assertEquals(Typ.FORALL,
        fu3.getOperanden().get(1).getTyp());
    Assertions.assertEquals(Typ.NEGATION,
        fu3.getOperanden().get(1).getOperanden().get(0).getTyp());
    Assertions.assertEquals(fu2.getOperanden().get(0),
        fu3.getOperanden().get(1).getOperanden().get(0).getOperanden()
            .get(0));
  }

  @Test
  public void testPraenexnormalformSchrittZwei1() {
    Variable vv = new Variable(vi.getName(), VarTyp.INT);
    Exists fu1 = new Exists(vv, r1);
    Variable vw = new Variable(vi.getName(), VarTyp.INT);
    ForAll fu2 = new ForAll(vw, r2.deepClone());
    Formel fu3 = new Und(fu1, fu2);
    fu3 = fu3.praenexnormalformSchritt2();
    Variable vvneu = ((Exists) fu3.getOperanden().get(0)).getVar();
    Variable vwneu = ((ForAll) fu3.getOperanden().get(1)).getVar();
    Assertions.assertNotEquals(vvneu, vv);
    Assertions.assertNotEquals(vwneu, vw);
    Assertions.assertNotEquals(vvneu, vwneu);
    Assertions.assertSame(f1.getTeilterme().get(0), vvneu);
    Assertions.assertSame(f1.getTeilterme().get(1), vvneu);
    Relation r2neu = (Relation) (fu2.getOperanden().get(0));
    Assertions.assertSame(
        r2neu.getTerme().get(0).getTeilterme().get(0), vwneu);
    Assertions.assertSame(
        r2neu.getTerme().get(0).getTeilterme().get(1), vwneu);
  }

  @Test
  public void testPraenexnormalformSchrittZwei2() {
    Variable vv = new Variable(vi.getName(), VarTyp.INT);
    Exists fu1 = new Exists(vv, r1);
    // Variable vw = new Variable(vi.getName(), VarTyp.INT);
    ForAll fu2 = new ForAll(vv, r2.deepClone());
    Formel fu3 = new Und(fu1, fu2);
    fu3 = fu3.praenexnormalformSchritt2();
    Variable vvneu = ((Exists) fu3.getOperanden().get(0)).getVar();
    Variable vwneu = ((ForAll) fu3.getOperanden().get(1)).getVar();
    Assertions.assertNotEquals(vvneu, vv);
    Assertions.assertNotEquals(vwneu, vv);
    Assertions.assertNotEquals(vvneu, vwneu);
    Assertions.assertSame(f1.getTeilterme().get(0), vvneu);
    Assertions.assertSame(f1.getTeilterme().get(1), vvneu);
    Relation r2neu = (Relation) (fu2.getOperanden().get(0));
    Assertions.assertSame(
        r2neu.getTerme().get(0).getTeilterme().get(0), vwneu);
    Assertions.assertSame(
        r2neu.getTerme().get(0).getTeilterme().get(1), vwneu);
  }

  @Test
  public void testPraenexnormalformSchrittZwei3() {
    Variable vv = new Variable(vi.getName(), VarTyp.INT);
    Formel fu1 = new Exists(vv, r1);
    // Variable vw = new Variable("blubb", VarTyp.INT);
    Exists fu2 = new Exists(vi2, r2.deepClone());
    Formel fu3 = new Und(fu1, fu2);
    fu3 = fu3.praenexnormalformSchritt2();
    Assertions.assertEquals(fu3.getOperanden().get(0), fu1);
    Assertions.assertEquals(fu3.getOperanden().get(1), fu2);
  }
  
  @Test
  public void testPraenexnormalformSchrittZwei4() {
    Formel f1 = new ForAll(new Variable("a", VarTyp.INT),
        new Relation("r", new Variable("a", VarTyp.INT)));
    Formel f2 = f1.deepClone();
    Formel sut = new Und(f1,f2);
    
    Formel erg = sut.praenexnormalformSchritt2();
    ForAll u1 = (ForAll)erg.erste();
    ForAll u2 = (ForAll)erg.zweite();
    String u1s = u1.getVar().getName();
    String u2s = u2.getVar().getName();
    Assertions.assertNotEquals(u1s, u2s);
    Assertions.assertEquals(u1s
        , ((Variable)((Relation)u1.erste()).getTerme().get(0)).getName());
    Assertions.assertEquals(u2s
        , ((Variable)((Relation)u2.erste()).getTerme().get(0)).getName());
  }

  @Test
  public void testPraenexnormalformSchrittEins1() {
    Variable vv = new Variable(vi.getName(), VarTyp.INT);
    Formel fu1 = new ForAll(vv, r1);
    Variable vw = new Variable(vi.getName(), VarTyp.INT);
    ForAll fu2 = new ForAll(vw, r2.deepClone());
    Formel fu3 = new Und(fu1, fu2);
    Variable vx = new Variable(vi.getName(), VarTyp.INT);
    // Variablen mit vi.getName() sind gebunden, sollte keine Umbenennung geben
    Formel fu4 = new Exists(vx, fu3);
    // aeusserer EXISTS ist ueberfluessig
    // (∃b ((∃b r1(f1(b, b), b2, 0)) AND (∃b r2(f1(b, b), b2, 0))))
    fu4 = fu4.praenexnormalformSchritt1();
    Assertions.assertEquals(fu3, fu4);
  }

  @Test
  public void testPraenexnormalformSchrittEins2() {
    Variable vv = new Variable(vi.getName(), VarTyp.INT);
    Formel fu1 = new Exists(vv, r1);
    Variable vw = new Variable("blubb", VarTyp.INT);
    Exists fu2 = new Exists(vw, r2.deepClone());
    Formel fu3 = new Und(fu1, fu2);
    // ((∃b r1(f1(b, b), b2, 0) AND (∃blubb r2(f1(b, b), b2, 0))
    fu3 = fu3.praenexnormalformSchritt1();
    Assertions.assertEquals(fu3.getOperanden().get(0), fu1);
    Assertions.assertEquals(fu3.getOperanden().get(1),
        fu2.getOperanden().get(0));
  }

  @Test
  public void testPraenexnormalformSchrittEins3() {
    Formel f = new ForAll(new Variable("a", VarTyp.INT),
        new Relation("r", new Variable("b", VarTyp.INT)));
    Formel fu3 = f.praenexnormalformSchritt1();
    Assertions.assertEquals(fu3, f.getOperanden().get(0));
    Assertions.assertTrue(this.inPraenexnormalform(fu3));
  }
  
  @Test
  public void testPraenexnormalformSchrittEins4() {
    Formel f1 = new ForAll(new Variable("a", VarTyp.INT),
        new Relation("r", new Variable("a", VarTyp.INT)));
    Formel f2 = f1.deepClone();
    Formel sut = new Und(f1,f2);
    
    Formel erg = sut.praenexnormalformSchritt1();
    ForAll u1 = (ForAll)erg.erste();
    ForAll u2 = (ForAll)erg.zweite();
    Variable u1v = u1.getVar();
    Variable u2v = u2.getVar();
    Assertions.assertEquals(u1v.getName(), u2v.getName());
    Assertions.assertEquals(u1v
        , ((Variable)((Relation)u1.erste()).getTerme().get(0)));
    Assertions.assertEquals(u2v
        , ((Variable)((Relation)u2.erste()).getTerme().get(0)));
  }

  private boolean inPraenexnormalform(Formel f) {
    Formel tmp = f;
    while (tmp.getTyp() == Typ.EXISTS || tmp.getTyp() == Typ.FORALL) {
      List<Variable> gebunden = tmp.gebunden();
      for (int i = 0; i < gebunden.size() - 1; i++) {
        for (int j = i + 1; j < gebunden.size(); j++) {
          if (gebunden.get(i).getName()
              .equals(gebunden.get(j).getName())) {
            throw new IllegalStateException("Variable "
                + gebunden.get(i).getName()
                + "  mehrfach gebunden in "
                + tmp.zeigen());
          }
        }
      }

      Variable var = null;
      if (tmp.getTyp() == Typ.EXISTS) {
        var = ((Exists) tmp).getVar();
      }
      if (tmp.getTyp() == Typ.FORALL) {
        var = ((ForAll) tmp).getVar();
      }
      tmp = tmp.getOperanden().get(0);
      if (!this.hatVariablenname(tmp.frei(), var.getName())) {
        System.out.println("Ueberfluessiger Quantor vor: "
                + tmp.zeigen() + " in\n  " + f.zeigen());
        tmp = tmp.praenexnormalformSchritt1();
        
        if (!this.hatVariablenname(tmp.frei(), var.getName())) {
        throw new IllegalStateException(
            "Ueberfluessiger Quantor vor: "
                + tmp.zeigen() + " in\n  " + f.zeigen());
        }
      }
    }
    return this.keinQuantor(tmp);
  }

  private boolean keinQuantor(Formel f) {
    if (f.getTyp() == Typ.EXISTS || f.getTyp() == Typ.FORALL) {
      return false;
    }
    for (Formel tf : f.getOperanden()) {
      if (!this.keinQuantor(tf)) {
        return false;
      }
    }
    return true;
  }

  @Test
  public void testMitZufallsformeln() {
    for (int i = 0; i < 50; i++) {
      Formel sut = Formelgenerator.generate(6);
      Formel erg = sut.deepClone()
          .praenexnormalformSchritt1()
          .praenexnormalformSchritt2()
          .praenexnormalformSchritt3()
          .praenexnormalformSchritt4();
      Assertions.assertTrue(this.inPraenexnormalform(erg),
          sut.zeigen() + "\n nicht in Praenexnormalform\n "
              + erg.zeigen() + "\n");
    }
  }

  private boolean hatVariablenname(Collection<Variable> vars,
      String name) {
    for (Variable v : vars) {
      if (v.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }
}
