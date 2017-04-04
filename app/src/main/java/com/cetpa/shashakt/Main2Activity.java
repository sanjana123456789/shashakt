package com.cetpa.shashakt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity implements AdapterView.OnItemClickListener{
ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        String[] names={"palak","aditi","ayushi","sakshi","sonam","divakar","sachin","palak","aditi","ayushi","sakshi","sonam","divakar","sachin","palak","aditi","ayushi","sakshi","sonam","divakar","sachin"};
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.single_row,names);
        lv= (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView tv=(TextView) view;//downcasting
        String name=tv.getText().toString();
        Toast.makeText(this, name +" is clicked at position"+position+"by user", Toast.LENGTH_SHORT).show();
    }
}
