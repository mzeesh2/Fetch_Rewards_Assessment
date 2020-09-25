package com.example.fetchrewardsassessment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    Button click;
    public static TextView data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        click = findViewById(R.id.button);
        data = (TextView) findViewById(R.id.fetchedData);

        //As soon as user clicks the button to fetch information, we call the fetchJSON class
        click.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                fetchJSON process = new fetchJSON();
                process.execute();
            }
        });
    }
}