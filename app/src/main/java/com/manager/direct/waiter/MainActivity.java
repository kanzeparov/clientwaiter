package com.manager.direct.waiter;

import android.app.DownloadManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.manager.direct.waiter.modelRecepient.Id;
import com.manager.direct.waiter.modelRecepient.Receipt;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    EditText lat;
    EditText lon;
    Button cur;
    Button send;
    EditText price;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lat = findViewById(R.id.latText);
        lon = findViewById(R.id.lonText);
        cur = findViewById(R.id.currentCoord);
        send = findViewById(R.id.send);
        price = findViewById(R.id.price);

        cur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lat.setText("55.751244");
                lon.setText("37.618523");
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncPost().execute();
            }
        });

    }
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private class AsyncPost extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {




            OkHttpClient httpClient = new OkHttpClient();
            String url = "http://lykov.tech:3000/receipts";
            Receipt receipt = new Receipt();
            int curId = getReceopt().get(getReceopt().size()-1).getId()+1;
            receipt.setId(curId);
            receipt.setType("andr");
            receipt.setAmount(price.getText().toString());
            receipt.setLoc(lat.getText().toString()+":"+lon.getText().toString());
            receipt.setTargetAddr("0xFD00A5fE03CB4672e4380046938cFe5A18456Df4");
            receipt.setTx("0");
            RequestBody body = RequestBody.create(JSON, new Gson().toJson(receipt));
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response responseInfo = null;

            try {
                responseInfo = httpClient.newCall(request).execute();
                if (responseInfo.isSuccessful()) {


                }

            } catch (IOException e) {
            }

            outer:
            while (true) {
                try {
                    Thread.sleep(1_000);

                        new Handler(getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                send.setText(send.getText().toString()+".");
                            }
                        });


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<Receipt> receipt1 = getReceopt();
                for (int i = 0; i < receipt1.size(); i++) {
                    if (receipt1.get(i).getId() == curId) {
                        if (!receipt1.get(i).getTx().equals("0")) {



                            new Handler(getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    LayoutInflater inflater = getLayoutInflater();
                                    View layout = inflater.inflate(R.layout.toast,
                                            (ViewGroup) findViewById(R.id.toast_layout_root));
                                    ImageView image = (ImageView) layout.findViewById(R.id.image);
                                    image.setImageResource(R.drawable.ok);
                                    TextView text = (TextView) layout.findViewById(R.id.text);
                                    text.setText("Hello! This is a custom toast!");
                                    Toast toast = new Toast(getApplicationContext());
                                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                    toast.setDuration(Toast.LENGTH_LONG);
                                    toast.setView(layout);
                                    toast.show();
                                    send.setText("Send");
                                }
                            });
                            break outer;
                        }
                    }
                }
            }
            return null;
        }
    }

    public List<Receipt> getReceopt() {
        DefaultHttpClient hc = new DefaultHttpClient();
        ResponseHandler response = new BasicResponseHandler();
        HttpGet http = new HttpGet("http://lykov.tech:3000/db");
        String responseString = null;
        try {
            responseString = (String) hc.execute(http, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Id activityInfo = gson.fromJson(responseString, Id.class);
        return activityInfo.getReceipts();
    }
}
