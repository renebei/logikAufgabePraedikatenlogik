package aussagenlogik;

import java.util.ArrayList;
import java.util.List;

import relation.Exists;
import relation.ForAll;

public class Negation extends Formel {
	
	public Negation(Formel... formeln) {
		super(formeln);
		super.typ = Typ.NEGATION;
	}

	@Override
	public boolean evaluieren(Interpretation inter) {
		return !super.operanden.get(0).evaluieren(inter);
	}
	
	@Override
	public boolean validieren(List<Formel> operanden) {
		if (operanden == null || operanden.size() != 1) {
			throw new IllegalArgumentException("Negation benoetigt genau einen Operanden: " 
					+ operanden);
		}
		return true;
	}
	
	@Override
	public boolean istLiteral() {
		this.validieren(super.operanden);
		return  Typ.RELATION == super.operanden.get(0).getTyp();
	}

	@Override
	public String zeigen() {
		this.validieren(super.operanden);
		if(this.istLiteral()) {
			return "NOT " + super.operanden.get(0).zeigen();
		}
		return "NOT(" + super.operanden.get(0).zeigen() +")";
	}
}
