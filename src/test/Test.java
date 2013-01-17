package test;

import com.eightylegs.cp.test.CrawlPackageTestUtility;

public class Test {
	
	public static void main(String[] args) {
		try {					
			String 
			url="http://www.clubtix.com/latest_events";
			int depthLevel = 0;
			CrawlPackageTestUtility.setDebugMode(true);
			
			// 1.  Single-File Test - Use this to run a local test with a local file that you have downloaded and saved as "test.html" on your desktop
			//CrawlPackageTestUtility.testLocal("com.eightylegs.customer.app.Ford80app", System.getProperty("user.home")+"\\Desktop\\test2.html", depthLevel, url, null);
			
			// 2.  Live Crawl Test - Use this to run a live crawl from your local machine
			CrawlPackageTestUtility.testLiveDepthFirst("com.eightylegs.customer.app.Clubtix80app", url, depthLevel, 100, true, null);
		} 
		catch ( Exception e ) {
			e.printStackTrace();
		}

	}

}
