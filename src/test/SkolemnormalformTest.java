package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import aussagenlogik.Formel;
import aussagenlogik.Typ;
import relation.Exists;
import relation.ForAll;
import relation.Relation;
import term.Funktion;
import term.Term;
import term.VarTyp;
import term.Variable;
import term.Wert;

public class SkolemnormalformTest {

	private Variable vi;
	private Variable vi2;

	@BeforeEach
	public void setUp() throws Exception {
		vi = new Variable("b", VarTyp.INT);
		vi2 = new Variable("b2", VarTyp.INT);
	}
	
	@Test
	public void testskolemnormalform1() {
		Variable y = new Variable("y", VarTyp.INT);
		Variable x = new Variable("x", VarTyp.INT);
		Variable u = new Variable("u", VarTyp.INT);
		Variable v = new Variable("v", VarTyp.INT);
		Variable w = new Variable("w", VarTyp.INT);
		// (∃y (∀x (∀b2 (∃w (∃u (∀v (∃b rel(b, b2, u, v, w, x, y))))))))
		Formel fu1 = new Exists(vi, new Relation("rel", vi, vi2, u, v, w, x, y));
		Formel fu2 = new ForAll(vi2, new Exists(w, new Exists(u, new ForAll(v, fu1))));
		Formel fu3 = new Exists(y,new ForAll(x, fu2));
		Formel fu4 = fu3.skolemnormalform();
		// erwartet: (∀x (∀b2 (∀v rel(funb4(x, b2, v), b2, funu3(x, b2), v, funw2(x, b2), x, 1))))
		Assertions.assertEquals(Typ.FORALL, fu4.getTyp());
		Assertions.assertEquals(Typ.FORALL, fu4.erste().getTyp());
		Assertions.assertEquals(Typ.FORALL, fu4.erste().erste().getTyp());
		Assertions.assertEquals(Typ.RELATION, fu4.erste().erste().erste().getTyp());
		ForAll tmp = (ForAll) fu4;
		Assertions.assertEquals(x, tmp.getVar());
		tmp = (ForAll)fu4.erste();
		Assertions.assertEquals(vi2, tmp.getVar());
		tmp = (ForAll)tmp.erste();
		Assertions.assertEquals(v, tmp.getVar());
		Relation tmpr = (Relation)tmp.erste();
		Assertions.assertEquals("rel", tmpr.getName());
		Assertions.assertEquals(7, tmpr.getTerme().size());
		Term tmpt = tmpr.getTerme().get(0);
		Assertions.assertEquals(VarTyp.INT, tmpt.getTyp());
		Assertions.assertEquals(3, tmpt.getTeilterme().size());
		// Reihenfolge egal
		Term tmp0 = tmpt.getTeilterme().get(0);
		Term tmp1 = tmpt.getTeilterme().get(1);
		Term tmp2 = tmpt.getTeilterme().get(2);
		Assertions.assertTrue(tmp0 == x || tmp1 == x || tmp2 == x);
		Assertions.assertTrue(tmp0 == vi2 || tmp1 == vi2 || tmp2 == vi2);
		Assertions.assertTrue(tmp0 == v || tmp1 == v || tmp2 == v);
		Assertions.assertTrue(tmp0 != tmp1 && tmp1 != tmp2 && tmp2 != tmp0);
		
		tmpt = tmpr.getTerme().get(1);
		Assertions.assertEquals(vi2, tmpt);
		
		tmpt = tmpr.getTerme().get(2);
		Assertions.assertEquals(VarTyp.INT, tmpt.getTyp());
		Assertions.assertEquals(2, tmpt.getTeilterme().size());
		// Reihenfolge egal
		tmp0 = tmpt.getTeilterme().get(0);
		tmp1 = tmpt.getTeilterme().get(1);
		Assertions.assertTrue(tmp0 == x || tmp1 == x);
		Assertions.assertTrue(tmp0 == vi2 || tmp1 == vi2);
		Assertions.assertTrue(tmp0 != tmp1);
		
		tmpt = tmpr.getTerme().get(3);
		Assertions.assertEquals(v, tmpt);
		
		tmpt = tmpr.getTerme().get(4);
		Assertions.assertEquals(VarTyp.INT, tmpt.getTyp());
		Assertions.assertEquals(2, tmpt.getTeilterme().size());
		// Reihenfolge egal
		tmp0 = tmpt.getTeilterme().get(0);
		tmp1 = tmpt.getTeilterme().get(1);
		Assertions.assertTrue(tmp0 == x || tmp1 == x);
		Assertions.assertTrue(tmp0 == vi2 || tmp1 == vi2);
		Assertions.assertTrue(tmp0 != tmp1);
		
		tmpt = tmpr.getTerme().get(5);
		Assertions.assertEquals(x, tmpt);
		
		tmpt = tmpr.getTerme().get(6);
		Assertions.assertEquals(VarTyp.INT, tmpt.getTyp());
		Assertions.assertTrue(tmpt instanceof Wert);
	}
	
	 private boolean inSkolemNormalform(Formel f) {
	    Formel tmp = f;
	    while(tmp.getTyp() == Typ.EXISTS || tmp.getTyp() == Typ.FORALL) {
	      if (f.getTyp() == Typ.EXISTS) {
	        return false;
	      }
	      tmp = tmp.getOperanden().get(0);
	    }
	    return this.keinQuantor(tmp);
	  }
	  
	  private boolean keinQuantor(Formel f) {
	    if (f.getTyp() == Typ.EXISTS || f.getTyp() == Typ.FORALL) {
	      return false;
	    }
	    for (Formel tf: f.getOperanden()) {
	      if (!this.keinQuantor(tf)) {
	        return false;
	      }
	    }
	    return true;
	  }
	  
	  @Test
	  public void testMitZufallsformeln() {
	    for(int i = 0; i < 50; i++) {
	      Formel sut = Formelgenerator.generate();
	      Formel erg = sut.deepClone()
	          .praenexnormalformSchritt1()
	          .praenexnormalformSchritt2()
	          .praenexnormalformSchritt3()
	          .praenexnormalformSchritt4()
	          .skolemnormalform();
	      Assertions.assertTrue(this.inSkolemNormalform(erg)
	          , sut.zeigen() + "\n nicht in Skolemnormalform\n " + erg.zeigen() + "\n");
	    }
	  }
}
