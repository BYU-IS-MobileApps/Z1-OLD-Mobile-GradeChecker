package BYUIS.classes.MyCourses;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class BYUISActivity extends Activity implements View.OnClickListener
{
	private final static String TAG = "BYUISActivity: "; 
	private final String GRADES_FILE_NAME = "gradese";
	private Button preferenceButton;
	private Button coursesButton;
	private Button testDownloadButton;
	private File file;
    /* 
     * Called when the activity is first created.  
     */
    public void onCreate(Bundle savedInstanceState){
    	
        super.onCreate(savedInstanceState);
        file=new File(getCacheDir(), GRADES_FILE_NAME);
        
        if(file.exists()){
	        setContentView(R.layout.cool_main);
	        // Set listeners
	        preferenceButton = (Button) findViewById(R.id.set_preferences);
	        preferenceButton.setOnClickListener(this);
	        
	        coursesButton = (Button) findViewById(R.id.my_grades_button);
	        coursesButton.setOnClickListener(this);
	        
	        testDownloadButton = (Button) findViewById(R.id.download_grades_test);
	        testDownloadButton.setOnClickListener(this);
	        
        }
        else{
        	Intent intent=new Intent(this, UserNameConfig.class);
        	intent.putExtra("file", GRADES_FILE_NAME);
        	startActivity(intent);        	
        }
        
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch(item.getItemId())
    	{
    		case R.id.optionOne:
    			Log.d(TAG, "option one selected");
    		break;
    	}
    	
		return false;
    }
    
	public void onClick(View v) 
	{
		switch(v.getId())
		{
			case R.id.download_grades_test:
				Toast.makeText(BYUISActivity.this, "going to download text", Toast.LENGTH_LONG).show();
				//new DownloadGrades().execute();
				//new LoadTestCourses().execute();
				startService(new Intent(this, GradesUpdaterService.class));
			break;
		
			case R.id.my_grades_button:
				startActivity(new Intent(this, CourseGradesList.class));
			break;
		
			case R.id.set_preferences:
				startActivity(new Intent(this, PrefsActivity.class));
			break;
		}
		
	}
	
	class LoadTestCourses extends AsyncTask<Void, Integer, String>
	{	
		protected String doInBackground(Void... params) 
		{
			GradeBookDbAdapter gradeBook = new GradeBookDbAdapter(BYUISActivity.this);
			gradeBook.open();
			gradeBook.insertTestCourses();
			gradeBook.close();
			
			return null;
		}
		
		protected void onPostExecute(String grades)
		{
			Log.d(TAG, "Grades cached");
		}
	}
	
	class DownloadGrades extends AsyncTask<Void, Integer, String>
	{
		private void storeGradesInCache(String gradesXML) throws IOException
		{
			
			try
			{
				FileOutputStream outputStream = new FileOutputStream(new File(getCacheDir(), GRADES_FILE_NAME));
				outputStream.write(gradesXML.getBytes());	
			}
			catch(IOException e)
			{
				e.printStackTrace();
				Log.d(TAG, "Problem reading cache file");
			}
		}
		
		protected String doInBackground(Void... params) 
		{	
			SharedPreferences preferences = getSharedPreferences("PrefFile", MODE_WORLD_READABLE);
			BrainHoneyAccess brainHoney = new BrainHoneyAccess();
			
			try 
			{
				brainHoney.login();
				String gradeBookXML = brainHoney.getUserGradebook((String) preferences.getAll().get("userID"));
				storeGradesInCache(gradeBookXML);
				//System.out.println(brainHoney.getUserGradebook((String) preferences.getAll().get("userID")));
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			return null;
		}
		
		protected void onPostExecute(String grades)
		{
			Log.d(TAG, "Grades cached");
		}
	}
}