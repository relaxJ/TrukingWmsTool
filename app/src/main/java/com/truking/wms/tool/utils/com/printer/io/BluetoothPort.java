package com.truking.wms.tool.utils.com.printer.io;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.Vector;

public class BluetoothPort extends PortManager
{
  private static final String TAG = BluetoothPort.class.getSimpleName();

  private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  private static final String NAME = "BluetoothPort";
  private BluetoothAdapter mAdapter = null;
  private BluetoothDevice device;
  private BluetoothSocket mSocket;
  private int state = 0;
  private int len = 0;
  private String macAddress;

  private void initSocketStream()
    throws IOException
  {
    this.inputStream = this.mSocket.getInputStream();
    this.outputStream = this.mSocket.getOutputStream();
  }

  public boolean openPort()
  {
    this.mAdapter = BluetoothAdapter.getDefaultAdapter();
    this.mAdapter.cancelDiscovery();

    if (this.mAdapter == null) {
      this.state = 0;
      Log.e(TAG, "Bluetooth is not support");
    }
    else if (!this.mAdapter.isEnabled()) {
      this.state = 0;
      Log.e(TAG, "Bluetooth is not open");
    } else {
      try {
        if (BluetoothAdapter.checkBluetoothAddress(this.macAddress)) {
          this.device = this.mAdapter.getRemoteDevice(this.macAddress);
          this.mSocket = this.device.createInsecureRfcommSocketToServiceRecord(SERIAL_PORT_SERVICE_CLASS_UUID);
          this.mSocket.connect();

          initSocketStream();
          this.state = 3;
          return true;
        }
        this.state = 0;
        Log.e(TAG, "Bluetooth address is invalid");
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }

    }

    this.macAddress = "";
    return false;
  }

  public BluetoothPort(String macAddress) {
    this.macAddress = macAddress;
  }
  public void writeData(byte[] data) {
    if ((this.mSocket != null) && (this.outputStream != null) && 
      (data != null) && (data.length > 0))
      try {
        this.outputStream.write(data, 0, data.length);
        this.outputStream.flush();
      } catch (IOException e) {
        Log.e(TAG, "Exception occured while sending data immediately: ", e);
      }
  }

  public void writeDataImmediately(Vector<Byte> data)
  {
    writeDataImmediately(data, 0, data.size());
  }

  public void writeDataImmediately(Vector<Byte> data, int offset, int len)
  {
    if ((this.mSocket != null) && (this.outputStream != null) && 
      (data != null) && (data.size() > 0))
      try {
        this.outputStream.write(convertVectorByteToBytes(data), offset, len);
        this.outputStream.flush();
      } catch (IOException e) {
        Log.e(TAG, "Exception occured while sending data immediately: ", e);
      }
  }

  public int readData(byte[] bytes)
    throws IOException
  {
    if (this.inputStream == null) {
      return -1;
    }
    if (this.inputStream.available() > 0) {
      this.len = this.inputStream.read(bytes);
      return this.len;
    }if (this.inputStream.available() == -1) {
      return -1;
    }
    return 0;
  }

  public boolean closePort()
  {
    try
    {
      closeConn();
      this.state = 0;
      return true;
    } catch (IOException e) {
      Log.e(TAG, "Close port error! ", e);
    }
    return false;
  }

  private void closeConn()
    throws IOException
  {
    if (this.inputStream != null) {
      this.inputStream.close();
      this.inputStream = null;
    }

    if (this.outputStream != null) {
      this.outputStream.close();
      this.outputStream = null;
    }

    if (this.mSocket != null) {
      this.mSocket.close();
      this.mSocket = null;
    }
  }
}