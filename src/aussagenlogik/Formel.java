package aussagenlogik;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import relation.Exists;
import relation.ForAll;
import relation.Relation;
import term.Funktion;
import term.Substitution;
import term.Term;
import term.VarTyp;
import term.Variable;
import term.Wert;
import util.DeepClone;

public abstract class Formel implements DeepClone<Formel> {
	private static final long serialVersionUID = 1L;
	protected Typ typ;
	protected List<Formel> operanden;

	protected Formel(Formel... formeln) {
		super();
		var tmp = new ArrayList<Formel>();
		for (Formel f : formeln) {
			tmp.add(f);
		}
		
		this.setOperanden(tmp);
		this.variablennamenNurEinmal();
		this.relationsnamenNurEinmal();
	}

	public Formel(Typ typ, Formel... formeln) {
		this(formeln);
		this.typ = typ;
		this.funktionsnamenNurEinmal();
		this.relationsnamenNurEinmal();		
	}

	public abstract boolean evaluieren(Interpretation inter);

	public boolean istLiteral() {
		return false;
	}

	public abstract boolean validieren(List<Formel> operanden); 
	
	public abstract String zeigen(); 
	
	/** Methode zum vereinfachten Zugriff auf den ersten Operanden,
	 * ist er nicht vorhanden, wird eine Exception geworfen.
	 * @return erster Operand
	 */
	public Formel erste() {
		Formel erg = this.operanden.get(0);
		if (erg == null) {
			throw new IllegalArgumentException("erste Teilformel nicht gefunden: "
					+ this.zeigen());
		}
		return erg;
	}
	
	 /** Methode zum vereinfachten Zugriff auf den ersten Operanden,
   * ist er nicht vorhanden, wird eine Exception geworfen.
   * @return zweiter Operand
   */
	public Formel zweite() {
		Formel erg = this.operanden.get(1);
		if (erg == null) {
			throw new IllegalArgumentException("zweite Teilformel nicht gefunden: "
					+ this.zeigen());
		}
		return erg;
	}
	
	/** Liste der Variablen der Formel in der Reihenfolge in der sie
	 * in der Formel vorkommen. Die Reihenfolge wird bei der Unifikation 
	 * benoetigt 
	 * @return Variablen der Formel in der Reihenfolge, in der sie 
	 * vorkommen.
	 */
	public List<Variable> variablen(){ // List, da Reihenfolge benoetigt wird
		List<Variable> ergebnis = new ArrayList<>();
		for(Formel t: this.operanden) {
			List<Variable> tmp = t.variablen();
			for(Variable v:tmp) {
				if (!ergebnis.contains(v)) {
			      ergebnis.add(v);
				}
			}
		}
		return ergebnis;
	}
	
	private void variablennamenNurEinmal() {
		List<Variable> vars = new ArrayList<>();
		for(Formel t: this.operanden) {
			List<Variable> tmp = t.variablen();
			for(Variable v:tmp) {
				if (!vars.contains(v)) {
			      vars.add(v);
				}
			}
		}
		
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
							this.substituierenTermFuerVariable(erster, vars.get(i));
						}
					}
				}
			}
		}
	}
	
	/** Prueft, ob die Kombination aus Funktionsnamen und Stelligkeit
	 * nur einmal vorkommt. Wenn nicht, wird eine Exception geworfen.
	 */
	protected void funktionsnamenNurEinmal() {
		List<Funktion> fkt = this.alleFunktionen();
		for(int i = 0; i < fkt.size(); i++){
			for(int j = i + 1; j < fkt.size(); j++){
				if (fkt.get(i).getName().equals(fkt.get(j).getName())
						&& 
						!fkt.get(i).gleicheStelligkeit(fkt.get(j))		
				) {
					throw new IllegalArgumentException("darf nicht zwei Funktionen "
							+ "mit gleichem Namen " + fkt.get(i).getName() 
							+ " und unterschiedlicher Stelligkeit geben. "
							+ fkt.get(i).getStelligkeit() + " - " + fkt.get(j).getStelligkeit());
				}
			}
		}
	}
	
  /** Prueft, ob die Kombination aus Relationsnamen und Stelligkeit
   * nur einmal vorkommt. Wenn nicht, wird eine Exception geworfen.
   */
  protected void relationsnamenNurEinmal() {
    List<Relation> fkt = this.alleRelationen();
    for(int i = 0; i < fkt.size(); i++){
      for(int j = i + 1; j < fkt.size(); j++){
        if (fkt.get(i).getName().equals(fkt.get(j).getName())
            && 
            !fkt.get(i).gleicheStelligkeit(fkt.get(j))
        ) {
          throw new IllegalArgumentException("darf nicht zwei Relationen "
              + "mit gleichem Namen " + fkt.get(i).getName() + " und unterschiedlicher Stelligkeit geben.");
        }
      }
    }
  }
	
	public boolean gleicheStelligkeit(Relation f1) {
		return true;
	}
		
	 /** Alle freien Vorkommen der Variablen alt werden durch den Term neu ersetzt.
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
		for(Formel f: this.operanden) {
			f.substituierenTermFuerVariable(neu, alt);
		}
	}
	
	public List<Substitution> unifiziereMit(Formel formel){
		// TODO
		return null;
	}
	
	 /** Berechnet die Menge aller in der Formel frei vorkommenden Variablen.
   * @return Menge der frei vorkommenden Variablen der Formel
   */
	public Set<Variable> frei(){
		Set<Variable> erg = new HashSet<>();
		for(Formel f: this.operanden) {
			erg.addAll(f.frei());
		}
		return erg;
	}
	
	/** Berechnet die Liste aller in der Formel gebunden vorkommenden Variablen.
	 * Da ein Variablenname oder ein Variablenobjekt mehrfach genutzt werden
	 * kann, ist das Ergebnis eine Liste, in der Objekte mit gleichen Namen und
	 * identische Objekte mehrfach vorkommen koennen.
	 * @return Liste der gebunden vorkommenden Variablen der Formel
	 */
	public List<Variable> gebunden(){
		List<Variable> erg = new ArrayList<>();
		for(Formel f: this.operanden) {
			erg.addAll(f.gebunden());
		}
		return erg;
	}
	
	public Formel praenexnormalformSchritt1() {
		//TODO
	  
		return null;
	}
	
	// nutzbar als eindeutiger Anhang fuer Variablennamen
	private static int id;
	
	public static int nextId() {
		return id++;
	}
	
	public Formel praenexnormalformSchritt2() {
	  //TODO
    
		return null;
	}
	 
  public Formel praenexnormalformSchritt3() {
    //TODO
    
    return null;
  }
  
  public Formel praenexnormalformSchritt4() {
    //TODO
    
    return null;
  }
	
	public List<String> funktionsnamen() {
		List<String> ergebnis = new ArrayList<>();
		for(Formel t: this.operanden) {
			ergebnis.addAll(t.funktionsnamen());
		}
		return ergebnis;
	}
	
	public List<Funktion> alleFunktionen() {
		List<Funktion> ergebnis = new ArrayList<>();
		for(Formel t: this.operanden) {
			ergebnis.addAll(t.alleFunktionen());
		}
		return ergebnis;
	}
	
	public List<Relation> alleRelationen() {
		List<Relation> ergebnis = new ArrayList<>();
		for(Formel t: this.operanden) {
			ergebnis.addAll(t.alleRelationen());
		}
		return ergebnis;
	}
	
	public List<String> relationsnamen() {
		List<String> ergebnis = new ArrayList<>();
		for(Formel t: this.operanden) {
			ergebnis.addAll(t.relationsnamen());
		}
		return ergebnis;
	}
			
	private String laengesterFunktionsname() {
		String max = "";
		for(String s: this.funktionsnamen()) {
			if (s.length() > max.length()) {
				max = s; 
			}
		}
		return max;
	}
	
	public Formel skolemnormalform() {
		//TODO
		return null;
	}
	
	public void addOperand(Formel f) {
		this.operanden.add(f);
		this.validieren(this.operanden);
	}

	public Typ getTyp() {
		return typ;
	}

	public void setTyp(Typ typ) {
		this.typ = typ;
	}

	public List<Formel> getOperanden() {
		return operanden;
	}

	public void setOperanden(List<Formel> operanden) {
		this.validieren(operanden);
		this.operanden = operanden;
	}

	@Override
	public int hashCode() {
		return Objects.hash(operanden, typ);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Formel other = (Formel) obj;
		return Objects.equals(operanden, other.operanden) && typ == other.typ;
	}

	@Override
	public String toString() {
		return "Formel [typ=" + typ + ", operanden=" + operanden + "]";
	}

}
