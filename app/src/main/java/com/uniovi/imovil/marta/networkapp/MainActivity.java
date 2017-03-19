package com.uniovi.imovil.marta.networkapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    String Url = "http://datos.gijon.es/doc/transporte/busgijontr.json";
    ProgressBar progressBar;
    boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        Button buttonConexion = (Button) findViewById(R.id.DescargarJSON);

        buttonConexion.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View arg0){
                Toast toast1= Toast.makeText(getApplicationContext(), "It's connected!", Toast.LENGTH_SHORT);
                Toast toast2= Toast.makeText(getApplicationContext(), "Not connected", Toast.LENGTH_SHORT);
                if(isOnline()){
                   // toast1.show();

                    progressBar.setVisibility(View.VISIBLE);
                    DownloadJsonTask task = new DownloadJsonTask();
                    task.execute(Url);
                }
                else{
                    toast2.show();
                }
            }
        }
        );
    }
    private InputStream openHttpInputStream(String myUrl)
            throws MalformedURLException, IOException, ProtocolException {
        InputStream is;
        URL url = new URL(myUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Aquí se hace realmente la petición
        conn.connect();

        is = conn.getInputStream();
        return is;
    }
    private class DownloadJsonTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // urls vienen de la llamada a execute(): urls[0] es la url
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                // TODO: las cadenas deberían ser recursos
                return "Fallo al intentar descargar la página";
            }
        }

        private String downloadUrl(String myUrl) throws IOException {
            InputStream is = null;

            try {
                is = openHttpInputStream(myUrl);

                return streamToString(is);
            } finally {
                // Asegurarse de que el InputStream se cierra
                if (is != null) {
                    is.close();
                }

            }
        }

        // Pasa un InputStream a un String
        public String streamToString(InputStream stream) throws IOException,
                UnsupportedEncodingException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = 0;
            do {
                length = stream.read(buffer);
                if (length != -1) {
                    baos.write(buffer, 0, length);
                }
            } while (length != -1);
            return baos.toString("UTF-8");
        }

        // Muestra el resultado en un text_view
        @Override
        protected void onPostExecute(String result) {
            TextView textView = (TextView) findViewById(R.id.result_text_view);
            textView.setText(result);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
