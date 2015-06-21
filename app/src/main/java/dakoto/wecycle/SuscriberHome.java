package dakoto.wecycle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class SuscriberHome extends ActionBarActivity {
    private ImageButton schedule;
    private ImageButton view_history;
    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suscriber_home2);

        schedule=(ImageButton)findViewById(R.id.imageButton3);
        session=new SessionManager(getApplicationContext());
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        alertDialog.setTitle("Order Details");

        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setIcon(R.drawable.ic_launcher);
        schedule.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                HttpClient client = new DefaultHttpClient();
//                curl -i -H "Content-Type:application/json" -H
//                "Authorization: Bearer 461DB80AB9BE17C1938F590BCEEE72121856C29D40B1B4FA01DA11CD6FEFD81C"
//                -X POST -d '{}' https://wecyclers-api.herokuapp.com/api/v1/orders
                try
                {
                    HttpPost request = new HttpPost("http://wecyclers-api.herokuapp.com/api/v1/orders");
                    StringEntity json =new StringEntity("{}");
                    request.addHeader("Content-Type", "application/json");
                    request.addHeader("Authorization", "Bearer "+session.getToken());
                    request.setEntity(json);
                    HttpResponse response = client.execute(request);
//                Toast.makeText(getBaseContext(), response.toString(), Toast.LENGTH_LONG).show();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    String json_response = reader.readLine();
                    JSONTokener tokener = new JSONTokener(json_response);
                    JSONObject jsonarray = new JSONObject(tokener);
                    if(jsonarray.isNull("error")){
                        alertDialog.setMessage("Your order has been set.\n The following are the details:\n"+jsonarray.getString("date")+"\n"+jsonarray.getString("partner_id"));
                        Toast.makeText(getBaseContext(), session.getToken(), Toast.LENGTH_LONG).show();
                        alertDialog.show();
                    }else{
                        alertDialog.setMessage(jsonarray.getString("error"));
                        Toast.makeText(getBaseContext(), session.getToken(), Toast.LENGTH_LONG).show();
                        alertDialog.show();
                    }
                }

                catch(Exception ex)
                {
                    ex.printStackTrace();
                    Toast.makeText(getApplicationContext(), "exception", Toast.LENGTH_LONG).show();
//                  Log.d(TAG, "Register Response: " + ex.getMessage());
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_suscriber_home, menu);
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
