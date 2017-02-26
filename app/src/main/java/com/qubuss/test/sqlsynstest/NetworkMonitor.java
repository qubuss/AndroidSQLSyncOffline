package com.qubuss.test.sqlsynstest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qubuss on 26.02.2017.
 */

public class NetworkMonitor extends BroadcastReceiver{
    @Override
    public void onReceive(final Context context, Intent intent) {

        if(checkNetworkConnsection(context)){

            final DbHelper dbHelper = new DbHelper(context);
            final SQLiteDatabase database = dbHelper.getReadableDatabase();

            Cursor cursor = dbHelper.redFromLocalDataBase(database);
            while (cursor.moveToNext()){
                int syncStatus = cursor.getInt(cursor.getColumnIndex(DbContract.SYNC_STATUS));

                if(syncStatus == DbContract.SYNC_STATUS_FAILED){

                    final String name = cursor.getString(cursor.getColumnIndex(DbContract.NAME));
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String responseFromServer = jsonObject.getString("response");

                                if(responseFromServer.equals("OK")){
                                    dbHelper.updateLocalDataBase(name, DbContract.SYNC_STATUS_OK, database);
                                    context.sendBroadcast(new Intent(DbContract.UI_UPDATE_BROADCAST));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("name", name);

                            return params;
                        }
                    };

                    MySingleton.getInstance(context).addToRequestQue(stringRequest);
                }
            }

            dbHelper.close();
        }
    }

    public boolean checkNetworkConnsection(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
