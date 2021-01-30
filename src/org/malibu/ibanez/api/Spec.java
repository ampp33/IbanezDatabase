package org.malibu.ibanez.api;

import java.util.ArrayList;
import java.util.List;

public class Spec {
	
	private String specTitle;
	private List<SpecDetails> specDetails = new ArrayList<>();
	private boolean requiresReview = false;
	
	public String getSpecTitle() {
		return specTitle;
	}

	public void setSpecTitle(String specTitle) {
		this.specTitle = specTitle;
	}

	public List<SpecDetails> getSpecDetails() {
		return specDetails;
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
		
		buffer.append(specTitle);
		
		if(requiresReview) {
			buffer.append(" !requires-review!");
		}
		
		buffer.append(": [");
		
		String comma = "";
		for (SpecDetails detail : specDetails) {
			buffer.append(comma);
			buffer.append(detail.toString());
			comma = ", ";
		}
		buffer.append("]");
		
		return buffer.toString();
	}
	
}
