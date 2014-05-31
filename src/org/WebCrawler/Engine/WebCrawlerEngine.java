package org.WebCrawler.Engine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.WebCrawler.Utils.UrlUtils;


public class WebCrawlerEngine {
	
	private final static Logger LOGGER = Logger.getLogger(WebCrawlerEngine.class.getName()); 
	
	private UrlBasket basket;
	private URL rootLink;
	private ThreadGroup workersGroup;
	private String outputFilename;
	private boolean ethical;
	
	/**
	 * Basic constructor
	 * ethical set to true by default
	 * output filename set to "webcrawlerresults_yyyMMddhhmmss.txt"
	 * @param link - starter url
	 * @param maxUrls - max number of valid urls to find
	 */
	public WebCrawlerEngine(String link, int maxUrls) {
		this(link, maxUrls, true, "webcrawlerresults_"+(new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime())+".txt"));
	}
	
	/**
	 * Complete constructor
	 * set ethical to false to ignore robot.txt files from hosts
	 * @param link
	 * @param maxUrls
	 * @param ethical
	 * @param filename
	 */
	public WebCrawlerEngine(String link, int maxUrls, boolean ethical, String filename) {
		this.basket = new UrlBasket(maxUrls);
		this.ethical = ethical; 
		LOGGER.setLevel(Level.ALL);
		try {
			File f = new File(filename);
			outputFilename = f.getAbsolutePath();  //just to be sure to print the absolute path of the file in the results
			this.rootLink = UrlUtils.verifyUrl(link);
			this.workersGroup = new ThreadGroup("workers");
		} catch (MalformedURLException e) {
			this.rootLink = null;
			this.workersGroup = null;
			LOGGER.warning(e.getMessage());
		} catch (Exception e) {
			
		}
	}
	
	public String crawl() {
		if(rootLink == null) return "Invalid root url, please try again";
		WebCrawlerWorker worker = new WebCrawlerWorker(rootLink, basket, ethical);
		Thread workerThread = new Thread(workersGroup, worker);
		workerThread.start();
		try {
			workerThread.join();
		} catch (InterruptedException e1) {
			LOGGER.severe(e1.getMessage());
		}
		String result = null;
		
		if(basket.countFoundUrls()>0) {
			try {
				FileWriter fWriter = new FileWriter(outputFilename);
				for (URL url : basket.getFoundUrls()) {
					fWriter.write(url.toString()+System.getProperty("line.separator"));
				}
				fWriter.close();
			} catch (IOException e) {
				result = "Error saving output to file";
			}
		}
		result = "Crawler found "+basket.countFoundUrls()+" URLs, results saved to file "+outputFilename;
		return result;
	}

	public URL getRootLink() {
		return rootLink;
	}

	public void setRootLink(URL rootLink) {
		this.rootLink = rootLink;
	}

	public UrlBasket getBasket() {
		return basket;
	}

	public void setBasket(UrlBasket basket) {
		this.basket = basket;
	}

	public ThreadGroup getWorkersGroup() {
		return workersGroup;
	}

	public void setWorkersGroup(ThreadGroup workersGroup) {
		this.workersGroup = workersGroup;
	}

	public String getOutputFilename() {
		return outputFilename;
	}

	public void setOutputFilename(String outputFilename) {
		this.outputFilename = outputFilename;
	}

	public boolean isEthical() {
		return ethical;
	}

	public void setEthical(boolean ethical) {
		this.ethical = ethical;
	}

}
