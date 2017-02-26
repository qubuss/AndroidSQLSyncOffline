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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        saveToAppServer(name);
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

    private void saveToAppServer(final String name){



        if(checkNetworkConnsection()){

            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String responseFromServer = jsonObject.getString("response");
                        if(responseFromServer.equals("OK")){
                            saveToLocalStorage(name, DbContract.SYNC_STATUS_OK);
                        }else {
                            saveToLocalStorage(name, DbContract.SYNC_STATUS_FAILED);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    saveToLocalStorage(name, DbContract.SYNC_STATUS_FAILED);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("name", name);

                    return params;
                }
            };

            MySingleton.getInstance(MainActivity.this).addToRequestQue(stringRequest);

        }else{

            saveToLocalStorage(name, DbContract.SYNC_STATUS_FAILED);

        }



    }

    public boolean checkNetworkConnsection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void saveToLocalStorage(String name, int syncStatus){

        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        dbHelper.saveToLocalDataBase(name, syncStatus, database);
        readFromLocalStorage();
        dbHelper.close();

    }
}
