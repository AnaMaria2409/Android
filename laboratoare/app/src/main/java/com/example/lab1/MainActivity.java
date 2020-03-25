package com.example.lab1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    String DESCRIPTION;
    String desc;
    ListView simpleList;
    TextView simpleText;
    String items[] = {"Primer", "Fond de ten", "Concealer", "Paleta contur", "Iluminator", "Blush"};

    Map<String, String> description = new HashMap<String, String>() {{
        put("Primer", "Hidreateaza tenul");
        put("Fond de ten", "Ascunde imperfectiuni");
        put("Concealer", "Pentru cearcane");
        put("Paleta contur", "Adauga umbre");
        put("Iluminator", "Stralucire");
        put("Blush", "Imbujorare :))");
    }};

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simpleList = (ListView) findViewById(R.id.simpleListView);
        simpleText = (TextView) findViewById(R.id.mytextView);


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        simpleList.setAdapter(arrayAdapter);

        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = (String) parent.getItemAtPosition(position);
                desc = description.get(selectedItem);
                simpleText.setText(desc);
            }
        });
        SaveFileToInternalStorage();
    }

    // lab 5 internal storage pentru produse si descrierea lor
    protected void SaveFileToInternalStorage() {
        FileOutputStream fos;
        try {
            fos = openFileOutput("saveinfo.txt", Context.MODE_PRIVATE);
            byte[] content = description.toString().getBytes();
            fos.write(content);
            Log.i("Save","File saved!");
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("lifecycle", "sunt in onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("lifecycle", "sunt in onStop");

    }

    protected void onResume() {
        super.onResume();
        Log.d("lifecycle", "sunt in onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("lifecycle", "sunt in onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("lifecycle", "sunt in onRestart");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("lifecycle", "sunt in onDestoy");

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        simpleText = (TextView) findViewById(R.id.mytextView);
        simpleText.setText(savedInstanceState.getString(DESCRIPTION));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        simpleText = (TextView) findViewById(R.id.mytextView);
        outState.putString(DESCRIPTION, (String) simpleText.getText());
        super.onSaveInstanceState(outState);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        return true;
    }

    //lab 4
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sms:
                Intent myIntent = new Intent();
                myIntent.setAction(Intent.ACTION_SEND);
                myIntent.putExtra(Intent.EXTRA_TEXT, "Hi!");
                myIntent.setType("text/plain");
                startActivity(myIntent);
                return true;

            case R.id.rate:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Cum am fost astazi?").setTitle("Rate us");

                builder.setPositiveButton(R.string.happy, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.setNegativeButton(R.string.sad, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            case R.id.settings:
                //lab 5
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
