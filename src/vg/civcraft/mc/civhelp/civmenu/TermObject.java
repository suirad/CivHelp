package vg.civcraft.mc.civhelp.civmenu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TermObject {

	private UUID uuid;
	
	private List<String> terms = new ArrayList<String>();
	
	public TermObject(UUID uuid) {
		this.uuid = uuid;
	}
	
	public void addTerm(String term) {
		terms.add(term);
	}
	
	public void removeTerm(String term) {
		terms.remove(term);
	}
	
	public boolean hasTerm(String term) {
		return terms.contains(term);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (String x: terms) 
			builder.append(x + ":");
		builder.deleteCharAt(builder.length()-1);
		return builder.toString();
	}
}
