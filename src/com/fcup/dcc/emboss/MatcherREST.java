package com.fcup.dcc.emboss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@SuppressWarnings("deprecation")
public class MatcherREST {
	static HttpClient client;
	
	public static void main(String[] args) {
		client = new DefaultHttpClient();
		try {
			/*
			for (String parameter : getParameters()) {
				getParameterDetails(parameter);
			}
			*/
			//getParameterDetails("matrix");
			String jobID = run();
			String status = getStatus(jobID);
			Boolean canProceed = false;
			while(!canProceed){
				if(status.equals("RUNNING")){
				}
				else if(status.equals("FINISHED")){
					canProceed = true;
				}
				else if(status.equals("ERROR")){
					System.out.println("Cant get the job status, try again");
				}
				else if(status.equals("FAILURE")){
					System.out.println("The job Failed! Program will exit");
				}
				else if(status.equals("NOT_FOUND")){
					System.out.println("Job can not be found! Program will exit");
				}
				Thread.sleep(4000);
				status = getStatus(jobID);
			}
			String result =getresult(jobID);
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<String> getParameters() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse("http://www.ebi.ac.uk/Tools/services/rest/emboss_matcher/parameters/");
		doc.getDocumentElement().normalize();

		List<String> parametersList = new LinkedList<String>();

		if (doc.hasChildNodes()) {
			NodeList nList = doc.getElementsByTagName("id");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);		 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					parametersList.add(nNode.getTextContent());
				}
			}
		}

		return parametersList;
	}

	public static void getParameterDetails(String parameter) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse("http://www.ebi.ac.uk/Tools/services/rest/emboss_matcher/parameterdetails/" + parameter);
		doc.getDocumentElement().normalize();

		if (doc.hasChildNodes()) {
			NodeList nList = doc.getElementsByTagName("name");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);		 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					System.out.println(nNode.getTextContent() + " (" + parameter + ")");
				}
			}
			
			nList = doc.getElementsByTagName("description");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);		 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					System.out.println(nNode.getTextContent());
				}
			}
			
			System.out.println();
		}
	}
	
	public static String run() throws SAXException, IOException, ParserConfigurationException {
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("http://www.ebi.ac.uk/Tools/services/rest/emboss_matcher/run/");
		
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("email", "up201003917@fc.up.pt"));
		params.add(new BasicNameValuePair("matrix", "EBLOSUM30"));
		params.add(new BasicNameValuePair("gapopen", "1"));
		params.add(new BasicNameValuePair("gapext", "1"));
		params.add(new BasicNameValuePair("alternatives", "1"));
		params.add(new BasicNameValuePair("stype", "protein"));
		params.add(new BasicNameValuePair("asequence", "AAAG"));
		params.add(new BasicNameValuePair("bsequence", "AAGG"));
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

		//Execute and get the response.
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();

		if (entity != null) {
		    InputStream instream = entity.getContent();
		    try {
		        // do something useful
		    	String jobID = slurp(instream, 256);
		    	System.out.println("FUNCIONA "+ jobID);
		    	return jobID;
		    } finally {
		        instream.close();
		    }
		}
		return null;
	}
	
	public static String slurp(final InputStream is, final int bufferSize)
	{
	  final char[] buffer = new char[bufferSize];
	  final StringBuilder out = new StringBuilder();
	  try (Reader in = new InputStreamReader(is, "UTF-8")) {
	    for (;;) {
	      int rsz = in.read(buffer, 0, buffer.length);
	      if (rsz < 0)
	        break;
	      out.append(buffer, 0, rsz);
	    }
	  }
	  catch (UnsupportedEncodingException ex) {
	    /* ... */
	  }
	  catch (IOException ex) {
	      /* ... */
	  }
	  return out.toString();
	}
	
	public static String getStatus(String jobID) throws ParserConfigurationException, SAXException, IOException {
		String result = makeRequest("http://www.ebi.ac.uk/Tools/services/rest/emboss_matcher/status/" + jobID);
		System.out.println("Status: "+ result);
		return result;
		
	}
	public static String getresult(String jobID) throws ParserConfigurationException, SAXException, IOException {
		String result = makeRequest("http://www.ebi.ac.uk/Tools/services/rest/emboss_matcher/result/" + jobID);
		return result;
	}

	public static String makeRequest(String url) throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(url);
		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
		String line = "";
		String fullText = "";
		
		while ((line = rd.readLine()) != null) {
			fullText += line+"\n";
		}

		return fullText;
	}
}
