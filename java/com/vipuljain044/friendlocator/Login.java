package com.vipuljain044.friendlocator;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.HashMap;

public class Login extends AppCompatActivity {

    Button btn;
    EditText text;
    String name;
    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(alreadyAccountAdded()){
            startActivity(new Intent(Login.this,Main.class));
            finish();
        }else{
            initializeViews();
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isInternetConnected(getApplicationContext())) {
                        if (validateEditText()) {
                            try {
                                SQLdb sqLdb = new SQLdb(Login.this);
                                sqLdb.open();
                                sqLdb.insertEntry(name, code);
                                sqLdb.close();

                                insertToServer(name, code);
                            } catch (Exception e) {
                                SQLdb sqLdb1 = new SQLdb(Login.this);
                                sqLdb1.open();
                                sqLdb1.onDelete();
                                sqLdb1.close();
                                Toast.makeText(getApplicationContext(), "Error Adding User", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid User Name", Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Connect Internet First", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about) {
            startActivity(new Intent(Login.this,about.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean alreadyAccountAdded() {
        SQLdb info = new SQLdb(Login.this);
        info.open();
        int identity = info.getDetail();
        info.close();
        if (identity > 0)
            return true;
        else
            return false;
    }
    public void initializeViews(){
        setContentView(R.layout.activity_login);
        btn= (Button) findViewById(R.id.button);
        text= (EditText) findViewById(R.id.edittext);
    }
    public boolean validateEditText(){
        name=text.getText().toString();
        if (name.equals("") || name == null) {
            return false;
        }else {
            String n_name = (name + "xxxxx").substring(0, 5);
            code = generateCode(n_name);
            return true;
        }
    }
    public String generateCode(String name) {
        return (name
                + Integer.toHexString((int) (10000 + (long) (Math.random() * 10000)))
                + Integer.toHexString((int) (10000 + (long) (Math.random() * 10000)))
                + Integer.toHexString((int) (10000 + (long) (Math.random() * 10000)))
                + "XXXXXX").substring(0, 11);
    }
    public void insertToServer(String UserName,String Code) {
        try {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("insertUserName", UserName);
            data.put("insertCurrentLocation", "0;0");
            data.put("insertUserCode", Code);

            PostResponseAsyncTask task = new PostResponseAsyncTask(Login.this, data, new AsyncResponse() {
                @Override
                public void processFinish(String s) {
                    if(s.equals("error")) {
                        SQLdb sqLdb1 = new SQLdb(Login.this);
                        sqLdb1.open();
                        sqLdb1.onDelete();
                        sqLdb1.close();
                        Toast.makeText(getApplicationContext(),"Error adding User", Toast.LENGTH_LONG).show();
                    }
                    else{
                        startActivity(new Intent(Login.this,Main.class));
                    }
                }
            });
            task.execute("http://mrjain.esy.es/index.php");
            task.setEachExceptionsHandler(new EachExceptionsHandler() {
                @Override
                public void handleIOException(IOException e) {}

                @Override
                public void handleMalformedURLException(MalformedURLException e) {}

                @Override
                public void handleProtocolException(ProtocolException e) {}

                @Override
                public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {}
            });
        } catch (Exception e) {
            SQLdb sqLdb1 = new SQLdb(Login.this);
            sqLdb1.open();
            sqLdb1.onDelete();
            sqLdb1.close();
            Toast.makeText(getApplicationContext(),"Error Adding User ", Toast.LENGTH_LONG).show();
        }
    }
    public static boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}



