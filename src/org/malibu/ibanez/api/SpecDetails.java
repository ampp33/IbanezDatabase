package org.malibu.ibanez.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpecDetails {
	
	private List<Integer> yearsProduced = new ArrayList<>();
	private String description;
	
	public List<Integer> getYearsProduced() {
		return yearsProduced;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		
		boolean hasDescription = description != null && description.trim().length() > 0;
		
		buffer.append("{");
		if(!yearsProduced.isEmpty()) {
			buffer.append(Arrays.toString(yearsProduced.toArray()));
			if(hasDescription) {
				buffer.append(" - ");
			}
		}
		
		if(hasDescription) {
			buffer.append(description);
		}
		
		buffer.append("}");
		
		return buffer.toString();
	}
	
}
