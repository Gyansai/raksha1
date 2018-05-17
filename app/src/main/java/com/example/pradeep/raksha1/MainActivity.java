package com.example.pradeep.raksha1;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity  implements AdapterView.OnItemClickListener  {
    Button b1,b2,b3,b4,b5;
    public  static String deviceAddress;
    private long backpresstime;
    private Toast backtoast;
    private static int SPALSH_SCREEN_TIMEOUT = 4000;
    BluetoothAdapter mbluetoothaadapter ;
    public BluetoothSocket socket;
    public OutputStream outputStream;
    public InputStream inputStream;
    public ArrayList<BluetoothDevice> mbtdevices = new ArrayList<>();
    public Devicelist  mDevicelist;
    ListView Ivnewdevices ;
    Switch s1;
    private static final String TAG = "MainActivity";
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (mbluetoothaadapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mbluetoothaadapter.ERROR);
            }
        }
    };
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                mbtdevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                mDevicelist = new Devicelist(context,R.layout.devicelist,mbtdevices);
                Ivnewdevices.setAdapter(mDevicelist);
            }
        }
    };
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");




                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b1 = (Button)findViewById(R.id.bt1);
        b2 = (Button)findViewById(R.id.bt2);
        b3 = (Button)findViewById(R.id.b3);
        b4=(Button)findViewById(R.id.button);
        b5=(Button)findViewById(R.id.bt7);
        s1=(Switch)findViewById(R.id.switch1);
        s1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
            if (isChecked == true){enablebt();}
            if(isChecked == false){
                if (mbluetoothaadapter.isEnabled())
                {btoff();}
            }
            }
        });

        Ivnewdevices = (ListView)findViewById(R.id.lvNewDevices) ;
        mbtdevices= new ArrayList<>();

        Ivnewdevices.setOnItemClickListener(MainActivity.this);
        mbluetoothaadapter= BluetoothAdapter.getDefaultAdapter();


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                enablebt();
            }
        });
        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);


    }

    private void btoff() {
        if (mbluetoothaadapter.isEnabled()) {
            mbluetoothaadapter.disable();

            IntentFilter BTintent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTintent);

        }

    }


    public void   enablebt(){


        if(mbluetoothaadapter==null) {
            Log.d(TAG,"enablebt dose not have bluetooth ");
        }
      if (!mbluetoothaadapter.isEnabled()){
          Intent enablebt= new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
          startActivity(enablebt);
          IntentFilter BTintent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
          registerReceiver(mBroadcastReceiver1,BTintent);
      }


    }

     public void enabledisc(View view){
         Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");

         Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
         discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
         startActivity(discoverableIntent);

         IntentFilter intentFilter = new IntentFilter(mbluetoothaadapter.ACTION_SCAN_MODE_CHANGED);
         registerReceiver(mBroadcastReceiver2,intentFilter);

    }


    public void scanning(View view) {

        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");
        Ivnewdevices.setVisibility(View.VISIBLE);


        if(mbluetoothaadapter.isDiscovering()){
            mbluetoothaadapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            mbluetoothaadapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if(!mbluetoothaadapter.isDiscovering()){

            //check BT permissions in manifest
            checkBTPermissions();

            mbluetoothaadapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }


    }

    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mbluetoothaadapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: You Clicked on a device.");
         String deviceName = mbtdevices.get(i).getName();
           deviceAddress = mbtdevices.get(i).getAddress();
        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);
        //create the bond.
        //NOTE: Requires API 17+? I think this is JellyBean
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG, "Trying to pair with " + deviceName);
            mbtdevices.get(i).createBond();
            Log.d(TAG, "Trying to connnect");





        }






     }


    public void connect(View view) {
    btconnct();

    }

    private void btconnct() {
        Set<BluetoothDevice> bondedDevices = mbluetoothaadapter.getBondedDevices();
        BluetoothDevice device;
        String DEVICE = "IOT_MOTOR";
        if (bondedDevices.isEmpty()) //Checks for paired bluetooth devices
        {
            Toast.makeText(getApplicationContext(), "Please pair the device first", Toast.LENGTH_SHORT).show();
        } else {
            for (BluetoothDevice iterator : bondedDevices) {
                if (iterator.getName().equals(DEVICE)) {
                    device = iterator;
                    break;
                }
            }
        }
        final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        mbluetoothaadapter.getAddress();
        BluetoothDevice dispositivo = mbluetoothaadapter.getRemoteDevice(deviceAddress);//connects to the device's address and checks if it's available
        try {
            socket = dispositivo.createInsecureRfcommSocketToServiceRecord(PORT_UUID);//create a RFCOMM (SPP) connection
            socket.connect();
            Log.d(TAG, "connnected");
            outputStream = socket.getOutputStream();

            Toast.makeText(getApplicationContext(),
                    "Connection to bluetooth device successful", Toast.LENGTH_LONG).show();
            Ivnewdevices.setVisibility(View.INVISIBLE);

        } catch (IOException e) {
            e.printStackTrace();
        }




     }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            onBackPressed();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        //this is only needed if you have specific things
        //that you want to do when the user presses the back button.
        /* your specific things...*/
        //super.onBackPressed();

        if (backpresstime + 2000 > System.currentTimeMillis()) {
            backtoast.cancel();
            /*unregisterReceiver(mBroadcastReceiver1);
            unregisterReceiver(mBroadcastReceiver1);
            unregisterReceiver(mBroadcastReceiver2);*/
            if(mBroadcastReceiver1.equals(true)){unregisterReceiver(mBroadcastReceiver1); }
            if(mBroadcastReceiver2.equals(true)){unregisterReceiver(mBroadcastReceiver2); }
            if(mBroadcastReceiver3.equals(true)){unregisterReceiver(mBroadcastReceiver3); }
            if(mBroadcastReceiver4.equals(true)){unregisterReceiver(mBroadcastReceiver4); }
            if(mbluetoothaadapter.isDiscovering()){mbluetoothaadapter.cancelDiscovery(); }


           // super.onBackPressed();
            finish();
            return;
        }
        else {
            backtoast = Toast.makeText(getBaseContext(),"Press back again to go back to main menu",Toast.LENGTH_SHORT);
            backtoast.show();
        }
        backpresstime=System.currentTimeMillis();
    }


    public void start(View view) {
       String command = "4";
        int count = 0;

        while(count == 0) {

            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int byteCount = 0;
            try {
                byteCount = inputStream.available();

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (byteCount > 0) {
                byte[] rawBytes = new byte[byteCount];
                try {
                    inputStream.read(rawBytes);
                    Toast.makeText(getApplicationContext(),
                            "byte recived ", Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    final String read = new String(rawBytes, "UTF-8");
                    if (read.equals("1")) {
                        count = 1;
                        Intent intent = new Intent(MainActivity.this, falldetected.class);
                        intent.putExtra("EXIT", false);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }


        }
    }
}



