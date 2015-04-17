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
			getParameters();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void getParameters() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse("http://www.ebi.ac.uk/Tools/services/rest/emboss_needle/parameters/");
		doc.getDocumentElement().normalize();

		//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		if (doc.hasChildNodes()) {
			System.out.println(parseGetParametersXML(doc.getChildNodes()));
		}
	}

	private static List<String> parseGetParametersXML(NodeList nodeList) {
		List<String> parametersList = new LinkedList<String>();
		
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node tempNode = nodeList.item(count);

			// make sure it's element node.
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
				// get node name and value
				/*System.out.println("\nNode Name =" + tempNode.getNodeName() + " [OPEN]");
				System.out.println("Node Value =" + tempNode.getTextContent());

				if (tempNode.hasAttributes()) {
					// get attributes names and values
					NamedNodeMap nodeMap = tempNode.getAttributes();

					for (int i = 0; i < nodeMap.getLength(); i++) {
						Node node = nodeMap.item(i);
						System.out.println("attr name : " + node.getNodeName());
						System.out.println("attr value : " + node.getNodeValue());
					}
				}*/
				
				System.out.println(tempNode.getNodeName() + " - " + tempNode.getTextContent());
				if (tempNode.getNodeName().equals("id"))
					parametersList.add(tempNode.getTextContent());

				if (tempNode.hasChildNodes()) {
					// loop again if has child nodes
					parametersList.addAll(parseGetParametersXML(tempNode.getChildNodes()));
				}

				//System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");
			}
		}
		
		return parametersList;
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
