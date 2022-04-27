package term;

import util.DeepClone;

public class Substitution implements DeepClone<Substitution> {
	private Term neu;
	private Variable alt;
	
	public Substitution(Term neu, Variable alt) {
		this.neu = neu;
		this.alt = alt;
	}
	
	public Term getNeu() {
		return neu;
	}
	
	public void setNeu(Term neu) {
		this.neu = neu;
	}
	
	public Variable getAlt() {
		return alt;
	}
	
	public void setAlt(Variable alt) {
		this.alt = alt;
	}

	@Override
	public String toString() {
		return "[" + alt.getName() + ":=" + neu.zeigen() + "]";
	}
}
