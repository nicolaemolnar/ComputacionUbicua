package com.privatecomms.securia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

public class MainActivity extends AppCompatActivity {


    private Button btnConfig, btnExit;
    SwitchCompat stream;
    boolean stateStream;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //bundle para recbir los datos del login
        Bundle datos = getIntent().getExtras();
        System.out.println(datos);

        //Init de botones
        this.btnConfig = this.findViewById(R.id.btnConfig);
        this.btnExit = this.findViewById(R.id.btnExit);
        //switch de stream
        preferences = getSharedPreferences("PREFS",0);
        stateStream = preferences.getBoolean("stream",false);
        //this.stream = this.findViewById(R.id.stream);

        //le paso los datos del usuario del inicio de sesion del login
        String email = datos.getString("email");
        System.out.println(email);
        String password = datos.getString("password");


        //funciones de botones y switch de stream
        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent config = new Intent(getApplicationContext(),config_activity.class);
                Bundle datos = getIntent().getExtras();
                String email = datos.getString(("email"));

                Bundle datosConfig= new Bundle();

                datos.putString("email",email);
                config.putExtras(datos);

                startActivity(config);
                finish();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
/**
        stream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateStream = !stateStream;
                stream.setChecked(stateStream);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("stream",stateStream);
                editor.apply();
            }
        });**/

    }

    private class GetXMLTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... urls) { //Leer respuesta del servidor (String del JSON)
            JSONObject output = null;
            for (String url : urls) {
                String json_string = getOutputFromUrl(url);
                try {
                    output = new JSONObject(json_string);
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            }
            return output;
        }

        private String getOutputFromUrl(String url) { // Recibe la String(JSON) que nos envia el servidor
            StringBuffer output = new StringBuffer("");
            try {
                InputStream stream = getHttpConnection(url);
                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(stream));
                String s = "";
                while ((s = buffer.readLine()) != null)
                    output.append(s);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return String.valueOf(output);
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();
                stream = httpConnection.getInputStream();
                //if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                //}
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }

        @Override
        protected void onPostExecute(JSONObject output) { //Analizar el resultado pagian web y redirigir o mostrar error

        }
    }
}