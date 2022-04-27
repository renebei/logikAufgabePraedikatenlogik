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
import aussagenlogik.Und;
import relation.Exists;
import relation.ForAll;
import relation.Relation;
import term.Funktion;
import term.VarTyp;
import term.Variable;
import term.Wert;

class PraedikatenlogikBasisTest {

	private Variable vb;
	private Variable vi;
	private Variable vi2;
	private Variable vs;
	private Wert wb;
	private Wert wi;
	private Wert ws;
	private Funktion f1;
	private Funktion f2;	
	private Relation r1;

	@BeforeEach
	public void setUp() throws Exception {
		vb = new Variable("a", VarTyp.BOOL);
		vi = new Variable("b", VarTyp.INT);
		vi2 = new Variable("b2", VarTyp.INT);
		vs = new Variable("c", VarTyp.STRING);
		wb = new Wert("wb", false);
		wi = new Wert("wi", 0);
		ws = new Wert("ws", "");
		f1 = new Funktion("f1",VarTyp.INT,vi,vi);
		f2 = new Funktion("f2",VarTyp.INT,f1,vi,vb,vs,wb,wi,ws);
		r1 = new Relation("r1",f1,vi2, wi);
	}
	
	@Test
	public void testFrei1() {
	  Formel f = new Relation("r", vb, vi);
	  Set<Variable> frei = f.frei();
	  Assertions.assertEquals(2, frei.size());
	  Assertions.assertTrue(frei.contains(vb));
	  Assertions.assertTrue(frei.contains(vi));
	}
	
	 @Test
   public void testFrei2() {
     Formel f = new Und(new Relation("r", vb, vi), new Relation("r", vb, vi));
     Set<Variable> frei = f.frei();
     Assertions.assertEquals(2, frei.size());
     Assertions.assertTrue(frei.contains(vb));
     Assertions.assertTrue(frei.contains(vi));
   }
	 
   @Test
   public void testFrei3() {
     Formel f = new Und(new Exists(vb, new Relation("r", vb, vi))
         , new Relation("r", vb, vi));
     Set<Variable> frei = f.frei();
     Assertions.assertEquals(2, frei.size());
     Assertions.assertTrue(frei.contains(vb));
     Assertions.assertTrue(frei.contains(vi));
     List<Variable> gebunden = f.gebunden();
     Assertions.assertEquals(1, gebunden.size());
     Assertions.assertTrue(gebunden.contains(vb));
   }
   
   @Test
   public void testFrei4() {
     Formel f = new Und(new ForAll(vi,new Exists(vb, new Relation("r", vb, vi)))
         , new Relation("r", vb, vi));
     Set<Variable> frei = f.frei();
     Assertions.assertEquals(2, frei.size());
     Assertions.assertTrue(frei.contains(vb));
     Assertions.assertTrue(frei.contains(vi));
     List<Variable> gebunden = f.gebunden();
     Assertions.assertEquals(2, gebunden.size());
     Assertions.assertTrue(gebunden.contains(vb));
     Assertions.assertTrue(gebunden.contains(vi));
   }
   
   @Test
   public void testFrei5() {
     Formel f = new Und(new Exists(vb, new Relation("r", vb, vi))
         , new ForAll(vi, new Relation("r", vb, vi)));
     Set<Variable> frei = f.frei();
     Assertions.assertEquals(2, frei.size());
     Assertions.assertTrue(frei.contains(vb));
     Assertions.assertTrue(frei.contains(vi));
     List<Variable> gebunden = f.gebunden();
     Assertions.assertEquals(2, gebunden.size());
     Assertions.assertTrue(gebunden.contains(vb));
     Assertions.assertTrue(gebunden.contains(vi));
   }
   
   private boolean hatVariablenname(Collection<Variable> vars, String name) {
     for(Variable v:vars) {
       if(v.getName().equals(name)) {
         return true;
       }
     }
     return false;
   }
   
   @Test
   public void testFrei6() {
     Formel f = new Und(new Exists(new Variable("a", VarTyp.BOOL), 
                new Relation("r", new Variable("a", VarTyp.BOOL)
                    , new Variable("b", VarTyp.INT)))
         , new ForAll(new Variable("b", VarTyp.INT)
             , new Relation("r", new Variable("a", VarTyp.BOOL)
                 , new Variable("b", VarTyp.INT))));
     Set<Variable> frei = f.frei();
     Assertions.assertEquals(2, frei.size());
     Assertions.assertTrue(this.hatVariablenname(frei,vb.getName()));
     Assertions.assertTrue(this.hatVariablenname(frei,vi.getName()));
     List<Variable> gebunden = f.gebunden();
     Assertions.assertEquals(2, gebunden.size());
     Assertions.assertTrue(this.hatVariablenname(gebunden, vb.getName()));
     Assertions.assertTrue(this.hatVariablenname(gebunden, vi.getName()));
   }
   
   @Test
   public void testFrei7() {
     Formel f = new ForAll(new Variable("a", VarTyp.INT)
             , new Relation("r", new Variable("b", VarTyp.INT)));
     Set<Variable> frei = f.frei();
     Assertions.assertEquals(1, frei.size());
     Assertions.assertTrue(this.hatVariablenname(frei,"b"));
     List<Variable> gebunden = f.gebunden();
     Assertions.assertEquals(1, gebunden.size());
     Assertions.assertTrue(this.hatVariablenname(gebunden, "a"));
   }
	
	@Test
	public void testSubstitutionInFormel1() {
		// passiert nichts
		r1.substituierenTermFuerVariable(f1, new Variable("xy", VarTyp.INT));
		Assertions.assertEquals(3, r1.getTerme().size());
		Assertions.assertSame(vi2, r1.getTerme().get(1));
		Assertions.assertSame(wi, r1.getTerme().get(2));
		Assertions.assertSame(vi, r1.getTerme().get(0).getTeilterme().get(0));
		Assertions.assertSame(vi, r1.getTerme().get(0).getTeilterme().get(1));
	}
	
	@Test
	public void testSubstitutionInFormel2() {
		r1.substituierenTermFuerVariable(f1, vi2);
		Assertions.assertEquals(3, r1.getTerme().size());
		Assertions.assertSame(f1, r1.getTerme().get(1));
		Assertions.assertSame(wi, r1.getTerme().get(2));
		Assertions.assertSame(vi, r1.getTerme().get(0).getTeilterme().get(0));
		Assertions.assertSame(vi, r1.getTerme().get(0).getTeilterme().get(1));
	}
	
	@Test
	public void testSubstitutionInFormel3() {
		r1.substituierenTermFuerVariable(vi2, vi);
		Assertions.assertEquals(3, r1.getTerme().size());
		Assertions.assertSame(vi2, r1.getTerme().get(1));
		Assertions.assertSame(wi, r1.getTerme().get(2));
		Assertions.assertSame(vi2, r1.getTerme().get(0).getTeilterme().get(0));
		Assertions.assertSame(vi2, r1.getTerme().get(0).getTeilterme().get(1));
	}
	
	@Test
	public void testSubstitutionInFormel4() {
		Formel fu1 = new ForAll(vi, r1);
		fu1.substituierenTermFuerVariable(vi2, vi);
		r1 = (Relation)fu1.getOperanden().get(0);
		Assertions.assertEquals(3, r1.getTerme().size());
		Assertions.assertSame(vi2, r1.getTerme().get(1));
		Assertions.assertSame(wi, r1.getTerme().get(2));
		Assertions.assertSame(vi, r1.getTerme().get(0).getTeilterme().get(0));
		Assertions.assertSame(vi, r1.getTerme().get(0).getTeilterme().get(1));
	}
	
	@Test
	public void testSubstitutionInFormel5() {
		Formel fu1 = new ForAll(vi, r1);
		Variable vv = new Variable("vv", VarTyp.INT);
		fu1.substituierenTermFuerVariable(vv, vi2);
		r1 = (Relation)fu1.getOperanden().get(0);
		Assertions.assertEquals(3, r1.getTerme().size());
		Assertions.assertSame(vv, r1.getTerme().get(1));
		Assertions.assertSame(wi, r1.getTerme().get(2));
		Assertions.assertSame(vi, r1.getTerme().get(0).getTeilterme().get(0));
		Assertions.assertSame(vi, r1.getTerme().get(0).getTeilterme().get(1));
	}
	
	@Test
	public void testSubstitutionInFormel6() {
		Formel fu1 = new ForAll(vi2, new Exists(vi, r1));
		Variable vv = new Variable("vv", VarTyp.INT);
		fu1.substituierenTermFuerVariable(vv, vi);
		fu1.substituierenTermFuerVariable(vv, vi2);
		r1 = (Relation)fu1.getOperanden().get(0).getOperanden().get(0);
		Assertions.assertEquals(3, r1.getTerme().size());
		Assertions.assertSame(vi2, r1.getTerme().get(1));
		Assertions.assertSame(wi, r1.getTerme().get(2));
		Assertions.assertSame(vi, r1.getTerme().get(0).getTeilterme().get(0));
		Assertions.assertSame(vi, r1.getTerme().get(0).getTeilterme().get(1));
	}
	
	@Test
	public void testGebunden1() {
		List<Variable> gebunden = r1.gebunden();
		Assertions.assertTrue(gebunden.isEmpty());
	}
	
	@Test
	public void testRelationGebunden2() {
		Formel fu1 = new ForAll(vi, r1);
		List<Variable> gebunden = fu1.gebunden();
		Assertions.assertEquals(1, gebunden.size());
		Assertions.assertTrue(gebunden.contains(vi));
	}
	
	@Test
	public void testRelationGebunden3() {
		Formel fu1 = new ForAll(vi2, new Exists(vi, r1));
	    List<Variable> gebunden = fu1.gebunden();
		Assertions.assertEquals(2, gebunden.size());
		Assertions.assertTrue(gebunden.contains(vi));
		Assertions.assertTrue(gebunden.contains(vi2));
	}
	
	@Test
	public void testRelationGebunden5() {
		Formel fu1 = new ForAll(vi, new Exists(vi, r1));
	    List<Variable> gebunden = fu1.gebunden();
		Assertions.assertEquals(2, gebunden.size());
		for (Variable v: gebunden) {
			Assertions.assertEquals(vi, v);
		}
	}
	
	@Test
	public void testRelationGebunden6() {
		Formel fu1 = new ForAll(new Variable(vi.getName(), vi.getTyp()), new Exists(vi, r1));
	    List<Variable> gebunden = fu1.gebunden();
		Assertions.assertEquals(2, gebunden.size());
		Assertions.assertTrue(gebunden.contains(vi));
		for (Variable v: gebunden) {
			Assertions.assertEquals(vi.getName(), v.getName());
		}
	}
	
	@Test
	public void testGebunden4() {
		Formel fu1 = new Und(new Relation("r",vi, wi), new Relation("r2",ws, vi2));
	    List<Variable> gebunden = fu1.gebunden();
		Assertions.assertTrue(gebunden.isEmpty());
	}
	
	@Test
	public void testRelationFrei1() {
		Set<Variable> frei = r1.frei();
		Assertions.assertEquals(2, frei.size());
		Assertions.assertTrue(frei.contains(vi));
		Assertions.assertTrue(frei.contains(vi2));
	}
	
	@Test
	public void testRelationFrei2() {
		Formel fu1 = new ForAll(vi, r1);
		Set<Variable> frei = fu1.frei();
		Assertions.assertEquals(1, frei.size());
		Assertions.assertTrue(frei.contains(vi2));
	}
	
	@Test
	public void testRelationFrei3() {
		Formel fu1 = new Exists(vi, r1);
		Set<Variable> frei = fu1.frei();
		Assertions.assertEquals(1, frei.size());
		Assertions.assertTrue(frei.contains(vi2));
	}
	
	@Test
	public void testRelationFrei4() {
		Formel fu1 = new ForAll(vi2, new Exists(vi, r1));
		Set<Variable> frei = fu1.frei();
		Assertions.assertEquals(0, frei.size());
	}
	
	@Test
	public void testNegationFrei() {
		Formel fu1 = new Negation(r1);
		Set<Variable> frei = fu1.frei();
		Assertions.assertEquals(2, frei.size());
		Assertions.assertTrue(frei.contains(vi2));
		Assertions.assertTrue(frei.contains(vi));
	}
	
	@Test
	public void testUndFrei() {
		Formel fu1 = new Und(new Relation("r",vi, wi), new Relation("r2",ws, vi2));
		Set<Variable> frei = fu1.frei();
		Assertions.assertEquals(2, frei.size());
		Assertions.assertTrue(frei.contains(vi2));
		Assertions.assertTrue(frei.contains(vi));
	}
	
	@Test
	public void testOderFrei() {
		Formel fu1 = new Oder(new Relation("r",vi, wi), new Relation("r2",ws, vi2));
		Set<Variable> frei = fu1.frei();
		Assertions.assertEquals(2, frei.size());
		Assertions.assertTrue(frei.contains(vi2));
		Assertions.assertTrue(frei.contains(vi));
	}
	
	@Test
	public void testRelationOk1() {
		Assertions.assertEquals(3, r1.getTerme().size());
		Assertions.assertSame(vi2, r1.getTerme().get(1));
		Assertions.assertSame(wi, r1.getTerme().get(2));
		Assertions.assertSame(vi, r1.getTerme().get(0).getTeilterme().get(0));
		Assertions.assertSame(vi, r1.getTerme().get(0).getTeilterme().get(1));
	}
	
	@Test
	public void testRelationOk2() {
		r1 = new Relation("r1",f1,vi2
				,new Variable(vi.getName(), VarTyp.INT)
				,new Variable(vi2.getName(), VarTyp.INT)
				, wi);
		Assertions.assertEquals(5, r1.getTerme().size());
		Assertions.assertSame(vi2, r1.getTerme().get(1));
		Assertions.assertSame(vi, r1.getTerme().get(0).getTeilterme().get(0));
		Assertions.assertSame(vi, r1.getTerme().get(0).getTeilterme().get(1));
		Assertions.assertSame(vi, r1.getTerme().get(2));
		Assertions.assertSame(vi2, r1.getTerme().get(3));
		Assertions.assertSame(wi, r1.getTerme().get(4));
	}
	
	@Test
	public void testRelationNichtOk() {
		try {
			r1 = new Relation("r1",f1,vi2
					,new Variable(vi.getName(), VarTyp.INT)
					,new Variable(vi2.getName(), VarTyp.BOOL));
			Assertions.fail();
		} catch (IllegalArgumentException e) {
		}
	}
	
	@Test
	public void testFunktionOk1() {
		Assertions.assertEquals(7, f2.getTeilterme().size());
		Assertions.assertSame(vi, f2.getTeilterme().get(1));
		Assertions.assertSame(vi, f2.getTeilterme().get(0).getTeilterme().get(0));
		Assertions.assertSame(vi, f2.getTeilterme().get(0).getTeilterme().get(1));
	}
	
	@Test
	public void testFunktionOk2() {
		Funktion f = new Funktion("f2",VarTyp.INT,f1,vi
				,new Variable(vi.getName(), VarTyp.INT)
				,new Variable(vi.getName(), VarTyp.INT)
				,vi);
		Assertions.assertEquals(5, f.getTeilterme().size());
		Assertions.assertSame(vi, f.getTeilterme().get(1));
		Assertions.assertSame(vi, f.getTeilterme().get(0).getTeilterme().get(0));
		Assertions.assertSame(vi, f.getTeilterme().get(0).getTeilterme().get(1));
		Assertions.assertSame(vi, f.getTeilterme().get(2));
		Assertions.assertSame(vi, f.getTeilterme().get(3));
		Assertions.assertSame(vi, f.getTeilterme().get(4));
	}
	
	@Test
	public void testFunktionNichtOk() {
		try {
			new Funktion("f2",VarTyp.INT,f1,vi
					,new Variable(vi.getName(), VarTyp.INT)
					,new Variable(vi.getName(), VarTyp.BOOL)
					,vi);
			Assertions.fail();
		} catch (IllegalArgumentException e) {
		}
	}
	
	@Test
	public void testVariablenberechnung1() {
		List<Variable> erg = wi.variablen();
		Assertions.assertTrue(erg.isEmpty());
	}
	
	@Test
	public void testVariablenberechnung2() {
		List<Variable> erg = vi.variablen();
		Assertions.assertEquals(1, erg.size());
		Assertions.assertTrue(erg.contains(vi));
	}
	
	@Test
	public void testVariablenberechnung3() {
		List<Variable> erg = f1.variablen();
		Assertions.assertEquals(1, erg.size());
		Assertions.assertTrue(erg.contains(vi));
	}
	
	@Test
	public void testVariablenberechnung4() {
		List<Variable> erg = f2.variablen();
		Assertions.assertEquals(3, erg.size());
		Assertions.assertTrue(erg.contains(vi));
		Assertions.assertTrue(erg.contains(vb));
		Assertions.assertTrue(erg.contains(vs));
	}

	@Test
	public void testSubstitutionVariableOk() {
		f1.substituierenTermFuerVariable(vi2, vi);
		Assertions.assertEquals(2, f1.getTeilterme().size());
		Assertions.assertSame(vi2, f1.getTeilterme().get(0));
		Assertions.assertSame(vi2, f1.getTeilterme().get(1));
	}
	
	@Test
	public void testSubstitutionVariableOk2() {
		f2.substituierenTermFuerVariable(vi2, vi);
		Assertions.assertEquals(7, f2.getTeilterme().size());
		Assertions.assertSame(vi2, f2.getTeilterme().get(1));
		Assertions.assertSame(vi2, f2.getTeilterme().get(0).getTeilterme().get(0));
		Assertions.assertSame(vi2, f2.getTeilterme().get(0).getTeilterme().get(1));
	}
	
	@Test
	public void testSubstitutionNichtBeinhalteteVariableOk2() {
		f2.substituierenTermFuerVariable(vi2, new Variable("x", VarTyp.INT));
		Assertions.assertEquals(7, f2.getTeilterme().size());
		Assertions.assertSame(vi, f2.getTeilterme().get(1));
		Assertions.assertSame(vi, f2.getTeilterme().get(0).getTeilterme().get(0));
		Assertions.assertSame(vi, f2.getTeilterme().get(0).getTeilterme().get(1));
	}
	
	@Test
	public void testSubstitutionWertOk() {
		f1.substituierenTermFuerVariable(wi, vi);
		Assertions.assertEquals(2, f1.getTeilterme().size());
		Assertions.assertSame(wi, f1.getTeilterme().get(0));
		Assertions.assertSame(wi, f1.getTeilterme().get(1));
	}
	
	@Test
	public void testSubstitutionWertOk2() {
		f2.substituierenTermFuerVariable(wi, vi);
		Assertions.assertEquals(7, f2.getTeilterme().size());
		Assertions.assertSame(wi, f2.getTeilterme().get(1));
		Assertions.assertSame(wi, f2.getTeilterme().get(0).getTeilterme().get(0));
		Assertions.assertSame(wi, f2.getTeilterme().get(0).getTeilterme().get(1));
	}
	
	@Test
	public void testSubstitutionFunktionOk() {
		f1.substituierenTermFuerVariable(f2, vi);
		Assertions.assertEquals(2, f1.getTeilterme().size());
		Assertions.assertSame(f2, f1.getTeilterme().get(0));
		Assertions.assertSame(f2, f1.getTeilterme().get(1));
	}
	
	@Test
	public void testSubstitutionFunktionOk2() {
		f2.substituierenTermFuerVariable(f1, vi);
		Assertions.assertEquals(7, f2.getTeilterme().size());
		Assertions.assertSame(f1, f2.getTeilterme().get(1));
		Assertions.assertSame(f1, f2.getTeilterme().get(0).getTeilterme().get(0));
		Assertions.assertSame(f1, f2.getTeilterme().get(0).getTeilterme().get(1));
	}
	
	@Test
	public void testSubstituionstypfehler1() {
		try {
			f1.substituierenTermFuerVariable(vb, vi);
			Assertions.fail();
		} catch(IllegalArgumentException e) {			
		}
	}
	
	@Test
	public void testSubstituionstypfehler2() {
		try {
			f1.substituierenTermFuerVariable(vs, vi);
			Assertions.fail();
		} catch(IllegalArgumentException e) {			
		}
	}
	
	@Test
	public void testSubstituionstypfehler3() {
		try {
			f1.substituierenTermFuerVariable(new Funktion("f3", VarTyp.BOOL), vi);
			Assertions.fail();
		} catch(IllegalArgumentException e) {			
		}
	}

}
