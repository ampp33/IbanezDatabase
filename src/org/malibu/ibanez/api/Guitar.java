package org.malibu.ibanez.api;

import java.util.ArrayList;
import java.util.List;

public class Guitar {
	
	private String description;
	private String modelName;
	private List<String> imageUrls = new ArrayList<>();
	private List<Spec> specs = new ArrayList<>();
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public List<String> getImageUrls() {
		return imageUrls;
	}
	public List<Spec> getSpecs() {
		return specs;
	}
	public Spec getSpecByName(String specName) {
		return specs.stream()
						.filter(spec -> spec.getSpecTitle().equalsIgnoreCase(specName))
						.findFirst()
						.orElse(null);
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("Model: ");
		buffer.append(modelName);
		buffer.append("\n");
		
		buffer.append("Description: ");
		buffer.append(description);
		buffer.append("\n");
		
		for (Spec spec : specs) {
			buffer.append(spec.toString());
			buffer.append("\n");
		}
		
		if(imageUrls.size() > 0) {
			buffer.append("\n");
			buffer.append("Image Urls:");
			buffer.append("\n");
			for (String imgUrl : imageUrls) {
				buffer.append(imgUrl);
				buffer.append("\n");
			}
		}
		
		return buffer.toString();
	}
	
}
