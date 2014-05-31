package org.WebCrawler.Engine;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UrlBasket {
	private int maxUrls;
	private Map<String, ArrayList<String>> disallowListMap; 
	private ArrayList<URL> foundUrls;
	
	public UrlBasket(int max) {
		this.maxUrls = max;
		this.disallowListMap = new HashMap<String, ArrayList<String>>();
		this.foundUrls = new ArrayList<URL>();
	}
	
	public synchronized boolean pushUrl(URL newUrl) {
		boolean result = false;
		if(!isFull() && !foundUrls.contains(newUrl)) {
			foundUrls.add(newUrl);
			result = true;
		}
		return result;
	}
	
	
		
	
	public synchronized boolean isFull(){
		return countFoundUrls()>=maxUrls;
	}
	
	public synchronized int countFoundUrls() {
		return foundUrls.size();
	}
	
	public synchronized void addDisallowedPath(URL url ) {
		if(!disallowListMap.containsKey(url.getHost())){
			disallowListMap.put(url.getHost(), new ArrayList<String>());
		}
		disallowListMap.get(url.getHost()).add(url.getPath());
	}
	
	
	public synchronized boolean checkRobot(URL url) {
		boolean result = true;
		String host = url.getHost();
		ArrayList<String> disallowList = disallowListMap.get(host);
		if(disallowList == null) {
			disallowList = new ArrayList<String>();
			try {
				URL robotsFileUrl = new URL("http://" + host + "/robots.txt");
				BufferedReader robotReader = new BufferedReader(new InputStreamReader(robotsFileUrl.openStream()));
				String line;
			      while ((line = robotReader.readLine()) != null) {  //does not check for user-agent.
			        if (line.startsWith("Disallow:")) {
			          String disallowPath = line.substring("Disallow:".length());
			          int commentIndex = disallowPath.indexOf("#");
			          if (commentIndex != -1) {
			            disallowPath =
			              disallowPath.substring(0, commentIndex);
			          }
			          disallowPath = disallowPath.trim();
			          if(!disallowList.contains(disallowPath)) disallowList.add(disallowPath);
			        }
			      }
			      disallowListMap.put(host, disallowList);
			}catch(Exception e) {
				//robot.txt not found, every URL is allowed;
			}
		}
		for (String string : disallowList) {
			if(url.getFile().startsWith(string)) result = false;
		}
		return result;
	}
	

	public ArrayList<URL> getFoundUrls() {
		return foundUrls;
	}

	public void setFoundUrls(ArrayList<URL> foundUrls) {
		this.foundUrls = foundUrls;
	}

	public Map<String, ArrayList<String>> getDisallowListMap() {
		return disallowListMap;
	}

	public void setDisallowListMap(Map<String, ArrayList<String>> disallowListMap) {
		this.disallowListMap = disallowListMap;
	}

	public int getMaxUrls() {
		return maxUrls;
	}

	public void setMaxUrls(int maxUrls) {
		this.maxUrls = maxUrls;
	}

}
