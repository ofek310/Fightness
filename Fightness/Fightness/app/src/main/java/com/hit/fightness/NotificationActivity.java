package com.hit.fightness;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class NotificationActivity extends AppCompatActivity {

    TextView messageTv;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/");

    final String API_TOKEN_KEY = "AAAAUH8OACs:APA91bGhtTHSRYkAvQ6deplD7kOyQCA52g7I9yxWEnGwf2gBX1sYbX_eJhc7E6W0EmdTPHrgiS2JsUzBKHHMVRKbSym9jnyDIDX-YIO0Urw-l_90ZCyqtRSZd3Y7jZrVnjY8a902v3Na";

    FirebaseMessaging messaging = FirebaseMessaging.getInstance();

    BroadcastReceiver receiver;

    public NotificationActivity(Context context, String message, String token) {


                final JSONObject rootObject  = new JSONObject();
                try {
                    rootObject.put("to", token);


                    rootObject.put("data",new JSONObject().put("message",message));

                    String url = "https://fcm.googleapis.com/fcm/send";

                    RequestQueue queue = Volley.newRequestQueue(context);
                    StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String,String> headers = new HashMap<>();
                            headers.put("Content-Type","application/json");
                            headers.put("Authorization","key="+API_TOKEN_KEY);
                            return headers;
                        }

                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            return rootObject.toString().getBytes();
                        }
                    };
                    queue.add(request);
                    queue.start();


                }catch (JSONException ex) {
                    ex.printStackTrace();
                }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

//                messageTv.setText(intent.getStringExtra("message"));
            }
        };

        IntentFilter filter = new IntentFilter("message_received");
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver,filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}