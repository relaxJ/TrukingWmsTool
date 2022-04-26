package com.truking.wms.tool.utils.com.printer.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

public class PortManager
{
  InputStream inputStream;
  OutputStream outputStream;
  static final int STATE_NONE = 0;
  static final int STATE_LISTEN = 1;
  static final int STATE_CONNECTING = 2;
  static final int STATE_CONNECTED = 3;

  public boolean openPort()
  {
    return false;
  }

  public void writeDataImmediately(Vector<Byte> data) throws IOException
  {
  }

  public void writeDataImmediately(Vector<Byte> data, int offset, int len) throws IOException
  {
  }

  public int readData(byte[] bytes) throws IOException {
    return 0;
  }

  public boolean closePort() {
    return false;
  }

  public InputStream getInputStream()
  {
    return this.inputStream;
  }

  public OutputStream getOutputStream() {
    return this.outputStream;
  }

  protected byte[] convertVectorByteToBytes(Vector<Byte> data) {
    byte[] sendData = new byte[data.size()];
    if (data.size() > 0) {
      for (int i = 0; i < data.size(); i++) {
        sendData[i] = ((Byte)data.get(i)).byteValue();
      }
    }
    return sendData;
  }
}