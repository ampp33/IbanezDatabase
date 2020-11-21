package org.malibu.ibanez.api;

import java.util.ArrayList;
import java.util.List;

public class Spec {
	
	private String specTitle;
	List<SpecDetails> specDetails = new ArrayList<>();
	
	public String getSpecTitle() {
		return specTitle;
	}

	public void setSpecTitle(String specTitle) {
		this.specTitle = specTitle;
	}

	public List<SpecDetails> getSpecDetails() {
		return specDetails;
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		
		buffer.append(specTitle);
		buffer.append(": ");
		buffer.append(" [");
		
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
