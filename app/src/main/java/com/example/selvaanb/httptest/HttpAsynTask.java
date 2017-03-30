package com.example.selvaanb.httptest;

import android.os.AsyncTask;

/**
 * Created by selvaanb on 10/5/2016.
 */

public class HttpAsynTask extends AsyncTask<String, Void, Boolean> {

    private String murlContent;

    public HttpAsynTask(String s) {
        murlContent = s;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }
}
