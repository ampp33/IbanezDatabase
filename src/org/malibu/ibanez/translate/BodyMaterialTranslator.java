package org.malibu.ibanez.translate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.malibu.ibanez.api.Guitar;
import org.malibu.ibanez.api.Spec;
import org.malibu.ibanez.api.SpecDetails;

public class BodyMaterialTranslator implements SpecDetailTranslator {
	
	// SIDES?
	
	private static final String SPLIT_TOKENS_FIRST_STAGE = "w/| with |,";
	private static final String SPLIT_TOKENS_SECOND_STAGE = " and |\\&";
	private static final String SPLIT_TOKENS_ALL = SPLIT_TOKENS_FIRST_STAGE + "|" + SPLIT_TOKENS_SECOND_STAGE;
	
	// suffixes
	private static final String TOP = "top";
	private static final String BINDING = "binding";
	private static final String BACK = "back";
	private static final String PURFLING = "purfling";
	private static final String INLAY = "inlay";
	
	// spec names
	private static final String BODY_MATERIAL = "Body Material";
	private static final String TOP_MATERIAL = "Top Material";
	private static final String BINDING_MATERIAL = "Binding Material";
	private static final String BACK_MATERIAL = "Back Material";
	private static final String PURFLING_MATERIAL = "Purfling Material";
	private static final String INLAY_MATERIAL = "Inlay";
	
	private TranslationReport report = null;
	
	public TranslationReport translate(Guitar guitar, Spec parentSpec, SpecDetails specDetail) {
		report = new TranslationReport();
		// null checks
		if(parentSpec != null && BODY_MATERIAL.equalsIgnoreCase(parentSpec.getSpecTitle())
				&& specDetail != null && specDetail.getDescription() != null) {
			
			String cleanedDescription = specDetail.getDescription().replaceAll("\s+", " ").trim();
			
			// split spec detail text by known separator tokens so we detect if splitting needs to occur anywhere in the text
			String[] allSplit = cleanedDescription.split(SPLIT_TOKENS_ALL);
			
			// only perform detail splitting if ANY split tokens were identified
			if(allSplit.length > 1) {
				
				// split spec detail text by separator tokens that don't imply pairing (ex: ignoring things like 'and')
				String[] firstStageSplit = cleanedDescription.split(SPLIT_TOKENS_FIRST_STAGE);
				
				for(int firstStgTokenIdx = 0; firstStgTokenIdx < firstStageSplit.length; firstStgTokenIdx++) {
					
					String firstStageToken = firstStageSplit[firstStgTokenIdx].trim();
					
					// first stage token handling
					boolean firstStageMatchFound = false;
					if(firstStageToken.toLowerCase().contains(TOP + " and " + BACK)) {
						updateOrAddSpecAndDetail(guitar, TOP_MATERIAL, removeSpecTitleFromDescription(TOP + " and " + BACK, firstStageToken), specDetail.getYearsProduced());
						updateOrAddSpecAndDetail(guitar, BACK_MATERIAL, removeSpecTitleFromDescription(TOP + " and " + BACK, firstStageToken), specDetail.getYearsProduced());
						firstStageMatchFound = true;
					} else if (firstStageToken.toLowerCase().contains(TOP + " & " + BACK)) {
						updateOrAddSpecAndDetail(guitar, TOP_MATERIAL, removeSpecTitleFromDescription(TOP + " & " + BACK, firstStageToken), specDetail.getYearsProduced());
						updateOrAddSpecAndDetail(guitar, BACK_MATERIAL, removeSpecTitleFromDescription(TOP + " & " + BACK, firstStageToken), specDetail.getYearsProduced());
						firstStageMatchFound = true;
					}
					
					// TODO This isn't handling the possiblity that the first token entry is something like 'Ash top & back',
					// since this is tricky, because it implies that we don't know the "body" material, just the top and back.
					// Not sure if this can be handled...
					
					// do second stage token handling ONLY if no first stage matches were found
					if(!firstStageMatchFound) {
						
						// split spec detail text by separator tokens that DO imply pairing (ex: things like 'and')
						String[] secondStageSplit = firstStageToken.split(SPLIT_TOKENS_SECOND_STAGE);
						List<String> pendingTokens = new ArrayList<>();
						
						for(int secondStgTokenIdx = 0; secondStgTokenIdx < secondStageSplit.length; secondStgTokenIdx++) {
							
							String secondStageToken = secondStageSplit[secondStgTokenIdx].trim();
							pendingTokens.add(secondStageToken);
							
							// determine what attribute the spec detail token belongs to and add/update the corresponding spec
							if(secondStageToken.toLowerCase().contains(TOP)) {
								updateOrAddSpecAndDetail(guitar, TOP_MATERIAL, removeSpecTitleFromTokens(TOP, pendingTokens), specDetail.getYearsProduced());
								pendingTokens.clear();
							} else if (secondStageToken.toLowerCase().contains(BINDING)) {
								updateOrAddSpecAndDetail(guitar, BINDING_MATERIAL, removeSpecTitleFromTokens(BINDING, pendingTokens), specDetail.getYearsProduced());
								pendingTokens.clear();
							} else if (secondStageToken.toLowerCase().contains(BACK)) {
								updateOrAddSpecAndDetail(guitar, BACK_MATERIAL, removeSpecTitleFromTokens(BACK, pendingTokens), specDetail.getYearsProduced());
								pendingTokens.clear();
							} else if (secondStageToken.toLowerCase().contains(PURFLING)) {
								updateOrAddSpecAndDetail(guitar, PURFLING_MATERIAL, removeSpecTitleFromTokens(PURFLING, pendingTokens), specDetail.getYearsProduced());
								pendingTokens.clear();
							} else if (secondStageToken.toLowerCase().contains(INLAY)) {
								updateOrAddSpecAndDetail(guitar, INLAY_MATERIAL, removeSpecTitleFromTokens(INLAY, pendingTokens), specDetail.getYearsProduced());
								pendingTokens.clear();
							}
							
							if(firstStgTokenIdx == 0 && secondStgTokenIdx == 0) {
								// the very first token should be the actual body material so set the original spec to have the first detail token
								specDetail.setDescription(secondStageToken);
								report.specUpdated();
								// indicate that 
								pendingTokens.remove(secondStageToken);
								continue;
							}
							
						}
						
						if(pendingTokens.size() > 0) {
							System.err.println(String.format("No match for '%s' '%s' split details %s, assuming they're part of the body material, but flagging them for review", 
																	guitar.getModelName(), parentSpec.getSpecTitle(), Arrays.toString(pendingTokens.toArray())));
							updateOrAddSpecAndDetail(guitar, BODY_MATERIAL, pendingTokens, specDetail.getYearsProduced());
							pendingTokens.clear();
						}
					}
				}
			}
		}
		
		return report;
	}
	
	private void updateOrAddSpecAndDetail(Guitar guitar, String specTitle, String specDetailDescription, List<Integer> yearsProduced) {
		updateOrAddSpecAndDetail(guitar, specTitle, Arrays.asList(specDetailDescription), yearsProduced);
	}
	
	private void updateOrAddSpecAndDetail(Guitar guitar, String specTitle, List<String> specDetailDescriptionTokens, List<Integer> yearsProduced) {
		Spec spec = guitar.getSpecByName(specTitle);
		if(spec == null) {
			spec = new Spec();
			spec.setSpecTitle(specTitle);
			guitar.getSpecs().add(spec);
			report.specListModified();
		}
		for (String specDetailDescription : specDetailDescriptionTokens) {
			SpecDetails specDetail = new SpecDetails();
			specDetail.setDescription(specDetailDescription);
			specDetail.getYearsProduced().addAll(yearsProduced);
			spec.getSpecDetails().add(specDetail);
		}
		report.specDetailListModified();
	}
	
	private List<String> removeSpecTitleFromTokens(String specTitle, List<String> tokens) {
		return tokens.stream()
				.map( token -> removeSpecTitleFromDescription(specTitle, token) )
				.collect(Collectors.toList());
	}
	
	private String removeSpecTitleFromDescription(String specTitle, String description) {
		return description.replaceAll("(?i)" + specTitle, "").replaceAll("\s+", " ").trim();
	}

}
