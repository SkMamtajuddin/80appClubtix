package com.eightylegs.cp.generator;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.eightylegs.common.domain.crawlpackagegenerator.CrawlPackageGeneratorJob;
import com.eightylegs.common.domain.crawlpackagegenerator.JobQueueGeneratorData;

public class ClubtixCPJobQueueGenerator extends CrawlPackageJobQueueGenerator 
{
	private static final String PARENT_URL = "http://www.clubtix.com/";
	private static final String PROJECT_NAME = "Clubtix";
	private static final String CYCLE = "CYCLE";
	private static final String DATE= "DATE";
		private static final String JOB_NAME_PATTERN = PROJECT_NAME + " - " + CYCLE + " - "+DATE;
	
	public static void main(String[] args) 
	{
		JobQueueGeneratorData data = new JobQueueGeneratorData();
		data.setCurrentCrawlCycleNumber(0);
		new ClubtixCPJobQueueGenerator().printTestData(data);
	}
		
	public List<CrawlPackageGeneratorJob> getCrawlPackageGeneratorJobs(JobQueueGeneratorData data) throws Exception 
	{
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy.MM.dd");
		String JQGDate = formatter.format(date);
		
		List<CrawlPackageGeneratorJob> jobs = new ArrayList<CrawlPackageGeneratorJob>();
		List<String> urls = new ArrayList<String>();
					urls.add("http://www.clubtix.com/latest_events");
		jobs.add(new CrawlPackageGeneratorJob(JOB_NAME_PATTERN.replace ("CYCLE", String.valueOf(data.getCurrentCrawlCycleNumber())).replace(DATE, JQGDate), urls));
		return jobs;
	}

	@Override
	public byte[] getCrawlPackageGeneratorState() 
	{
//		AutosAOL doesn't need to keep track of CPG state
		return null; 
	}
}
