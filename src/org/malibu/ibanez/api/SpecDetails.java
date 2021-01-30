package org.malibu.ibanez.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpecDetails {
	
	private List<Integer> yearsProduced = new ArrayList<>();
	private String description;
	private boolean requiresReview = false;
	
	public List<Integer> getYearsProduced() {
		return yearsProduced;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean requiresReview() {
		return requiresReview;
	}

	public void setRequiresReview(boolean requiresReview) {
		this.requiresReview = requiresReview;
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		
		boolean hasDescription = description != null && description.trim().length() > 0;
		
		buffer.append("{");
		
		if(requiresReview) {
			buffer.append(" !requires-review! ");
		}
		
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
