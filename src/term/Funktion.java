package term;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import aussagenlogik.Formel;
import aussagenlogik.Interpretation;

public class Funktion extends Term {
	private String name;
	private List<VarTyp> stelligkeit;
	
	public Funktion(String name, VarTyp ergebnistyp, Term... terme) {
		super(ergebnistyp, terme);
		this.stelligkeit = new ArrayList<>();
		this.name = name;
		this.typ = ergebnistyp;
		super.termTyp = TermTyp.FUNKTION;
		for(int i=0; i < terme.length; i++) {
			this.stelligkeit.add(terme[i].getTyp());
		}
		try {
			this.variablennamenNurEinmal();
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("darf nur eine Variable pro"
					+ " Namen in Funktion geben: " + e.getMessage());
		}
	}
	
	@Override
	public List<String> funktionsnamen() {
		List<String> erg = super.funktionsnamen();
		erg.add(this.name);
		return erg;
	}
	
	public List<Funktion> alleFunktionen() {
		List<Funktion> erg = super.alleFunktionen();
		erg.add(this);
		return erg;
	}

	public void variablennamenNurEinmal() {
		List<Variable> vars = new ArrayList<>(this.variablen());
		Set<String> mehrfach = new HashSet<>();
		for(int i = 0; i < vars.size(); i++) {
			for (int j = i + 1; j < vars.size(); j++) {
				if (vars.get(i).getName().equals(vars.get(j).getName())) {
					mehrfach.add(vars.get(i).getName());
				}
			}
		}
		if(!mehrfach.isEmpty()) {
			for(String n:mehrfach){
				Variable erster = null;
				for(int i = 0; i < vars.size(); i++) {					
					if(vars.get(i).getName().equals(n)) {
						if (erster == null) {
							erster = vars.get(i);
						} else {
							super.substituierenTermFuerVariable(erster, vars.get(i));
						}
					}
				}
			}
		}
	}
//	public Funktion(String name, List<VarTyp> stelligkeit, Term... terme) {
//		super(terme);
//		if(stelligkeit == null) {
//			throw new IllegalArgumentException("Liste der Parametertypen"
//					+ "darf nicht null sein.");
//		}
//		
//		this.name = name;
//		this.stelligkeit = stelligkeit;
//		if (this.stelligkeit.size() != super.teilterme.size()) {
//			throw new IllegalArgumentException("Anzahl der Parametertypen und"
//					+ " Terme muss uebereinstimmen.");
//		}
//		for(int i=0; i < this.stelligkeit.size(); i++) {
//			if (this.stelligkeit.get(i) != terme[i].getTyp()) {
//				throw new IllegalArgumentException("Parametertypen und Termtypen"
//						+ " muessen uebereinstimmen, nicht " + this.stelligkeit.get(i)
//						+ " und " + terme[i].getTyp() + ".");
//			}
//		}
//	}

	@Override
	public Wert evaluieren(Interpretation inter) {
		Term[] termeArray = new Term[teilterme.size()];
		for (int i = 0; i < teilterme.size(); i++) {
			termeArray[i] =  teilterme.get(i).evaluieren(inter);
		}

    	return inter.getFunktion(name).berechnen(termeArray);
	}

	@Override
	public String zeigen() {
		StringBuilder sb = new StringBuilder(this.name +"(");
		for(int i=0; i < super.teilterme.size() -1; i++) {
			sb.append(super.teilterme.get(i).zeigen() +", ");
		}
		if(super.teilterme.size() > 0) {
			sb.append(super.teilterme.get(super.teilterme.size() - 1).zeigen());
		}
		return sb.append(")").toString();
	}

	@Override
	public List<Variable> variablen(){ // List, da Reihenfolge benoetigt wird
		List<Variable> ergebnis = new ArrayList<>();
		for(Term t: super.teilterme) {
			List<Variable> tmp = t.variablen();
			for(Variable v:tmp) {
				if (!ergebnis.contains(v)) {
			      ergebnis.add(v);
				}
			}
		}
		return ergebnis;
	}
	
	@Override
	public List<Substitution> unifiziereMit(Term t){
		//TODO
		return null;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<VarTyp> getStelligkeit() {
		return stelligkeit;
	}

	public void setStelligkeit(List<VarTyp> stelligkeit) {
		this.stelligkeit = stelligkeit;
	}

	@Override
	public String toString() {
		return "Funktion [name=" + name + ", stelligkeit=" + stelligkeit + ", typ=" + typ + ", teilterme=" + teilterme
				+ ", hashCode()=" + hashCode() + "]";
	}
	
	
}
