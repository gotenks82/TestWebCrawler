package org.WebCrawler.Engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.WebCrawler.Utils.UrlUtils;

public class WebCrawlerWorker implements Runnable {
	private final static Logger LOGGER = Logger.getLogger(WebCrawlerWorker.class.getName()); 

	private UrlBasket basket;
	private URL link;
	private boolean ethical;


	public WebCrawlerWorker(URL link, UrlBasket basket, boolean ethical) {
		this.link = link;
		this.basket = basket;
		this.ethical = ethical;
		LOGGER.setLevel(Level.ALL);
	}


	@Override
	public void run() {
		if(this.link!=null && this.basket!=null) this.crawl();
	}

	private void crawl() {
		LOGGER.info("Visiting "+ link.toString());
		ArrayList<URL> list = getValidLinks(link);
		ArrayList<URL> urlToSpawn = new ArrayList<URL>();
		for (URL link : list) {
			if(!basket.isFull() && (!ethical || basket.checkRobot(link)) && basket.pushUrl(link)) {
				urlToSpawn.add(link);
			}
		}
		for (URL url : urlToSpawn) {
			if(basket.isFull()) break; //to avoid spawning unnecessary workers.
			spawnWorker(url, basket);
		}
	}

	private void spawnWorker(URL newlink, UrlBasket basket) {
		WebCrawlerWorker worker = new WebCrawlerWorker(newlink, basket, ethical);
		Thread thread = new Thread(Thread.currentThread().getThreadGroup(),worker);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e1) {
			LOGGER.severe(e1.getMessage());
		}
	}

	private String getUrlContents(URL link) {
		String contents = "";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(link.openStream()));
			String line;
			StringBuffer pageBuffer = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				pageBuffer.append(line);
			}
			contents = pageBuffer.toString();
		} catch (IOException e) {
			LOGGER.warning(e.getMessage());
		}
		return contents;
	}	

	private ArrayList<URL> getValidLinks(URL url){
		String pageContent = getUrlContents(url);
		Pattern p = Pattern.compile("<a\\s+href\\s*=\\s*\"?(.*?)[\"|>]", Pattern.CASE_INSENSITIVE); 
		ArrayList<URL> linkList = new ArrayList<URL>();
		Matcher m = p.matcher(pageContent);
		while (m.find()) {
			String link = m.group(1).trim();
			//skip page anchors, mailto links, javascript links
			if (link.length() < 1 || link.charAt(0) == '#' || link.indexOf("mailto:") != -1 || link.toLowerCase().indexOf("javascript") != -1) {
				continue;
			}    
			if (link.indexOf("://") == -1) {  //handle same host urls
				if (link.charAt(0) == '/') {
					link = "http://" + url.getHost() + link;
				} else {
					String file = url.getFile();
					if (file.indexOf('/') == -1) {
						link = "http://" + url.getHost() + "/" + link;
					} else {
						String path = file.substring(0, file.lastIndexOf('/') + 1);  
						link = "http://" + url.getHost() + path + link;
					}
				}
			}
			// Remove anchors from link.
		    int index = link.indexOf('#');
		    if (index != -1) {
		      link = link.substring(0, index);
		    } 
		    link = UrlUtils.removeWww(link);
		    URL newurl = null;
		    try {
				newurl = UrlUtils.verifyUrl(link);
			} catch (MalformedURLException e) {
			}
		    if(newurl!=null) linkList.add(newurl);
		}
		return linkList;
	}
}
