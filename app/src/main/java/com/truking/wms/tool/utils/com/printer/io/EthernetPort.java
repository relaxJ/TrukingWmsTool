package com.truking.wms.tool.utils.com.printer.io;

import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Vector;

public class EthernetPort extends PortManager
{
  private static final String TAG = EthernetPort.class.getSimpleName();
  private Socket mSocket;
  private InetAddress mInetAddress;
  private String mIp;
  private int mPort;
  private SocketAddress mSocketAddress;
  private boolean connSuccessful;
  private boolean connState;

  public EthernetPort(String ip, int port)
  {
    this.mIp = ip;
    this.mPort = port;
  }

  public EthernetPort()
  {
  }

  public void setIp(String ip)
  {
    this.mIp = ip;
  }

  public void setPort(int port) {
    this.mPort = port;
  }

  private void initSocketStream()
    throws IOException
  {
    this.inputStream = this.mSocket.getInputStream();
    this.outputStream = this.mSocket.getOutputStream();
  }

  public boolean openPort()
  {
    Thread thread = new Thread()
    {
      public void run() {
        EthernetPort.this.mSocket = new Socket();
        try {
          EthernetPort.this.mInetAddress = Inet4Address.getByName(EthernetPort.this.mIp);
          EthernetPort.this.mSocketAddress = new InetSocketAddress(EthernetPort.this.mInetAddress, EthernetPort.this.mPort);

          EthernetPort.this.mSocket.connect(EthernetPort.this.mSocketAddress, 4000);

          EthernetPort.this.initSocketStream();
          EthernetPort.this.connSuccessful = true;
        } catch (UnknownHostException e) {
          Log.e(EthernetPort.TAG, "IpAddress is invalid", e);
          EthernetPort.this.connSuccessful = false;
        } catch (IOException e) {
          EthernetPort.this.connSuccessful = false;
          Log.e(EthernetPort.TAG, "connect failed", e);
          try {
            if (EthernetPort.this.mSocket != null)
              EthernetPort.this.mSocket.close();
          }
          catch (IOException e1)
          {
            Log.e(EthernetPort.TAG, "unable to close() socket during connection failure", e1);
          }
        }
      }
    };
    thread.start();
    try {
      thread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return this.connSuccessful;
  }

  public void writeDataImmediately(Vector<Byte> data)
    throws IOException
  {
    writeDataImmediately(data, 0, data.size());
  }

  public void writeDataImmediately(final Vector<Byte> data, final int offset, final int len)
    throws IOException
  {
    new Thread()
    {
      public void run()
      {
        try {
          if ((EthernetPort.this.mSocket != null) && (EthernetPort.this.outputStream != null) && 
            (data.size() > 0))
          {
            EthernetPort.this.outputStream.write(EthernetPort.this.convertVectorByteToBytes(data), offset, len);

            EthernetPort.this.outputStream.flush();
          }
        }
        catch (IOException e) {
          Log.e(EthernetPort.TAG, "EthernetPort.class writeDataImmediately method error!", e);
        }
      }
    }
    .start();
  }

  public int readData(byte[] bytes)
    throws IOException
  {
    if (this.inputStream == null) {
      return -1;
    }
    if (this.inputStream.available() > 0) {
      return this.inputStream.read(bytes);
    }
    return 0;
  }

  public boolean closePort()
  {
    try
    {
      closeStreamAndSocket();
      return true;
    } catch (IOException e) {
      Log.e(TAG, "Close port error!", e);
    }
    return false;
  }

  private void closeStreamAndSocket()
    throws IOException
  {
    if (this.outputStream != null) {
      this.outputStream.close();
      this.outputStream = null;
    }

    if (this.inputStream != null) {
      this.inputStream.close();
      this.inputStream = null;
    }

    if (this.mSocket != null) {
      this.mSocket.close();
      this.mSocket = null;
    }
  }
}