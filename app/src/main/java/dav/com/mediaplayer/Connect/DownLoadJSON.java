package dav.com.mediaplayer.Connect;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by binhb on 28/04/2017.
 */

public class DownLoadJSON extends AsyncTask<String, Void,String>{

    StringBuilder data;
    String url;
    List<HashMap<String,String>> attrs = new ArrayList<>();
    ProgressDialog progressDialog;
    Context context;

    public DownLoadJSON(String url){
        this.url = url;
    }
    public DownLoadJSON(Context context,String url){
        this.url = url;
        this.context = context;
    }

    public DownLoadJSON(String url, List<HashMap<String,String>> attrs){
        this.url = url;
        this.attrs = attrs;
    }
    public DownLoadJSON(Context context,String url, List<HashMap<String,String>> attrs){
        this.url = url;
        this.attrs = attrs;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        if(context != null){

        }

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if(attrs.size() != 0){
                return mothodPost(connection);
            }else{
                return methodGet(connection);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String methodGet(HttpURLConnection connection) {

        try {
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            this.data = new StringBuilder();
            String line = "";
            while((line = bufferedReader.readLine())!=null){
                this.data.append(line);
            }

            bufferedReader.close();
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.data.toString();
    }

    private String mothodPost(HttpURLConnection connection) {
        String data ="";
        String key = "";
        String value = "";
        try{
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            int count = attrs.size();
            JSONObject jsonObject = new JSONObject();
            for(int i=0; i<count; i++){
                for(Map.Entry<String,String> values:attrs.get(i).entrySet()){
                    key = values.getKey();
                    value = values.getValue();
                }

                jsonObject.put(key,value);

            }
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));

          /*  DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(jsonObject.toString());

            outputStream.flush();
            outputStream.close();*/

            bw.write(jsonObject.toString());
            bw.flush();
            bw.close();

            data = methodGet(connection);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}
