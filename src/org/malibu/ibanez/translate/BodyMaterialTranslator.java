package org.malibu.ibanez.translate;

import java.util.List;

import org.malibu.ibanez.api.Guitar;
import org.malibu.ibanez.api.Spec;
import org.malibu.ibanez.api.SpecDetails;

public class BodyMaterialTranslator implements SpecDetailTranslator {
	
	// SIDES?
	
	// TODO: maintain list of woods and only use those (so we don't get words like "beautiful" or "sandwich"
	// have this list stored in the database
	
	private static final String SPLIT_TOKENS = "w/| with | and |\\&";
	
	// suffixes
	private static final String TOP = "top";
	private static final String BINDING = "binding";
	private static final String BACK = "back";
	private static final String PURFLING = "purfling";
	private static final String INLAY = "inlay";
	
	// spec names
	private static final String TOP_MATERIAL = "Top Material";
	private static final String BINDING_MATERIAL = "Binding Material";
	private static final String BACK_MATERIAL = "Back Material";
	private static final String PURFLING_MATERIAL = "Purfling Material";
	private static final String INLAY_MATERIAL = "Inlay";
	
	private TranslationReport report = null;
	
	public TranslationReport translate(Guitar guitar, Spec parentSpec, SpecDetails specDetail) {
		report = new TranslationReport();
		// null checks
		if(parentSpec != null && "Body Material".equalsIgnoreCase(parentSpec.getSpecTitle())
				&& specDetail != null && specDetail.getDescription() != null) {
			// split spec detail text by known separator tokens
			String[] detailTokens = specDetail.getDescription().split(SPLIT_TOKENS);
			// only perform detail splitting if multiple split tokens were identified
			if(detailTokens.length > 1) {
				for(int tokenIndex = 0; tokenIndex < detailTokens.length; tokenIndex++) {
					String token = detailTokens[tokenIndex].trim();
					// the first token should be the actual body material so set the original spec to have the first detail token
					if(tokenIndex == 0) {
						specDetail.setDescription(token);
						report.specUpdated();
						continue;
					}
					// determine what attribute the spec detail token belongs to and add/update the corresponding spec
					if(token.toLowerCase().endsWith(TOP)) {
						updateOrAddSpecAndDetail(guitar, TOP_MATERIAL, token, specDetail.getYearsProduced());
					} else if (token.toLowerCase().endsWith(BINDING)) {
						updateOrAddSpecAndDetail(guitar, BINDING_MATERIAL, token, specDetail.getYearsProduced());
					} else if (token.toLowerCase().endsWith(BACK)) {
						updateOrAddSpecAndDetail(guitar, BACK_MATERIAL, token, specDetail.getYearsProduced());
					} else if (token.toLowerCase().endsWith(PURFLING)) {
						updateOrAddSpecAndDetail(guitar, PURFLING_MATERIAL, token, specDetail.getYearsProduced());
					} else if (token.toLowerCase().endsWith(INLAY)) {
						updateOrAddSpecAndDetail(guitar, INLAY_MATERIAL, token, specDetail.getYearsProduced());
					} else {
						System.err.println(String.format("No match for '%s' split detail '%s'", parentSpec.getSpecTitle(), token));
					}
				}
			}
		}
		
		return report;
	}
	
	private void updateOrAddSpecAndDetail(Guitar guitar, String specTitle, String specDetailDescription, List<Integer> yearsProduced) {
		Spec spec = guitar.getSpecByName(specTitle);
		if(spec == null) {
			spec = new Spec();
			spec.setSpecTitle(specTitle);
			guitar.getSpecs().add(spec);
			report.specListModified();
		}
		SpecDetails specDetail = new SpecDetails();
		specDetail.setDescription(specDetailDescription);
		specDetail.getYearsProduced().addAll(yearsProduced);
		spec.getSpecDetails().add(specDetail);
		report.specDetailListModified();
	}

}
