package org.malibu.ibanez;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.malibu.ibanez.api.Guitar;
import org.malibu.ibanez.dao.IbanezDao;
import org.malibu.ibanez.dao.IbanezDatabaseDao;
import org.malibu.ibanez.dao.IbanezException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IbanezWikiCrawler {
	
	private static final Logger log = LoggerFactory.getLogger(IbanezWikiCrawler.class);
	
	/*
	 * Good test guitars for goofy data: 
	 * 
	 * https://ibanez.fandom.com/wiki/540P%E2%85%A1-HH
	 * 
	 */
	
	public static void main(String[] args) throws IOException, SQLException {
		IbanezWikiCrawler crawler = new IbanezWikiCrawler();
		crawler.process();
	}
	
	private static final String ROOT_WIKI_URL = "https://ibanez.fandom.com";
	private static final String GUITAR_MODEL_PAGE_URL = "https://ibanez.fandom.com/wiki/Category:Guitar_models";
	private static final int NUMBER_OF_PROCESOR_THREADS = 5;
	private static final Object URL_LOCK = new Object();
	
	private final List<String> guitarUrls = new LinkedList<>();
	
	public void process() throws IOException, SQLException {
		
		log.info("Connecting to target database...");
		IbanezDao dao = new IbanezDatabaseDao("ibanez");
		
		log.info("retrieving guitar urls...");
		
		retrieveGuitarUrls();
		
		log.info(guitarUrls.size() + " urls found.");
		
		log.info("starting " + NUMBER_OF_PROCESOR_THREADS + " threads to process page data...");
		
		ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_PROCESOR_THREADS);
		for(int executorIndex = 0; executorIndex < NUMBER_OF_PROCESOR_THREADS; executorIndex++) {
			final int threadId = executorIndex;
			executor.execute(() -> {
				String guitarUrl = null;
				while((guitarUrl = getNextGuitarUrl()) != null) {
					log.info("thread " + threadId + " processing guitar: " + guitarUrl);
					Guitar guitar = null;
					try {
						guitar = GuitarPageParser.parseGuitarSpecPage(guitarUrl);
						dao.storeGuitar(guitar);
					} catch (IOException | IbanezException | RuntimeException e) {
						log.error("thread dying due to error", e);
						return;
					}
//					log.info("thread " + threadId + " done processing guitar: " + guitarUrl);
				}
			});
		}
		executor.shutdown();
		while(!executor.isTerminated());
		
		log.info("Closing database connection...");
		try {
			dao.close();
		} catch (Exception e) {
			log.error("Failed to close connection");
		}
		
		log.info("done!");
	}
	
	private String getNextGuitarUrl() {
		synchronized (URL_LOCK) {
			if(guitarUrls.size() > 0) {
				if(guitarUrls.size() % 300 == 0) {
					log.info(guitarUrls.size() + " remaining to be processed...");
				}
				return guitarUrls.remove(0);
			}
			return null;
		}
	}
	
	private void retrieveGuitarUrls() throws IOException {
		String currentPageUrl = GUITAR_MODEL_PAGE_URL;
		
		while(currentPageUrl != null) {
			
			// get all guitar links on the current catalog page
			Document doc = Jsoup.connect(currentPageUrl).get();
			Elements guitarUrlLinks = doc.select("a.category-page__member-link");
			
			for(int urlIndex = 0; urlIndex < guitarUrlLinks.size(); urlIndex++) {
				String urlStub = guitarUrlLinks.get(urlIndex).attr("href");
				String guitarUrl = ROOT_WIKI_URL + urlStub;
				guitarUrls.add(guitarUrl);
			}
			
			// find the url for the next catalog page
			Elements nextButtons = doc.select("a[class^='category-page__pagination-next']");
			if(nextButtons.size() == 1) {
				currentPageUrl = nextButtons.get(0).attr("href");
			} else {
				currentPageUrl = null;
			}
			
		}
	}

}
