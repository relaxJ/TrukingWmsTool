package com.truking.wms.tool.utils.com.printer.io;

import android.util.Log;
import com.truking.wms.tool.utils.com.printer.utils.SerialPortControl;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

public class SerialPort extends PortManager
{
  private static final String TAG = "SerialPort";
  private int baudrate;
  private String serialPortPath;
  private int flags;
  private SerialPortControl serialPortControl;

  public SerialPort(String path, int baudrate, int flags)
  {
    this.serialPortPath = path;
    this.baudrate = baudrate;
    this.flags = flags;
  }

  public SerialPort()
  {
  }

  public void setSerialPortPath(String path) {
    this.serialPortPath = path;
  }

  public void setBaudrate(int baudrate) {
    this.baudrate = baudrate;
  }

  public void setFlage(int flags) {
    this.flags = flags;
  }

  public boolean openPort()
  {
    try {
      File file = new File(this.serialPortPath);
      if (file.exists()) {
        this.serialPortControl = new SerialPortControl(file, this.baudrate, this.flags);
        this.inputStream = this.serialPortControl.getInputStream();
        this.outputStream = this.serialPortControl.getOutputStream();
        if ((this.inputStream != null) && (this.outputStream != null))
          return true;
      }
    }
    catch (IOException e) {
      Log.e("SerialPort", "Open serial port error!", e);
    }
    return false;
  }

  public void writeDataImmediately(Vector<Byte> data) throws IOException
  {
    writeDataImmediately(data, 0, data.size());
  }

  public void writeDataImmediately(Vector<Byte> data, int offset, int len) throws IOException
  {
    try {
      if (data.size() > 0) {
        this.outputStream.write(convertVectorByteToBytes(data), offset, len);
        this.outputStream.flush();
      }
    } catch (IOException e) {
      Log.e("SerialPort", "write data error!", e);
    }
  }

  public int readData(byte[] bytes)
    throws IOException
  {
    if (this.inputStream.available() > 0) {
      return this.inputStream.read(bytes);
    }
    return 0;
  }

  public boolean closePort()
  {
    try
    {
      if (this.inputStream != null) {
        this.inputStream.close();
        this.inputStream = null;
      }

      if (this.outputStream != null) {
        this.outputStream.close();
        this.outputStream = null;
      }

      if (this.serialPortControl != null) {
        this.serialPortControl.close();
        this.serialPortControl = null;
      }
      return true;
    } catch (IOException e) {
      Log.e("SerialPort", "Close the steam or serial port error!", e);
    }
    return false;
  }
}