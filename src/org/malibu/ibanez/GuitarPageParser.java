package org.malibu.ibanez;

import static org.malibu.ibanez.GuitarSpecParser.parseSpec;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.malibu.ibanez.api.Guitar;
import org.malibu.ibanez.api.Spec;

public class GuitarPageParser {
	
	private static final List<String> SPECS_TO_IGNORE = Arrays.asList("Model name");
	private static final List<String> SPECS_TO_SPLIT_ON_SLASHES = Arrays.asList("Finish(es)", "Controls");
	
	public static Guitar parseGuitarSpecPage(String url) throws IOException {
		URLConnection urlConn = new URL(url).openConnection();
		urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
		Document doc = Jsoup.parse(urlConn.getInputStream(), "UTF-8", url);
		Guitar guitar = parsePageDocument(doc);
		return guitar;
	}
	
	private static Guitar parsePageDocument(Document doc) {
		Guitar guitar = new Guitar();
		
		parseModelName(doc, guitar);
		parseDescription(doc, guitar);
		populateImageUrls(doc, guitar);
		parseSpecCells(doc, guitar);
		
		return guitar;
	}

	private static void parseModelName(Document doc, Guitar guitar) {
		String modelName = doc.select("h1.page-header__title").get(0).text();
		guitar.setModelName(modelName);
	}

	private static void populateImageUrls(Document doc, Guitar guitar) {
		List<String> imgUrls = new LinkedList<>();
		
		// main image url
		Elements purpleBoxes = doc.select("div.purplebox");
		// some guitars may not have a main image, and if so skip trying to grab the main image
		if(purpleBoxes.size() >= 2) {
			Elements mainImages = purpleBoxes.get(0).select("img");
			Element mainImg = getFirstImgWithUrlAsSrc(mainImages);
			if(mainImg != null) {
				String mainImgUrl = mainImg.attr("src");
				imgUrls.add(updateImgUrlScaleToMax(mainImgUrl));
			}
		}
		
		// aux images urls from bottom of page
		Elements imageSections = doc.select("span:containsOwn(Images)");
		// some guitars don't have an aux image section, so skip searching for those images
		// if the aux section doesn't exist
		if(imageSections.size() > 0) {
			Element auxImgsContainer = imageSections.get(0).parent().parent();
			Elements auxImgs = auxImgsContainer.select("div.wikiabox img");
			for(int imgIndex = 0; imgIndex < auxImgs.size(); imgIndex++) {
				Element auxImg = auxImgs.get(imgIndex);
				String auxImgUrl = auxImg.attr("src");
				// lazy loading here causes images with a data stream for its 'src' attribute,
				// but another img with the actual img URL follows, so just skip the data stream images
				if(!auxImgUrl.startsWith("data:")) {
					imgUrls.add(updateImgUrlScaleToMax(auxImgUrl));
				}
			}
		}
		
		guitar.getImageUrls().addAll(imgUrls);
	}
	
	private static void parseSpecCells(Document doc, Guitar guitar) {
		Elements specCells = doc.select("div.purplebox td");
		for (Element specCell : specCells) {
			Elements specTitleElements = specCell.select("span.blacklink b");
			if(specTitleElements.size() == 1) {
				Element specTitleElement = specTitleElements.get(0);
				String specTitle = specTitleElement.text().replace(":", "");
				if(!SPECS_TO_IGNORE.contains(specTitle)) {
					boolean ignoreSlashes = !SPECS_TO_SPLIT_ON_SLASHES.contains(specTitle);
					Spec spec = parseSpec(specCell, ignoreSlashes);
					spec.setSpecTitle(specTitle);
					guitar.getSpecs().add(spec);
				}
			}
		}
	}
	
	private static void parseDescription(Document doc, Guitar guitar) {
		boolean descriptionParagraphFound = false;
		Elements possibleDescriptionParagraphs = doc.getElementById("mw-content-text").children();
		
		StringBuilder buffer = new StringBuilder();
		String newline = "";
		for(Element possibleDescriptionParagraph : possibleDescriptionParagraphs) {
			if("p".equals(possibleDescriptionParagraph.tagName().toLowerCase())) {
				if(!descriptionParagraphFound) {
					descriptionParagraphFound = true;
				}
				buffer.append(newline).append(newline);
				buffer.append(possibleDescriptionParagraph.text());
				newline = "\n";
			} else if(descriptionParagraphFound) {
				break;
			}
		}
		
		guitar.setDescription(buffer.toString());
	}
	
	private static Element getFirstImgWithUrlAsSrc(Elements images) {
		for(int imgIndex = 0; imgIndex < images.size(); imgIndex++) {
			Element img = images.get(imgIndex);
			if(!img.attr("src").startsWith("data:")) {
				return img;
			}
		}
		return null;
	}
	
	private static String updateImgUrlScaleToMax(String imgUrl) {
		// this just chops off the revision and scaling url items
		return imgUrl.substring(0, imgUrl.indexOf("/revision"));
	}
	
}
