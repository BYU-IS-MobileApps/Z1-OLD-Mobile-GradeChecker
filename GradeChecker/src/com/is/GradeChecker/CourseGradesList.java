package BYUIS.classes.MyCourses;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

public class CourseGradesList extends ListActivity 
{
	private static final String TAG = "CourseGradeList";
	private ProgressDialog loadingDialog;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);	
		
		loadingDialog = ProgressDialog.show(this, "", "Loading courses...", true, true);
		new LoadCoursesFromDb().execute();
		//new LoadCourses().execute();
	}

	private class LoadCoursesFromDb extends AsyncTask<Void, Void, Cursor>
	{
		private GradeBookDbAdapter gradesDb;

		@Override
		protected Cursor doInBackground(Void... params) 
		{
			gradesDb = new GradeBookDbAdapter(CourseGradesList.this);
			gradesDb.open();
			Cursor gradesQueryResult = gradesDb.getAllCourses();
			
			return gradesQueryResult;
		}
		
		protected void onPostExecute(Cursor gradesQueryResult)
		{		
			startManagingCursor(gradesQueryResult);
			
			Log.d(TAG, "" + gradesQueryResult.getColumnCount());
			
			String[] fromColumns = {GradeBookDbHelper.C_COURSE_TITLE, GradeBookDbHelper.C_COURSE_GRADE}; 
			int[] toRowIds = {R.id.courseName, R.id.courseGrade};
			
			ListAdapter dbListAdapter = new SimpleCursorAdapter(CourseGradesList.this,
												R.layout.overall_grade_row,
												gradesQueryResult,
												fromColumns,
												toRowIds);			
			setListAdapter(dbListAdapter);
			
			gradesDb.close();
			loadingDialog.dismiss();
		}		
	}
	
	/*
	 * Asynchronous task for loading the courses into the list activity.
	 */
	private class LoadCourses extends AsyncTask<Void, Void, ArrayList<String>>
	{
		private ArrayList<String> getCourses() throws SAXException, IOException, ParserConfigurationException
		{
			FileInputStream inputStream = new FileInputStream(new File(getCacheDir() + "/grades"));
			
			String gradeBookXML = "";
			Scanner gradesScanner = new Scanner(inputStream);
			while(gradesScanner.hasNext())
				gradeBookXML += gradesScanner.nextLine();
			
			Log.d(TAG, gradeBookXML);
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
			org.w3c.dom.Document doc = documentBuilder.parse(new ByteArrayInputStream(gradeBookXML.getBytes()));
			
			// find all the course names
			NodeList entities = doc.getElementsByTagName("entity");
			ArrayList<String> courses = new ArrayList<String>();
			Node courseAttribute;
			for(int i = 0; i < entities.getLength(); i++)
			{
				NamedNodeMap attributes = entities.item(i).getAttributes(); 
				courseAttribute = attributes.getNamedItem("title");
				
				if(courseAttribute != null)
				{
					courses.add(courseAttribute.getNodeValue());
					System.out.println("found: " + courseAttribute.getNodeValue());
				}
					
			}
			
			System.out.println(courses);
			
			return courses;
		}		
		
		protected ArrayList<String> doInBackground(Void...voids)
		{
			Log.d(TAG, "Loading courses");
			
			/*GradeBookDbAdapter dbAdapter = new GradeBookDbAdapter(CourseGradesList.this);
			dbAdapter.open();
			dbAdapter.close();*/
			
			try 
			{	
				return getCourses();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			return null;
		}
		
		protected void onPostExecute(ArrayList<String> courses)
		{			
			String[] courseArray = new String[courses.size()];
			for(int i = 0; i < courses.size(); i++)
				courseArray[i] = courses.get(i);
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(CourseGradesList.this, android.R.layout.simple_list_item_1, courseArray);
			setListAdapter(adapter);	
			
			loadingDialog.dismiss();
			
			Log.d(TAG, "Courses: " + courses.toArray());
		}
	}
}
