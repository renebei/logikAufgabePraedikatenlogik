package aussagenlogik;

import java.util.List;

public class Und extends Formel {

	public Und(Formel... formeln) {
		super(formeln);
		super.typ = Typ.UND;
	}

	@Override
	public boolean evaluieren(Interpretation inter) {
		for (Formel formel : super.operanden) {
			if (!formel.evaluieren(inter)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean validieren(List<Formel> operanden) {
		if (operanden == null || operanden.size() < 1) {
			throw new IllegalArgumentException("Und benoetigt mindestens eine Operanden: "
					+ operanden);
		}
		return true;
	}

	@Override
	public String zeigen() {
		this.validieren(super.operanden);
		String erg ="(";
		int i=0;
		for(; i<super.operanden.size()-1; i++) {
			erg += super.operanden.get(i).zeigen() + " AND ";
		}
		return erg + super.operanden.get(i).zeigen()+")";
	}
}
