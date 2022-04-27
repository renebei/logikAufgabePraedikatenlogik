package test;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import aussagenlogik.Formel;
import aussagenlogik.Oder;
import aussagenlogik.Und;
import relation.Relation;
import term.Funktion;
import term.Substitution;
import term.VarTyp;
import term.Variable;
import term.Wert;

public class UnifikationTest {

	private Variable vi;
	private Variable vi2;
	private Variable vs;

	@BeforeEach
	public void setUp() throws Exception {
		vi = new Variable("b", VarTyp.INT);
		vi2 = new Variable("b2", VarTyp.INT);
		vs = new Variable("c", VarTyp.STRING);
	}
	
	@Test
	public void testUnifikationNichtOk1() {
		Relation r1 = new Relation("r1", vi);
		Relation r2 = new Relation("r2", vi); 
		List<Substitution> erg = r1.unifiziereMit(r2);
		List<Substitution> erg2 = r2.unifiziereMit(r1);
		Assertions.assertNull(erg);
		Assertions.assertNull(erg2);
	}
	
	@Test
	public void testUnifikationNichtOk2() {
		Relation r1 = new Relation("r1", vi);
		Relation r2 = new Relation("r1", vi, vi); 
		List<Substitution> erg = r1.unifiziereMit(r2);
		List<Substitution> erg2 = r2.unifiziereMit(r1);
		Assertions.assertNull(erg);
		Assertions.assertNull(erg2);
	}
	
	@Test
	public void testUnifikationNichtOk3() {
		Relation r1 = new Relation("r1", new Funktion("f1", VarTyp.BOOL, vi));
		Relation r2 = new Relation("r1", new Funktion("f2", VarTyp.BOOL, vi)); 
		List<Substitution> erg = r1.unifiziereMit(r2);
		List<Substitution> erg2 = r2.unifiziereMit(r1);
		Assertions.assertNull(erg);
		Assertions.assertNull(erg2);
	}
	
	@Test
	public void testUnifikationNichtOk4() {
		Relation r1 = new Relation("r1", new Funktion("f1", VarTyp.BOOL, vi));
		Relation r2 = new Relation("r1", new Funktion("f1", VarTyp.STRING, vi)); 
		List<Substitution> erg = r1.unifiziereMit(r2);
		List<Substitution> erg2 = r2.unifiziereMit(r1);
		Assertions.assertNull(erg);
		Assertions.assertNull(erg2);
	}
	
	@Test
	public void testUnifikationNichtOk5() {
		Relation r1 = new Relation("r1", new Funktion("f1", VarTyp.BOOL, vi));
		Relation r2 = new Relation("r1", new Funktion("f1", VarTyp.BOOL, vi, vi)); 
		List<Substitution> erg = r1.unifiziereMit(r2);
		List<Substitution> erg2 = r2.unifiziereMit(r1);
		Assertions.assertNull(erg);
		Assertions.assertNull(erg2);
	}
	
	@Test
	public void testUnifikationNichtOk6() {
		Relation r1 = new Relation("r1", new Funktion("f1", VarTyp.BOOL, vi, new Wert("__ano", 42)));
		Relation r2 = new Relation("r1", new Funktion("f1", VarTyp.BOOL, vi, new Wert("__ano", 43))); 
		List<Substitution> erg = r1.unifiziereMit(r2);
		List<Substitution> erg2 = r2.unifiziereMit(r1);
		Assertions.assertNull(erg);
		Assertions.assertNull(erg2);
	}
	
	@Test
	public void testUnifikationNichtOk7() {
		Relation r1 = new Relation("r1", new Funktion("f1", VarTyp.BOOL, vi, new Wert("__ano", 42)));
		Relation r2 = new Relation("r1", new Funktion("f1", VarTyp.BOOL, vi, new Funktion("f2", VarTyp.STRING, vi))); 
		List<Substitution> erg = r1.unifiziereMit(r2);
		List<Substitution> erg2 = r2.unifiziereMit(r1);
		Assertions.assertNull(erg);
		Assertions.assertNull(erg2);
	}
	
	@Test
	public void testUnifikationNichtOk8() {
		Relation r1 = new Relation("r1", vi);
		Relation r2 = new Relation("r1", new Funktion("f1", VarTyp.BOOL, vi, new Wert("__ano", 43))); 
		List<Substitution> erg = r1.unifiziereMit(r2);
		List<Substitution> erg2 = r2.unifiziereMit(r1);
		Assertions.assertNull(erg);
		Assertions.assertNull(erg2);
	}
	
	@Test
	public void testUnifikationNichtOk9() {
		Relation r1 = new Relation("r1", new Funktion("f1", VarTyp.BOOL, vi, new Wert("__ano", 42)));
		Relation r2 = new Relation("r1", new Funktion("f1", VarTyp.BOOL, new Wert("__ano", 47), vi)); 
		List<Substitution> erg = r1.unifiziereMit(r2);
		List<Substitution> erg2 = r2.unifiziereMit(r1);
		Assertions.assertNull(erg);
		Assertions.assertNull(erg2);
	}
	
	@Test
	public void testUnifikationOk1() {
		Relation r1 = new Relation("r1", new Wert("__ano", 42), vi, new Funktion("f1", VarTyp.BOOL, vi2, new Wert("__ano", 43)));
		Relation r2 = new Relation("r1", new Wert("__ano", 42), vi, new Funktion("f1", VarTyp.BOOL, vi2, new Wert("__ano", 43))); 
		List<Substitution> erg = r1.unifiziereMit(r2);
		List<Substitution> erg2 = r2.unifiziereMit(r1);
		Assertions.assertTrue(erg.isEmpty());
		Assertions.assertTrue(erg2.isEmpty());
	}
	
	@Test
	public void testUnifikationOk2() {
		Wert w = new Wert("__ano", 42);
		Relation r1 = new Relation("r1", w);
		Relation r2 = new Relation("r1", vi);
		Relation r3 = new Relation("r1", vi); // r2 wird beim Unifizieren Veraendert
		List<Substitution> erg = r1.unifiziereMit(r2);
		List<Substitution> erg2 = r3.unifiziereMit(r1);
		Assertions.assertEquals(1, erg.size());
		Assertions.assertEquals(1, erg2.size());
		Assertions.assertTrue(erg.get(0).getNeu() == w && erg.get(0).getAlt() == vi);
		Assertions.assertTrue(erg2.get(0).getNeu() == w && erg2.get(0).getAlt() == vi);
	}
	
	@Test
	public void testUnifikationOk3() {
		Relation r1 = new Relation("r1", vi2);
		Relation r2 = new Relation("r1", vi);
		List<Substitution> erg = r1.unifiziereMit(r2);
		Assertions.assertEquals(1, erg.size());
		Assertions.assertTrue((erg.get(0).getNeu() == vi2 && erg.get(0).getAlt() == vi)
				|| (erg.get(0).getNeu() == vi && erg.get(0).getAlt() == vi2));
	}
	
	@Test
	public void testUnifikationOk4() {
		Relation r1 = new Relation("r1", vi2);
		Relation r2 = new Relation("r1", vi);
		List<Substitution> erg = r2.unifiziereMit(r1);
		Assertions.assertEquals(1, erg.size());
		Assertions.assertTrue((erg.get(0).getNeu() == vi2 && erg.get(0).getAlt() == vi)
				|| (erg.get(0).getNeu() == vi && erg.get(0).getAlt() == vi2));
	}
	
	@Test
	public void testUnifikationOk5() {
		Funktion f = new Funktion("f1", VarTyp.INT, vs);
		Relation r1 = new Relation("r1", f);
		Relation r2 = new Relation("r1", vi);
		List<Substitution> erg = r1.unifiziereMit(r2);
		Assertions.assertEquals(1, erg.size());
		Assertions.assertTrue((erg.get(0).getNeu() == f && erg.get(0).getAlt() == vi));
	}
	
	@Test
	public void testUnifikationOk6() {
		Funktion f = new Funktion("f1", VarTyp.INT, vs);
		Relation r1 = new Relation("r1", f);
		Relation r2 = new Relation("r1", vi);
		List<Substitution> erg = r2.unifiziereMit(r1);
		Assertions.assertEquals(1, erg.size());
		Assertions.assertTrue((erg.get(0).getNeu() == f && erg.get(0).getAlt() == vi));
	}
	
	@Test
	public void testUnifikationOk7() {
		Wert w = new Wert("__ano", 42);
		Funktion g = new Funktion("g", VarTyp.INT, vi, vi2);
		Funktion f = new Funktion("f1", VarTyp.INT, vi, g);
		Funktion g2 = new Funktion("g", VarTyp.INT, w, w);
		Funktion f2 = new Funktion("f1", VarTyp.INT, w, g2);
		Relation r1 = new Relation("r1", f);
		Relation r2 = new Relation("r1", f2);
		List<Substitution> erg = r2.unifiziereMit(r1);
		Assertions.assertEquals(2, erg.size());
		Assertions.assertTrue((erg.get(0).getNeu() == w && erg.get(0).getAlt() == vi
						&& (erg.get(1).getNeu() == w && erg.get(1).getAlt() == vi2))
				|| (erg.get(0).getNeu() == w && erg.get(1).getAlt() == vi
						&& (erg.get(1).getNeu() == w && erg.get(0).getAlt() == vi2)));
	}
	
	@Test
	public void testUnifikationOk8() { 
		Wert w = new Wert("__ano", 42);
		Funktion g = new Funktion("g", VarTyp.INT, vi, vi2);
		Funktion f = new Funktion("f1", VarTyp.INT, vi, g);
		Funktion g2 = new Funktion("g", VarTyp.INT, w, w);
		Funktion f2 = new Funktion("f1", VarTyp.INT, w, g2);
		Relation r1 = new Relation("r1", f);
		Relation r2 = new Relation("r1", f2);
		List<Substitution> erg = r1.unifiziereMit(r2);
		Assertions.assertEquals(2, erg.size());
		Assertions.assertTrue((erg.get(0).getNeu() == w && erg.get(0).getAlt() == vi
						&& (erg.get(1).getNeu() == w && erg.get(1).getAlt() == vi2))
				|| (erg.get(0).getNeu() == w && erg.get(1).getAlt() == vi
						&& (erg.get(1).getNeu() == w && erg.get(0).getAlt() == vi2)));
	}
	
	@Test
	public void testUnifikationOk9() { 
		Wert w = new Wert("__ano", 42);
		Funktion g = new Funktion("g", VarTyp.INT, vi, vi2);
		Funktion f = new Funktion("f1", VarTyp.INT, vi, g);
		Funktion g2 = new Funktion("g", VarTyp.INT, w, w);
		Funktion f2 = new Funktion("f1", VarTyp.INT, w, g2);
		Relation r1 = new Relation("r1", f);
		Relation r2 = new Relation("r1", f2);
		Formel f1 = new Und(r1,r2);
		Formel fo2 = new Und(r2,r1);
		List<Substitution> erg = f1.unifiziereMit(fo2);
		Assertions.assertEquals(2, erg.size());
		Assertions.assertTrue((erg.get(0).getNeu() == w && erg.get(0).getAlt() == vi
						&& (erg.get(1).getNeu() == w && erg.get(1).getAlt() == vi2))
				|| (erg.get(0).getNeu() == w && erg.get(1).getAlt() == vi
						&& (erg.get(1).getNeu() == w && erg.get(0).getAlt() == vi2)));
	}
	
	@Test
	public void testUnifikationNichtOk10() { 
		Wert w = new Wert("__ano", 42);
		Funktion g = new Funktion("g", VarTyp.INT, vi, vi2);
		Funktion f = new Funktion("f1", VarTyp.INT, vi, g);
		Funktion g2 = new Funktion("g", VarTyp.INT, w, w);
		Funktion f2 = new Funktion("f1", VarTyp.INT, w, g2);
		Relation r1 = new Relation("r1", f);
		Relation r2 = new Relation("r1", f2);
		Formel f1 = new Und(r1,r2);
		Formel fo2 = new Oder(r2,r1);
		List<Substitution> erg = f1.unifiziereMit(fo2);
		Assertions.assertNull(erg);
	}
}
