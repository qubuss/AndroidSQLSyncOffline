package com.qubuss.test.sqlsynstest;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by qubuss on 26.02.2017.
 */

//Singleton Volley

public class MySingleton {

    private static MySingleton mInstance;
    private RequestQueue requestQueue;
    private static Context mContext;

    private MySingleton(Context mContext) {
        this.mContext = mContext;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {

        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        return requestQueue;
    }

    public static synchronized MySingleton getInstance(Context mContext) {

        if(mInstance == null){
            mInstance = new MySingleton(mContext);
        }

        return mInstance;
    }

    public<T> void addToRequestQue(Request<T> request){

        getRequestQueue().add(request);
    }

}
