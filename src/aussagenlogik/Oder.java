package aussagenlogik;

import java.util.List;

public class Oder extends Formel {

	public Oder(Formel... formeln) {
		super(formeln);
		super.typ = Typ.ODER;
	}

	@Override
	public boolean evaluieren(Interpretation inter) {
		for (Formel formel : super.operanden) {
			if(formel.evaluieren(inter)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean validieren(List<Formel> operanden) {
		if (operanden == null || operanden.size() < 1) {
			throw new IllegalArgumentException("Oder benoetigt mindestens eine Operanden: "
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
			erg += super.operanden.get(i).zeigen() + " OR ";
		}
		return erg + super.operanden.get(i).zeigen() +")";
	}

}
