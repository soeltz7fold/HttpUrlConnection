package x7a.droid.httpurlconnection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    Button btn_req;
    TextView lbl_http_connection;
    HttpURLConnection connection = null;
    BufferedReader reader = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Getting Info", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                new ApiConnect().execute("http://private-2efe7-soeltz7fold.apiary-mock.com/questions");
            }
        });

        btn_req = (Button) findViewById(R.id.btn_request);
        lbl_http_connection = (TextView) findViewById(R.id.lbl_http_connection);

        //button CLick will trigger http connection
        btn_req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //localhost /127/0.0.1 or 10.0.2.2
//                new ApiConnect().execute("http:10.0.2.2:3000/api/v1/auth");
                new ApiConnect().execute("http://private-2efe7-soeltz7fold.apiary-mock.com/questions");
            }
        });
    }

        //this methode for handle http connection
        public String get_data(String url_target){
        String line = "";
        try{
            URL url = new URL(url_target);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine())!=null){
                buffer.append(line);
            }
            //this will return to OnPostExecute when doInBackgroundFinished
            return buffer.toString();
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(connection !=null)connection.disconnect();
            try{
                if(reader !=null)reader.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    //This Methode For Handle JSONParser
    public void process_json(String json_str)throws  JSONException{
        try{
            //Clean Textview before append Data
            lbl_http_connection.setText("");
            JSONObject api_json = new JSONObject(json_str);
            JSONArray users = api_json.getJSONArray("users");
            for(int i=0; i<users.length(); i++){
                JSONObject user = users.getJSONObject(i);
                lbl_http_connection.append(
                        "Id = "+String.valueOf(user.getInt("id"))+
                                System.getProperty("line.separator")+
                        "Email = "+user.getString("password")+
                                System.getProperty("line.separator")+
                        "Token Auth = "+user.getString("token_auth")+
                                System.getProperty("line.separator")+
                        "Created at = "+user.getString("created_at")+
                                System.getProperty("line.separator")+
                        "Updated at = "+ user.getString("updated_at")+
                                System.getProperty("line.separator")+
                                System.getProperty("line.separator")
                );
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    //asynctask method will process http connection in background
    //http connection will working in UI Thread
    class ApiConnect extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... params){
            return get_data(params[0]);
        }
        @Override
        protected void onPostExecute (String s){
            super.onPostExecute(s);
            try{
                process_json(s);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
