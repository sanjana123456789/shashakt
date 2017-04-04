package com.cetpa.shashakt;

import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class NextActivity extends Activity implements OnCheckedChangeListener
{
	private static final String DEFAULT = "Add number first";
	EditText n1,n2;
	Switch s1,s2,s3;
	TextView tv1,tv2;
	Button b1,b2;
	static boolean wa;
    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_next);
		n1=(EditText) findViewById(R.id.editText1);
		n2=(EditText) findViewById(R.id.editText2);
		tv1=(TextView) findViewById(R.id.textView1);
		tv2=(TextView) findViewById(R.id.textView2);
		b1= (Button) findViewById(R.id.button1);
		b2= (Button) findViewById(R.id.button2);
		SharedPreferences sp=getSharedPreferences("setting", MODE_PRIVATE);
		boolean b=sp.getBoolean("sms", false);
		boolean bb=sp.getBoolean("whats", false);
		boolean bbb=sp.getBoolean("both", false);
		s1=(Switch) findViewById(R.id.switch1);
		s2=(Switch) findViewById(R.id.switch2);
		s3=(Switch) findViewById(R.id.switch3);
		s1.setChecked(b);
		s2.setChecked(bb);
		s3.setChecked(bbb);
		s1.setOnCheckedChangeListener(this);
		s2.setOnCheckedChangeListener(this);
		s3.setOnCheckedChangeListener(this);
	}

	public void save(View v)
	{
		String yW="yesS";
		String num1=n1.getText().toString();
		String num2 =n2.getText().toString();
		SharedPreferences sp=getSharedPreferences("MyCred", MODE_PRIVATE);//used to
		SharedPreferences.Editor e=sp.edit();
		e.putString("k1",num1);
		e.putString("k2",num2);
		e.commit();
		Toast.makeText(this,"Contacts have saved", Toast.LENGTH_SHORT).show();
		Intent intent =new Intent(this,SplashActivity.class);
		intent.putExtra("isCheck", yW);
		startActivity(intent);
		finish();
	}

	public void showNumber(View v)
	{
		SharedPreferences sp=getSharedPreferences("MyCred",MODE_PRIVATE);
		String  num1=sp.getString("k1", DEFAULT);
		String  num2=sp.getString("k2", DEFAULT);
		
		if(num1.equals(DEFAULT)&&num2.equals(DEFAULT))
		{
			
			Toast.makeText(this, "no such data is found", Toast.LENGTH_SHORT).show();
		}
		else
		{
			n1.setText(num1);
			n2.setText(num2);
			Toast.makeText(this, "value is retrived", Toast.LENGTH_SHORT).show();
			
		}

		
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
	{
	  int id=buttonView.getId();
	  switch (id)
	  {
	case R.id.switch1:
		if(isChecked)
		{
			tv1.setVisibility(View.VISIBLE);
			n1.setVisibility(View.VISIBLE);
			tv2.setVisibility(View.VISIBLE);
			n2.setVisibility(View.VISIBLE);
		b1.setVisibility(View.VISIBLE);
		b2.setVisibility(View.VISIBLE);
		
		
		}
		else
		{
			tv1.setVisibility(View.INVISIBLE);
			n1.setVisibility(View.INVISIBLE);
			tv2.setVisibility(View.INVISIBLE);
			n2.setVisibility(View.INVISIBLE);
		b1.setVisibility(View.INVISIBLE);
		b2.setVisibility(View.INVISIBLE);	
		}
		SharedPreferences sp=getSharedPreferences("setting", MODE_PRIVATE);
		SharedPreferences.Editor e=sp.edit();
		e.putBoolean("sms",isChecked);
		
		e.commit();
		break;
	case R.id.switch2 :
		if(isChecked)
		{
            String yW="yesW";
			Intent intent =new Intent(this,SplashActivity.class);
			
			intent.putExtra("isCheck", yW);
			startActivity(intent);
			finish();
		}
		else
		{
			Toast.makeText(this, "can't sent via WhatsApp", Toast.LENGTH_SHORT).show();	
		}
		SharedPreferences sp1=getSharedPreferences("setting", MODE_PRIVATE);
		SharedPreferences.Editor e1=sp1.edit();
		e1.putBoolean("whats",isChecked);
		
		e1.commit();
		break;
	case R.id.switch3 :
		if(isChecked)
		{
			 String yW="yesB";
			Intent i =new Intent(this,SplashActivity.class);
			i.putExtra("isCheck", yW);
			startActivity(i);
			finish();
		}
		else
		{
			Toast.makeText(this, "can't sent via WhatsApp and SMS together", Toast.LENGTH_SHORT).show();	
		}
		SharedPreferences sp2=getSharedPreferences("setting", MODE_PRIVATE);
		SharedPreferences.Editor e2=sp2.edit();
		e2.putBoolean("both",isChecked);
		
		e2.commit();
		break;
	default:
		break;
	}
		
	}
}
