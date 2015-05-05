import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

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
//import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;



class REST {
	static HttpClient client = HttpClientBuilder.create().build();
	/**
	 * 
	 * @param type of the alignment to do (matcher to do local, needle to global)
	 * @return the list of parameters to each of the alignment
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static List<String> getParameters(String type) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse("http://www.ebi.ac.uk/Tools/services/rest/emboss_"+type+"/parameters/");
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
	
	/**
	 * Print the detail on screen
	 * @param parameter to obtain the detail
	 * @param type type of the alignment to do (matcher to do local, needle to global)
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static void getParameterDetails(String parameter, String type) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse("http://www.ebi.ac.uk/Tools/services/rest/emboss_"+type+"matcher/parameterdetails/" + parameter);
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
	
	/**
	 * Run a service of local alignment on the server
	 * @param matrix
	 * @param gapopen
	 * @param gapext
	 * @param alternatives
	 * @param stype
	 * @param asequence
	 * @param bsequence
	 * @return the job ID allocated by the server
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static String runMatcher(String matrix, String gapopen, String gapext, String alternatives,
			String stype, String asequence, String bsequence) throws SAXException, IOException, ParserConfigurationException {
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("http://www.ebi.ac.uk/Tools/services/rest/emboss_matcher/run/");
		
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("email", "up201003917@fc.up.pt"));
		params.add(new BasicNameValuePair("matrix", matrix));
		params.add(new BasicNameValuePair("gapopen", gapopen));
		params.add(new BasicNameValuePair("gapext", gapext));
		params.add(new BasicNameValuePair("alternatives", alternatives));
		params.add(new BasicNameValuePair("stype", stype));
		params.add(new BasicNameValuePair("asequence", asequence));
		params.add(new BasicNameValuePair("bsequence", bsequence));
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

		//Execute and get the response.
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();

		if (entity != null) {
		    InputStream instream = entity.getContent();
		    try {
		        // do something useful
		    	String jobID = slurp(instream, 256);
		    	System.out.println("Trabalho submetido no servidor com id: "+ jobID);
		    	return jobID;
		    } finally {
		        instream.close();
		    }
		}
		return null;
	}
	
	/**
	 * Run a service of global alignment on the server
	 * @param matrix
	 * @param gapopen
	 * @param gapext
	 * @param endweight
	 * @param endopen
	 * @param endextend
	 * @param alternatives
	 * @param stype
	 * @param asequence
	 * @param bsequence
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static String runNeedle(String matrix, String gapopen, String gapext, String endweight,
			String endopen, String endextend, String alternatives,
			String stype, String asequence, String bsequence) throws SAXException, IOException, ParserConfigurationException {
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("http://www.ebi.ac.uk/Tools/services/rest/emboss_matcher/run/");
		
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("email", "up201003917@fc.up.pt"));
		params.add(new BasicNameValuePair("matrix", matrix));
		params.add(new BasicNameValuePair("gapopen", gapopen));
		params.add(new BasicNameValuePair("gapext", gapext));
		params.add(new BasicNameValuePair("endweight", endweight));
		params.add(new BasicNameValuePair("endopen", endopen));
		params.add(new BasicNameValuePair("endextend", endextend));
		params.add(new BasicNameValuePair("alternatives", alternatives));
		params.add(new BasicNameValuePair("stype", stype));
		params.add(new BasicNameValuePair("asequence", asequence));
		params.add(new BasicNameValuePair("bsequence", bsequence));
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

		//Execute and get the response.
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();

		if (entity != null) {
		    InputStream instream = entity.getContent();
		    try {
		        // do something useful
		    	String jobID = slurp(instream, 256);
		    	System.out.println("Trabalho submetido no servidor com id: "+ jobID);
		    	return jobID;
		    } finally {
		        instream.close();
		    }
		}
		return null;
	}
	/**
	 * Read from an inputStream
	 * @param is
	 * @param bufferSize
	 * @return
	 */
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
	
	/**
	 * 
	 * @param jobID the id of the job to get the status
	 * @param type of the alignment to do (matcher to do local, needle to global)
	 * @return the status of the job
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static String getStatus(String jobID, String type) throws ParserConfigurationException, SAXException, IOException {
		String result = makeRequest("http://www.ebi.ac.uk/Tools/services/rest/emboss_"+type+"/status/" + jobID);
		System.out.println("Status: "+ result);
		return result;
		
	}
	
	/**
	 * 
	 * @param jobID the id of the job to get the status
	 * @param output the type of output that we want
	 * @param type type of the alignment to do (matcher to do local, needle to global)
	 * @return the result of the job
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static String getresult(String jobID, String output, String type) throws ParserConfigurationException, SAXException, IOException {
		String result = makeRequest("http://www.ebi.ac.uk/Tools/services/rest/emboss_"+type+"/result/" + jobID+ "/"+ output);
		return result;
	}
	/**
	 * make a get request to an url
	 * @param url
	 * @return the text of the html response
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
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


public class PSA  {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		// TODO Auto-generated method stub
		
		//Read the files of the sequences
		String currentDir = System.getProperty("user.dir") + "/";
    	Path arg1 = Paths.get( currentDir + args[0] );
    	Path arg2 = Paths.get( currentDir + args[1] );
    	String x = readFile(arg1);
    	String y = readFile(arg2);
		
    	//get the type of alignment
    	String type = handleChoice();
		REST rest = new REST();
		
		try {	
			// set the parameters to run on server 
			String matrix = "EBLOSUM30";
			String gapopen = "1";
			String gapext = "1";
			String alternatives ="1";
			String stype = "protein";
			String asequence = x;
			String bsequence = y;
			
			String jobID;
			if(type.equals("local"))		
				jobID= REST.runMatcher(matrix, gapopen, gapext, alternatives, stype, asequence, bsequence);
			else{
				String endweight ="false" ;
				String  endopen = "1";
				String endextend = "1.0";
				jobID= REST.runNeedle(matrix, gapopen, gapext, endweight, endopen, endextend, alternatives, stype, asequence, bsequence);
			}
			String status;
			Boolean canProceed = false;
			//wait for the job to be finished
			while(!canProceed){
				status = REST.getStatus(jobID, type);
				if(status.equals("RUNNING\n")){
				}
				else if(status.startsWith("FINISHED")){
					canProceed = true;
				}
				else if(status.equals("ERROR\n")){
					System.out.println("Cant get the job status, try again");
				}
				else if(status.equals("FAILURE\n")){
					System.out.println("The job Failed! Program will exit");
				}
				else if(status.equals("NOT_FOUND\n")){
					System.out.println("Job can not be found! Program will exit");
				}
				//wait 4 seconds to check the status on server
				if(!canProceed)
					Thread.sleep(4000);
			}
			String result =REST.getresult(jobID, "out", type);
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 	
	 * @return the choice of the user about what alignment wants
	 */
	public static String handleChoice(){
		Scanner teclado = new Scanner(System.in);
		int choice;
		System.out.println("What type of alignment you want to do?");
		System.out.println("1 - Local");
		System.out.println("2 - Global");
		choice = teclado.nextInt();
		teclado.nextLine();
		if(choice==1)
			return "matcher";
		else
			return "needle";
		
	}
	/**
	 * 
	 * @param file to read
	 * @return a string resulting from the parsed file with first line ignored
	 */
	   public static String readFile(Path file){
	    	String temp = "";
			Charset charset = Charset.forName("US-ASCII");
			try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
				String line = reader.readLine(); // ignores the first line
				while ((line = reader.readLine()) != null) {
					temp = temp.concat(line);
				}
			    } catch (IOException x) {
			    System.err.format("IOException: %s%n", x);
			}
			return temp;
	    }

}
