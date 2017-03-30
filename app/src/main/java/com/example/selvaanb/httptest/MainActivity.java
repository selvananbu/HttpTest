package com.example.selvaanb.httptest;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText mInputEdit;
    private Button mRequestButton;
    private EditText mOutputEdit;
    private CheckBox mAsyncCheck;
    private ProgressBar mLoadingBar;
    private HttpAsynTask mHttpAsyncTask = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRequestButton = (Button)findViewById(R.id.button);
        mInputEdit = (EditText) findViewById(R.id.editText);
//        mOutputEdit = (EditText) findViewById(R.id.editText2);
        mAsyncCheck = (CheckBox) findViewById(R.id.checkBox);
        mLoadingBar = (ProgressBar) findViewById(R.id.progressBar);
        mRequestButton.setEnabled(false);
        setupUI(findViewById(R.id.activity_main));


        showProgress(false);

        mInputEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mRequestButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty())
                    mRequestButton.setEnabled(false);


            }
        });
        // Register the onClick listener with the implementation above
        mRequestButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mInputEdit.getText().toString().isEmpty()) return;
//                mOutputEdit.setText("");
                final String output = mInputEdit.getText().toString();

                if (!mAsyncCheck.isChecked()) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            HttpClient httpclient = new DefaultHttpClient();
                            HttpResponse response = null;
                            try {
                                Uri newUri = Uri.parse(output);
                                response = httpclient.execute(new HttpGet(String.valueOf(newUri)));
                                StatusLine statusLine = response.getStatusLine();
                                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                                    response.getEntity().writeTo(out);
                                    final String responseString = out.toString();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
//                                            mOutputEdit.setText(responseString);
                                        }
                                    });

                                    out.close();
                                    //..more logic
                                } else {
                                    //Closes the connection.
                                    response.getEntity().getContent().close();
                                    throw new IOException(statusLine.getReasonPhrase());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    });
                    t.start();
                }
                else
                {
                    if(mHttpAsyncTask != null)
                        return;
                    showProgress(true);
                    mHttpAsyncTask = new HttpAsynTask(output);
                    mHttpAsyncTask.execute();
                }
            }
        });


    }
    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(MainActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);

    }

    public void showProgress(boolean showProgress) {
//        if(showProgress)
//        {
//            mLoadingBar.setVisibility(View.VISIBLE);
//        }
//        else
//            mLoadingBar.setVisibility(View.GONE);
    }

    public class HttpAsynTask extends AsyncTask<String, Void, Boolean> {

        private String murlContent;

        public HttpAsynTask(String s) {
            murlContent = s;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = null;
            try {
                Uri newUri = Uri.parse(murlContent);
                response = httpclient.execute(new HttpGet(String.valueOf(newUri)));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    final String responseString = out.toString();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            mOutputEdit.setText(responseString);
                        }
                    });
                    out.close();
                    //..more logic
                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            showProgress(false);

        }
    }



}
