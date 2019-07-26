package com.example.simpletodoapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // a numeric code to identify the edit activity
    public final static  int EDIT_REQUEST_CODE = 20;
    // Keys used for passing data between activities
    public final static String ITEM_TEXT = "itemText";
    public final static String ITEM_POSITION ="itemPosition";

ArrayList<String> item;
ArrayAdapter<String> itemAdapter;
ListView lvItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readItem();
        itemAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item );
        lvItems = (ListView) findViewById(R.id.lvItem) ;
        lvItems.setAdapter(itemAdapter);

        //mock data
       // item.add("First item");
        //item.add("Second item");

        setupListViewListener();

    }

    public void  onAddItem(View v) {
        EditText etNewItem =(EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        itemAdapter.add(itemText);
        etNewItem.setText("");
        writeItem();
        Toast.makeText(getApplicationContext(),"Item added to List", Toast.LENGTH_SHORT).show();
    }
    private void setupListViewListener() {
        Log.i("MainActivity","Setting up Listener on List View");
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i("MainActivity","item removed from List: "+ position);
               item.remove(position);
               itemAdapter.notifyDataSetChanged();
               writeItem();
               return true;

            }
        });

        // set up item listener for edit (regular click)
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // create the new activity
                Intent i = new Intent(MainActivity.this, EdititemActivity.class);
                // pass the data being edited
                i.putExtra(ITEM_TEXT, item.get(position));
                i.putExtra(ITEM_POSITION, position);
                // display the activity
                startActivityForResult(i, EDIT_REQUEST_CODE);

            }
        });

    }
                 // handle results from edit activity

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if the edit activity completed ok
        if (resultCode == RESULT_OK && requestCode == EDIT_REQUEST_CODE){
            // extract updated item text from result intent extras
            String updatedItem = data.getExtras().getString(ITEM_TEXT);
            // extract original position of edited item
            int position = data.getExtras().getInt(ITEM_POSITION);
            // update the model with the new item text at the edited position
            item.set(position, updatedItem);
            // notify the  adapter that the model changed
            itemAdapter.notifyDataSetChanged();
            //persist the changed model
            writeItem();
            // notify the user the operation completed ok
            Toast.makeText(this, "Item updated successfully", Toast.LENGTH_SHORT).show();
        }

    }

    private File getDataFile() {
        return new File(getFilesDir(),"todo.txt");

    }
    private void readItem(){
        try {
            item= new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity" , "Error file" , e );
            item= new ArrayList<>();
        }
    }
    private void writeItem(){
        try {
            FileUtils.writeLines(getDataFile(),item);
        } catch (IOException e) {
            Log.e("MainActivity" , "Error writing file" , e );
        }

    }
}

