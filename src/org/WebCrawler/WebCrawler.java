package org.WebCrawler;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.WebCrawler.Engine.WebCrawlerEngine;

public class WebCrawler {

	public static void main(String[] args) {
		String syntax = "Arguments missing! Syntax: java -jar WebCrawler.jar {root_url} {options}"
				+ "\n\nOptions:"
				+ "\n\nmax_urls=<n>         sets max number of urls"
				+ "\n\nethical=true/false   sets ethical behaviour, if true the crawler will check robots.txt for allowed paths, defaults to true"
				+ "\n\nfilename=<filename>  sets a specific output filename, defaults to ./webcrawlerresults_yyyyMMddHHmmss.txt ";
		if(args==null || args.length==0) { //checks for arguments and returns syntax
			System.out.println(syntax);
		}else {
			int max_urls = 1000;
			boolean ethical = true;
			String filename = "webcrawlerresults_"+(new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime())+".txt");
			String root_url = args[0];
			for (int i = 1; i < args.length; i++) {
				try {
					if(args[i].startsWith("max_urls")){
						max_urls = Integer.parseInt(args[i].substring("max_urls=".length()));
					}else if(args[i].startsWith("ethical")) {
						ethical = Boolean.parseBoolean(args[i].substring("ethical=".length()));
					}else if(args[i].startsWith("filename")) {
						filename = args[i].substring("filename=".length());
					}
				}catch(Exception ex) {
					System.out.println("Error while parsing arguments:");
					System.out.println(ex.getMessage());
				}
			}

			System.out.println("Web Crawler Started!");
			WebCrawlerEngine engine = new WebCrawlerEngine(root_url, max_urls, ethical, filename);
			String result = engine.crawl();
			System.out.println(result);	
		}
	}

}
