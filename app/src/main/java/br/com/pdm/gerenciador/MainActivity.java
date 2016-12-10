package br.com.pdm.gerenciador;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends Activity {

    ToggleButton tbAlarme;
    ToggleButton tbLuzExternaPorta;
    ToggleButton tbLuzExternaVaranda;
    ToggleButton tbLuzGaragem;
    ToggleButton tbLuzSala;
    ToggleButton tbLuzPrimeiroAndar;
    ToggleButton tbLuzSegundoAndar;
    ToggleButton tbLuzesExternas;
    ToggleButton tbMotorGaragem;
    ToggleButton tbPersonalizadoUm;
    ToggleButton tbPersonalizadoDois;

    Handler h;

    final int RECIEVE_MESSAGE = 1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();

    private ConnectedThread mConnectedThread;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static String address = "20:16:06:06:20:36";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        tbMotorGaragem = (ToggleButton) findViewById(R.id.tbMotorGaragem);
        tbAlarme = (ToggleButton) findViewById(R.id.tbAlarme);
        tbLuzExternaPorta = (ToggleButton) findViewById(R.id.tbLuzExternaPorta);
        tbLuzExternaVaranda = (ToggleButton) findViewById(R.id.tbLuzExternaVaranda);
        tbLuzGaragem = (ToggleButton) findViewById(R.id.tbLuzGaragem);
        tbLuzSala = (ToggleButton) findViewById(R.id.tbLuzSala);
        tbLuzPrimeiroAndar = (ToggleButton) findViewById(R.id.tbLuzPrimeiroAndar);
        tbLuzSegundoAndar = (ToggleButton) findViewById(R.id.tbLuzSegundoAndar);
        tbLuzesExternas = (ToggleButton) findViewById(R.id.tbLuzesExternas);
        tbPersonalizadoUm = (ToggleButton) findViewById(R.id.tbPersonalizadoUm);
        tbPersonalizadoDois = (ToggleButton) findViewById(R.id.tbPersonalizadoDois);

        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);
                        sb.append(strIncom);
                        int endOfLineIndex = sb.indexOf("\r\n");
                        if (endOfLineIndex > 0) {
                            String retorno = sb.substring(0, endOfLineIndex);
                            sb.delete(0, sb.length());

                            String[] comandos = retorno.split(";");

                            Map<String, String> comandoValor = new HashMap<>();

                            for (int i = 0; i < comandos.length; i++) {
                                String[] valor = comandos[i].split(":");
                                if (valor != null && valor.length == 2) {
                                    comandoValor.put(valor[0], valor[1]);
                                }
                            }

                            atualizarInterfaceGrafica(comandoValor);
                        }
                        break;
                }
            }

            ;
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        tbMotorGaragem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConnectedThread.write("1");
            }
        });

        tbAlarme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConnectedThread.write("2");
            }
        });


        tbLuzExternaPorta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConnectedThread.write("3");
            }
        });

        tbLuzExternaVaranda.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConnectedThread.write("4");
            }
        });

        tbLuzGaragem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConnectedThread.write("5");
            }
        });

        tbLuzSala.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConnectedThread.write("6");
            }
        });

        tbLuzPrimeiroAndar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConnectedThread.write("7");
            }
        });

        tbLuzSegundoAndar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConnectedThread.write("8");
            }
        });

        tbLuzesExternas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConnectedThread.write("9");
            }
        });

        tbPersonalizadoUm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConnectedThread.write("10");
            }
        });

        tbPersonalizadoDois.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConnectedThread.write("11");
            }
        });
    }

    private void atualizarInterfaceGrafica(Map<String, String> mapa) {
        if (mapa.get("M1") != null) {
            if (mapa.get("M1").trim().equals("0")) {
                tbMotorGaragem.setChecked(false);
            } else {
                tbMotorGaragem.setChecked(true);
            }
        }

        if (mapa.get("A2") != null) {
            if (mapa.get("A2").trim().equals("0")) {
                tbAlarme.setChecked(false);
            } else {
                tbAlarme.setChecked(true);
            }
        }

        if (mapa.get("L3") != null) {
            if (mapa.get("L3").trim().equals("0")) {
                tbLuzExternaPorta.setChecked(false);
            } else {
                tbLuzExternaPorta.setChecked(true);
            }
        }

        if (mapa.get("L4") != null) {
            if (mapa.get("L4").trim().equals("0")) {
                tbLuzExternaVaranda.setChecked(false);
            } else {
                tbLuzExternaVaranda.setChecked(true);
            }
        }

        if (mapa.get("L5") != null) {
            if (mapa.get("L5").trim().equals("0")) {
                tbLuzGaragem.setChecked(false);
            } else {
                tbLuzGaragem.setChecked(true);
            }
        }

        if (mapa.get("L6") != null) {
            if (mapa.get("L6").trim().equals("0")) {
                tbLuzSala.setChecked(false);
            } else {
                tbLuzSala.setChecked(true);
            }
        }

        if (mapa.get("L7") != null) {
            if (mapa.get("L7").trim().equals("0")) {
                tbLuzPrimeiroAndar.setChecked(false);
            } else {
                tbLuzPrimeiroAndar.setChecked(true);
            }
        }

        if (mapa.get("L8") != null) {
            if (mapa.get("L8").trim().equals("0")) {
                tbLuzSegundoAndar.setChecked(false);
            } else {
                tbLuzSegundoAndar.setChecked(true);
            }
        }

        if (mapa.get("L9") != null) {
            if (mapa.get("L9").trim().equals("0")) {
                tbLuzesExternas.setChecked(false);
            } else {
                tbLuzesExternas.setChecked(true);
            }
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if (Build.VERSION.SDK_INT >= 10) {
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[]{UUID.class});
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
            }
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        btAdapter.cancelDiscovery();

        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {
        if (btAdapter == null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message) {
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(String message) {
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
            }
        }
    }
}
