package com.fcup.dcc.emboss;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@SuppressWarnings("deprecation")
public class NeedleREST {
	static HttpClient client;

	public static void main(String[] args) {
		client = new DefaultHttpClient();
		try {
			for (String parameter : getParameters()) {
				getParameterDetails(parameter);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<String> getParameters() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse("http://www.ebi.ac.uk/Tools/services/rest/emboss_needle/parameters/");
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
		Document doc = dBuilder.parse("http://www.ebi.ac.uk/Tools/services/rest/emboss_needle/parameterdetails/" + parameter);
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

	/*public static String makeRequest(String url) throws ClientProtocolException, IOException {
		HttpGet request = new HttpPost(url);
		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
		String line = "";
		String fullText = "";
		while ((line = rd.readLine()) != null) {
			fullText += line;
		}

		return fullText;
	}*/
}
