package org.malibu.ibanez;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.malibu.ibanez.api.Spec;
import org.malibu.ibanez.api.SpecDetails;

public class GuitarSpecParser {
	
	private static final String CELL_DATA_SPLIT_REGEX = "/|:|@";
	private static final String CELL_DATA_SPLIT_IGNORE_SLASHES_REGEX = ":|@";
	
	private static final Pattern SINGLE_DATE_REGEX = Pattern.compile("(.*)(\\d{4})(.*)");
	private static final Pattern DATE_RANGE_REGEX = Pattern.compile("(.*)(\\d{4})(?:-|–)(\\d{4})(.*)");
	
	public static Spec parseSpec(Element element) {
		return parseSpec(element, true);
	}
	
	public static Spec parseSpec(Element element, boolean ignoreSlashes) {
		Spec guitarSpec = new Spec();
		
		SpecDetails spec = null;
		List<String> cellContents = getCellContents(element, ignoreSlashes);
		for (String text : cellContents) {
			spec = new SpecDetails();
			if(DATE_RANGE_REGEX.matcher(text).matches()) {
				Matcher m = DATE_RANGE_REGEX.matcher(text);
				m.find();
				spec.getYearsProduced().addAll(getYearsInRange(m.group(2), m.group(3)));
				String description = m.group(1) + m.group(4);
				description = description.trim();
				spec.setDescription(description);
			} else if (SINGLE_DATE_REGEX.matcher(text).matches()) {
				Matcher m = SINGLE_DATE_REGEX.matcher(text);
				m.find();
				spec.getYearsProduced().addAll(getYearsInRange(m.group(2), m.group(2)));
				String description = m.group(1) + m.group(3);
				description = description.trim();
				spec.setDescription(description);
			} else {
				text = text.trim();
				spec.setDescription(text);
			}
			guitarSpec.getSpecDetails().add(spec);
		}
		
		return guitarSpec;
	}
	
	private static List<Integer> getYearsInRange(String start, String end) {
		List<Integer> results = new ArrayList<>();
		int startYear = Integer.parseInt(start);
		int endYear = Integer.parseInt(end);
		for(int year = startYear; year <= endYear; year++) {
			results.add(year);
		}
		return results;
	}
	
	private static List<String> getCellContents(Element element, boolean ignoreSlashes) {
		List<String> result = new ArrayList<>();
		
		element.select("br").append("@");
		String splitRegex = ignoreSlashes ? CELL_DATA_SPLIT_IGNORE_SLASHES_REGEX : CELL_DATA_SPLIT_REGEX;
		String text = element.text();
		String[] cellContents = text.split(splitRegex);
		for(int i = 1; i < cellContents.length; i++) {
			String trimedText = cellContents[i].trim();
			if(trimedText.length() != 0) {
				result.add(trimedText);
			}
		}
		
		return result;
	}
	
}
