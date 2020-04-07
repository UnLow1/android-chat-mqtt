package com.agh.wiet.mobilki.chat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;

public class SimpleChatActivity extends AppCompatActivity {

    MqttClient sampleClient = null;
    String nick;
    String ip;
    Handler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_chat);

        nick = getIntent().getStringExtra(MainActivity.NICK);
        ip = getIntent().getStringExtra(MainActivity.IP);
        TextView loginTextView = findViewById(R.id.loginTextView);
        loginTextView.setText(nick);

        new Thread(new Runnable() {
            @Override
            public void run() {
                startMQTT();
            }
        }).start();

        final ListView chatListView = findViewById(R.id.chatListView);

        //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
        final ArrayList<ChatMessage> listItems = new ArrayList<>();
        //DEFINING STRING ADAPTER WHICH WILL HANDLE DATA OF LISTVIEW
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, listItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);

                text1.setText(listItems.get(position).getNick());
                text2.setText(listItems.get(position).getMessage());
                return view;
            }
        };
        chatListView.setAdapter(adapter);

         myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String nick = msg.getData().getString("NICK");
                String message = msg.getData().getString("MSG");
                listItems.add(new ChatMessage(nick, message));
                adapter.notifyDataSetChanged();
                chatListView.setSelection(listItems.size() - 1);
            }
        };

        final EditText messageEditText = findViewById(R.id.messageEditText);
        Button button = findViewById(R.id.sendButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MqttMessage mqttMessage = new MqttMessage(messageEditText.getText().toString().getBytes());
                try {
                    sampleClient.publish(nick, mqttMessage);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sampleClient != null) {
            try {
                sampleClient.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private void startMQTT() {
        String clientId;
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            String broker = "tcp://" + ip + ":1883";
            clientId = nick;
            sampleClient = new MqttClient(broker, clientId, persistence);
            sampleClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {

                }

                @Override
                public void messageArrived(String nickOfSender, MqttMessage mqttMessage) {
                    Message msg = myHandler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("NICK", nickOfSender);
                    b.putString("MSG", mqttMessage.toString());
                    msg.setData(b);
                    myHandler.sendMessage(msg);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            sampleClient.subscribe("#");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
