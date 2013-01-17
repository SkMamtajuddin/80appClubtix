package com.eightylegs.customer.app;

import java.awt.Event;
import java.awt.image.SampleModel;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.eightylegs.app.parselinks.ParselinksHelper;
import com.eightylegs.cp.link.CrawlPackageLinkSet;
import com.eightylegs.cp.xml.config.DefaultRegexes;
import com.eightylegs.cp.xml.domain.attribute.DocumentAttribute;
import com.eightylegs.cp.xml.domain.attribute.EventAttribute;
import com.eightylegs.cp.xml.domain.attribute.FeatureAttribute;
import com.eightylegs.cp.xml.domain.attribute.IdentityAttribute;
import com.eightylegs.cp.xml.domain.attribute.LocationAttribute;
import com.eightylegs.cp.xml.domain.attribute.MerchantAttribute;
import com.eightylegs.cp.xml.domain.attribute.ProductAttribute;
import com.eightylegs.cp.xml.domain.attribute.TicketAttribute;
import com.eightylegs.cp.xml.domain.tag.common.EventTag;
import com.eightylegs.cp.xml.domain.tag.common.FeatureTag;
import com.eightylegs.cp.xml.domain.tag.common.FeaturesTag;
import com.eightylegs.cp.xml.domain.tag.common.LocationsTag;
import com.eightylegs.cp.xml.domain.tag.common.MerchantTag;
import com.eightylegs.cp.xml.domain.tag.common.MerchantsTag;
import com.eightylegs.cp.xml.domain.tag.common.RelativesTag;
import com.eightylegs.cp.xml.domain.tag.common.TicketTag;
import com.eightylegs.cp.xml.domain.tag.common.TicketsTag;
import com.eightylegs.cp.xml.domain.tag.secondlevel.IdentityTag;
import com.eightylegs.cp.xml.domain.tag.secondlevel.LocationTag;
import com.eightylegs.cp.xml.domain.tag.secondlevel.ProductTag;
import com.eightylegs.cp.xml.domain.tag.toplevel.DocumentTag;
import com.eightylegs.cp.xml.util.CrawlPackageUtils;
import com.eightylegs.cp.xml.util.CrawlPackageXMLDocument;
import com.eightylegs.customer.Default80AppPropertyKeys;
import com.eightylegs.customer.I80App;

public class Clubtix80app implements I80App 
{

	private static final String BASE_URL = "http://www.clubtix.com/";
	
	private static final String CHARSET = "UTF-8";
	
	public String getVersion()
	{
		return "80App_1.2";  
	}
	
	public void initialize(Properties properties, byte[] data)
	{ 
		
	}

	public Collection<String> parseLinks(byte[] documentContent, String url, Map<String, String> headers, Map<Default80AppPropertyKeys, Object> default80AppProperties, String statusCodeLine){
		try{
			CrawlPackageLinkSet linkSet = new CrawlPackageLinkSet(new URL(BASE_URL), documentContent, CHARSET);
			HashSet<String> redirectLink = ParselinksHelper.checkForHTTP30XRedirects(url, headers, statusCodeLine,null);
			String documentString = new String(documentContent, CHARSET);
			if (redirectLink != null)
				return redirectLink;
			else{
				if(url.contains("latest_events")){
					linkSet.addLinksForRegex("<a  class='eventNameLink'[^>]*href='([^>]*)'");
					
				}
			}
		return linkSet.getOutlinkSet();		
} 
catch (Exception e){
	e.printStackTrace();
}
return new ArrayList<String>();
}
	public byte[] processDocument(byte[] documentContent, String url, Map<String, String> headers, Map<Default80AppPropertyKeys, Object> default80AppProperties, String statusCodeLine)
	{		
		if (!statusCodeLine.contains("200"))
			return null;
		try{
			if(! url.contains("latest_events")){	  
				DocumentTag documentTag = new DocumentTag();
				documentTag.setAttributeRegex(DocumentAttribute.title, DefaultRegexes.DOCUMENT_TITLE);
				documentTag.setAttributeRegexList(DocumentAttribute.meta_description,new String[] { DefaultRegexes.META_DESCRIPTION,DefaultRegexes.OG_META_DESCRIPTION });
				documentTag.setAttributeRegex(DocumentAttribute.meta_keywords, DefaultRegexes.META_KEYWORDS);
				documentTag.setAttributeValue(DocumentAttribute.date_crawled, CrawlPackageUtils.getCurrentDate());
				documentTag.setAttributeValue(DocumentAttribute.v, CrawlPackageUtils.getLatestVersion());
				
				Map<String, String> priceReplacementValues = new HashMap<String, String>();
				priceReplacementValues.put("^", "USD "); 
				priceReplacementValues.put("$", "");
				priceReplacementValues.put(",", "");
			
				EventTag eventTag = new EventTag();
				eventTag.setAttributeRegex(EventAttribute.date, "<p class='b justLeft bigText'>([^>]*)</p>",new SimpleDateFormat("EEE, MMM dd, yyyy"));
				eventTag.setAttributeRegex(EventAttribute.description, "Event Information([\\s\\S]*?)</div>");
				eventTag.setAttributeRegex(EventAttribute.host, "<p[^>]*>Presented By:(.*?)</p>");
				eventTag.setAttributeRegex(EventAttribute.id, "<a href='[^>]*-(\\d+)");
				eventTag.setAttributeRegex(EventAttribute.name, "<h3 class='justLeft justTop'[^>]*>([^>]*)</h3>");
				
				TicketsTag ticketsTag = new TicketsTag("Ticket Type:[\\s\\S]*?<table class='borderTable");
				TicketTag ticketTag = new TicketTag();
				ticketTag.setAttributeRegex(TicketAttribute.date_end, "<td class='venter center'>[^>]*>[^$]([^>]*)</p>" ,new SimpleDateFormat());
				ticketTag.setAttributeRegex(TicketAttribute.price, "<td class='venter center'>[^>]*>\\$([^>]*)</p>",priceReplacementValues);
				ticketTag.setAttributeRegex(TicketAttribute.ticket_type, "<td class='venter'>([\\s\\S]*?)</td>");
				ticketsTag.addChildTagsMatchingRegex(ticketTag,"<tr class='bgwhite'>[\\s\\S]*?\\$.*?</p></td>");
				eventTag.addChildTag(ticketsTag);	
				
				LocationTag locationTag = new LocationTag();
				locationTag.setAttributeRegex(LocationAttribute.address,"var mydescription.*?<br/>([^>]*)<br/>");
				locationTag.setAttributeRegex(LocationAttribute.capacity,"Capacity:([^>]*)</p>");
				locationTag.setAttributeRegex(LocationAttribute.locality,"var mydescription.*?<br/>[^>]*<br/>([^>]*),");
				locationTag.setAttributeRegex(LocationAttribute.name,"<h3 class='justTop '>([^>]*)</h3>");
				locationTag.setAttributeRegex(LocationAttribute.phone,"Info Line:([^>]*)</p>");
				locationTag.setAttributeRegex(LocationAttribute.postal_code,"var mydescription.*?<br/>[^>]*<br/>[^>]*,[^>]*(\\d{5})");
				locationTag.setAttributeRegex(LocationAttribute.region,"var mydescription.*?<br/>[^>]*<br/>[^>]*,([^>]*)\\d{5}");
			    eventTag.addChildTag(locationTag);			
			
				documentTag.addChildTag(eventTag);				
				return CrawlPackageXMLDocument.generateXMLAsEncodedByteArray(documentContent, url, documentTag, CHARSET);
			}
		}			
		catch ( Exception e ){
			e.printStackTrace(); 
		}
		return null;
	}
}