package BYUIS.classes.MyCourses;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class UserNameConfig extends Activity implements View.OnClickListener
{
	private Button button;
	String fileName;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.user_config);
		
		fileName=getIntent().getStringExtra("file");
		Log.d("Filename",fileName);
		button=(Button) findViewById(R.id.Login);
		button.setOnClickListener(this);
	}
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.Login:

			File cacheFile=new File(getCacheDir(),fileName);	//replace the filename with real file name
			try{
				if(!cacheFile.exists())cacheFile.createNewFile();
				startActivity(new Intent(this,BYUISActivity.class));			
			}catch(IOException e){
				Log.e("Error",e.getMessage());
			}
			break;
		}
	}
}
