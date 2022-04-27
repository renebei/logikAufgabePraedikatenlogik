package term;

import java.util.ArrayList;
import java.util.List;

import aussagenlogik.Interpretation;
import util.DeepClone;

public abstract class Term implements DeepClone<Term>{
	protected VarTyp typ; // Ergebnistyp
	protected List<Term> teilterme;
	protected TermTyp termTyp;

	private Term(Term... terme) {
		super();
		var tmp = new ArrayList<Term>();
		for (Term t : terme) {
			tmp.add(t);
		}
		this.setTeilterme(tmp);
	}

	public Term(VarTyp typ, Term... terme) {
		this(terme);
		if (typ == null) {
			throw new IllegalArgumentException("Ergebnistyp muss bei Term angegeben werden.");
		}
		this.typ = typ;
	}
	
	public Term erster() {
		return this.teilterme.get(0);
	}
	
	public Term zweiter() {
		return this.teilterme.get(1);
	}
	
	public abstract List<Substitution> unifiziereMit(Term t);
	
	public boolean gleicheStelligkeit(Term f1) {
		if (f1.getTeilterme().size() != this.getTeilterme().size()) {
			return false;
		}
		for(int i = 0; i < f1.getTeilterme().size(); i++) {
			if (f1.getTeilterme().get(i).getTyp() != VarTyp.OPEN
					&& this.getTeilterme().get(i).getTyp() != VarTyp.OPEN
					&& f1.getTeilterme().get(i).getTyp() != this.getTeilterme().get(i).getTyp()) {
				return false;
			}
		}
		return true;
	}
	

	public abstract Wert evaluieren(Interpretation inter);

	
	public abstract String zeigen(); 
	
	public List<String> funktionsnamen() {
		List<String> ergebnis = new ArrayList<>();
		for(Term t: this.teilterme) {
			ergebnis.addAll(t.funktionsnamen());
		}
		return ergebnis;
	}
	
	public List<Funktion> alleFunktionen() {
		List<Funktion> ergebnis = new ArrayList<>();
		for(Term t: this.teilterme) {
			ergebnis.addAll(t.alleFunktionen());
		}
		return ergebnis;
	}
	
	/** Alle Vorkommen der Variablen alt werden durch den Term neu ersetzt.
	 * 
	 * @param neu neuer Term
	 * @param alt zu ersetzende Variable
	 */
	public void substituierenTermFuerVariable(Term neu, Variable alt) {
		if(alt.getTyp() == VarTyp.OPEN) {
			alt.setTyp(neu.getTyp());
		}
		if(alt.getTyp() != neu.getTyp()) {
			throw new IllegalArgumentException("Typ " + alt.getTyp() + " kann nicht durch "
					+ neu.getTyp() + " ersetzt werden."); 
		}
		List<Term> neueTeilterme = new ArrayList<>();
		for(Term t: this.teilterme) {
		  if (t.getTermTyp() == TermTyp.VARIABLE) {
			//if (t instanceof Variable) {
				if (t == alt) {
					neueTeilterme.add(neu);
				} else {
					neueTeilterme.add(t);
				}
			} else {
			  if (t.getTermTyp() == TermTyp.KONSTANTE) {
					neueTeilterme.add(t);
				} else {
				  if (t.getTermTyp() == TermTyp.FUNKTION) {
						t.substituierenTermFuerVariable(neu, alt);
						neueTeilterme.add(t);
					} else {
						throw new IllegalArgumentException("Term muesste Funktion sein");
					}
				}
			}
		}
		this.teilterme = neueTeilterme;
	}
	
	public abstract List<Variable> variablen();
	
	public VarTyp getTyp() {
		return typ;
	}

	public void setTyp(VarTyp typ) {
		this.typ = typ;
	}

	public List<Term> getTeilterme() {
		return teilterme;
	}

	public void setTeilterme(List<Term> teilterme) {
		this.teilterme = teilterme;
	}

	public TermTyp getTermTyp() {
		return termTyp;
	}

	public void setTermTyp(TermTyp termTyp) {
		this.termTyp = termTyp;
	}
}
