package BYUIS.classes.MyCourses;
import java.io.IOException;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

// Here's my change -- luke80

public class BrainHoneyAccess 
{
	private static final String BRAIN_HONEY_URL = "https://gls.agilix.com/dlap.ashx";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String TEXT_XML_TYPE = "text/xml;";
	//private static final String SESSION_COOKIE_NAME = "AZT"; **COOKIES AUTOMATICALLY TAKEN CARE OF BY HTTP CLIENT**
	
	private HttpClient httpClient;		// Performs execution of internet calls
	private HttpResponse response;		// Container for the responses from the server
	private HttpEntity responseEntity;	// Container for the response entity from the server 
	
	/*
	* Prints out the response from BrainHoney.
	*/
	private void printResponseEntity() throws IllegalStateException, IOException
	{
		// Output the response
		Scanner reader = new Scanner(responseEntity.getContent());
		String responseString = "";
		while(reader.hasNext())
		{
			responseString += reader.nextLine() + "\n";
		}
		
		System.out.println(responseString);		
	}

	/*
	 * No-argument constructor.
	 */
	public BrainHoneyAccess()
	{
		this.httpClient = new DefaultHttpClient();
	}
	
	/*
	 * Logs into the BrainHoney.
	 */
	public void login() throws ClientProtocolException, IOException
	{
		// Set login information
		HttpPost httpPost = new HttpPost(BRAIN_HONEY_URL);
		httpPost.setHeader(CONTENT_TYPE, TEXT_XML_TYPE);
		//StringEntity loginXML = new StringEntity("<request cmd='login' username='byuistest/dbuser' password='@dM1n'/>");
		StringEntity loginXML = new StringEntity("<request cmd='login' username='byuis/dbuser' password='@dM1n'/>");
		httpPost.setEntity(loginXML);
		
		response = httpClient.execute(httpPost);
		
		responseEntity = response.getEntity();
		
		//updateSessionCookie();
		
		printResponseEntity();
	}
	
	/*
	 * Gets a course from Brainhoney.
	 * 
	 * Parameters:
	 *	courseID: Id of the course being retrieved.
	 */
	public void getCourse(int courseID) throws ClientProtocolException, IOException 
	{
		String courseQuery = "?cmd=getcourse&courseid=" + Integer.toString(courseID);
		
		HttpGet courseGET = new HttpGet(BRAIN_HONEY_URL + courseQuery);
		
		response = httpClient.execute(courseGET);
		responseEntity = response.getEntity();
		printResponseEntity();		
	}
	
	/*
	 * Gets the user id for a particular username.
	 */
	public String getUserID(String username) throws ClientProtocolException, IOException, ParserConfigurationException, IllegalStateException, SAXException
	{
		final String IS_TEST_DOMAIN_ID = "2521289";
		String userQuery = "?cmd=getuserlist&username="+ username + "&domainid=" + IS_TEST_DOMAIN_ID;
		
		HttpGet userNameGET = new HttpGet(BRAIN_HONEY_URL + userQuery);
		response = httpClient.execute(userNameGET);
		responseEntity = response.getEntity();
		
		// Parse the XML response 
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
		org.w3c.dom.Document doc = documentBuilder.parse(responseEntity.getContent());
		
		// Get the user id
		final int FIRST_USER_IN_LIST = 0;
		NodeList titleList = doc.getElementsByTagName("user");
		
		if(titleList.getLength() == 0)
			return "";
		
		NamedNodeMap attributes = titleList.item(FIRST_USER_IN_LIST).getAttributes();	
		Node userIDAttribute = attributes.getNamedItem("userid");
		
		String userID = userIDAttribute.getNodeValue();
		
		return userID;
	}
	
	/*
	 * Get the courses the user is enrolled in.
	 */
	public String getUserGradebook(String userID) throws ClientProtocolException, IOException
	{
		String enrollmentQuery = "?cmd=getusergradebook2&userid=" + userID;
		
		System.out.println(enrollmentQuery);
		
		HttpGet userEnrollmentGET = new HttpGet(BRAIN_HONEY_URL + enrollmentQuery);
		response = httpClient.execute(userEnrollmentGET);
		responseEntity = response.getEntity();
		
		//printResponseEntity();
		
		Scanner reader = new Scanner(responseEntity.getContent());
		String gradeXML = "";
		while(reader.hasNext())
		{
			gradeXML += reader.nextLine() + "\n";
		}
		
		return gradeXML;
	}
}
