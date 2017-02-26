package com.qubuss.test.sqlsynstest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText nameET;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter recyclerAdapter;
    ArrayList<Contact> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        readFromLocalStorage();


    }
    private void init(){
        nameET = (EditText) findViewById(R.id.nameET);
        layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerAdapter = new RecyclerAdapter(arrayList);
        recyclerView.setAdapter(recyclerAdapter);
    }

    public void hendlerSubmitName(View view) {

        String name = nameET.getText().toString();
        saveToLocalStorage(name);
        nameET.setText("");

    }

    private void readFromLocalStorage(){

        arrayList.clear();
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = dbHelper.redFromLocalDataBase(database);
        while (cursor.moveToNext()){

            String name = cursor.getString(cursor.getColumnIndex(DbContract.NAME));
            int syncStatus = cursor.getInt(cursor.getColumnIndex(DbContract.SYNC_STATUS));

            arrayList.add(new Contact(name, syncStatus));
        }

        recyclerAdapter.notifyDataSetChanged();
        cursor.close();
        dbHelper.close();
    }

    private void saveToLocalStorage(String name){

        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        if(checkNetworkConnsection()){

        }else{

            dbHelper.saveToLocalDataBase(name, DbContract.SYNC_STATUS_FAILED, database);
        }

        readFromLocalStorage();
        dbHelper.close();

    }

    public boolean checkNetworkConnsection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
