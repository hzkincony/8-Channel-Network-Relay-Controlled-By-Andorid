package socket.kincony.com.socket;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    /**
     *
     */

    // Handler
    // Used to display messages retrieved from the server
    private Handler mMainHandler;

    // Socket
    private Socket socket;

    // Threadpool
    //
    private ExecutorService mThreadPool;

    /**
     *
     */
    // InputStream
    InputStream is;

    // Receive server messages
    InputStreamReader isr ;
    BufferedReader br ;

    // Receiving messages sent by the server
    String response;


    /**
     * Send a message to the server
     */
    // OutputStream
    OutputStream outputStream;

    /**
     * Button
     */

    // connect Disconnect Sendmessage
    private Button btnConnect, btnDisconnect, btnSend;

    // TextView  Receive
    private TextView Receive,receive_message;

    // input message
    private EditText mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * init
         */

        // init button
        btnConnect = (Button) findViewById(R.id.connect);
        btnDisconnect = (Button) findViewById(R.id.disconnect);
        btnSend = (Button) findViewById(R.id.send);
        mEdit = (EditText) findViewById(R.id.edit);
        receive_message = (TextView) findViewById(R.id.receive_message);
        Receive = (Button) findViewById(R.id.Receive);

        // init ThreadPool
        mThreadPool = Executors.newCachedThreadPool();


        //
        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        receive_message.setText(response);
                        break;
                }
            }
        };


        /**
         * create socket
         */
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // start Thread
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            // create Socket,IP,port
                            socket = new Socket("192.168.1.172", 8989);

                            //
                            System.out.println(socket.isConnected());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });

        /**
         * Receiving messages sent by the server
         */
        Receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // init ThreadPool
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            // init InputStream
                            is = socket.getInputStream();

                            // Receiving messages
                            isr = new InputStreamReader(is);
                            br = new BufferedReader(isr);


                            response = br.readLine();

                            // show messages
                            Message msg = Message.obtain();
                            msg.what = 0;
                            mMainHandler.sendMessage(msg);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });


        /**
         * send messages to server
         */
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // init ThreadPool
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            // init OutputStream
                            //
                            outputStream = socket.getOutputStream();

                            // write message into OutputStream
                            outputStream.write((mEdit.getText().toString()+"\n").getBytes("utf-8"));


                            // send message to server
                            outputStream.flush();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });


        /**
         * Disconnect
         */
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    // close OutputStream
                    outputStream.close();

                    // close BufferedReader
                    br.close();

                    // close Socket
                    socket.close();

                    //
                    System.out.println(socket.isConnected());

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


    }
}