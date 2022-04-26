package com.truking.wms.tool.utils.com.printer.io;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import java.io.IOException;
import java.util.Vector;

public class UsbPort extends PortManager
{
  private static final String TAG = UsbPort.class.getSimpleName();
  private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
  private UsbDevice usbDevice;
  private UsbManager usbManager;
  private Context mContext;
  private UsbDeviceConnection mmConnection;
  private UsbInterface mmIntf;
  private UsbEndpoint mmEndIn;
  private UsbEndpoint mmEndOut;
  private PendingIntent mPermissionIntent;

  public UsbPort(Context context, UsbDevice usbDevice)
  {
    this.mContext = context;
    this.usbDevice = usbDevice;
    this.usbManager = ((UsbManager)context.getSystemService("usb"));
  }

  private UsbPort() {
  }

  public void setUsbDevice(UsbDevice usbDevice) {
    this.usbDevice = usbDevice;
  }

  public boolean openPort()
  {
    if (this.usbDevice != null) {
      if (!this.usbManager.hasPermission(this.usbDevice)) {
        Log.e(TAG, "USB is not permission");
        this.mPermissionIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent("com.android.example.USB_PERMISSION"), 0);
        this.usbManager.requestPermission(this.usbDevice, this.mPermissionIntent);
      }
      else if (checkUsbDevicePidVid(this.usbDevice)) {
        openUsbPort();
        if ((this.mmEndOut != null) && (this.mmEndIn != null)) {
          return true;
        }
      }
    }

    return false;
  }

  public void writeDataImmediately(Vector<Byte> data) throws IOException
  {
    writeDataImmediately(data, 0, data.size());
  }

  private void openUsbPort() {
    int count = this.usbDevice.getInterfaceCount();
    UsbInterface intf = null;
    int i = 0; if (i < count)
    {
      UsbInterface usbInterface = this.usbDevice.getInterface(i);
      intf = usbInterface;
      if (intf.getInterfaceClass() != 7);
    }

    if (intf != null) {
      this.mmIntf = intf;
      this.mmConnection = null;
      this.mmConnection = this.usbManager.openDevice(this.usbDevice);

      if ((this.mmConnection != null) && 
        (this.mmConnection.claimInterface(intf, true)))
        for (int ii = 0; ii < intf.getEndpointCount(); ii++) {
          UsbEndpoint ep = intf.getEndpoint(ii);
          if (ep.getType() == 2)
            if (ep.getDirection() == 0)
              this.mmEndOut = ep;
            else
              this.mmEndIn = ep;
        }
    }
  }

  public void writeDataImmediately(Vector<Byte> data, int offset, int len)
    throws IOException
  {
    try
    {
      int result = 0;
      Vector sendData = new Vector();

      for (int i = 0; i < data.size(); i++) {
        if (sendData.size() >= 1024) {
          Log.e(TAG, "i = " + i + "\tsendData size -> " + sendData.size() + "\tdata size -> " + data.size());
          result += this.mmConnection.bulkTransfer(this.mmEndOut, convertVectorByteToBytes(sendData), sendData.size(), 1000);
          sendData.clear();
          Log.e(TAG, "sendData.clear() size -> " + sendData.size());
        }
        sendData.add((Byte)data.get(i));
      }

      if (sendData.size() > 0) {
        Log.e(TAG, "sendData size -> " + sendData.size());
        result += this.mmConnection.bulkTransfer(this.mmEndOut, convertVectorByteToBytes(sendData), sendData.size(), 1000);
      }

      if (result == data.size())
        Log.d(TAG, "send success");
    }
    catch (Exception e) {
      Log.d(TAG, "Exception occured while sending data immediately: " + e.getMessage());
    }
  }

  public int readData(byte[] bytes) throws IOException
  {
    if (this.mmConnection != null) {
      return this.mmConnection.bulkTransfer(this.mmEndIn, bytes, bytes.length, 200);
    }
    return 0;
  }

  public boolean closePort()
  {
    if ((this.mmIntf != null) && (this.mmConnection != null)) {
      this.mmConnection.releaseInterface(this.mmIntf);
      this.mmConnection.close();
      this.mmConnection = null;
      return true;
    }
    return false;
  }

  public UsbDevice getUsbDevice() {
    return this.usbDevice;
  }

  private boolean checkUsbDevicePidVid(UsbDevice dev)
  {
    int pid = dev.getProductId();
    int vid = dev.getVendorId();
    boolean rel = false;
    if (((vid == 34918) && (pid == 256)) || ((vid == 1137) && (pid == 85)) || ((vid == 6790) && (pid == 30084)) || 
      ((vid == 26728) && (pid == 256)) || ((vid == 26728) && (pid == 512)) || ((vid == 26728) && (pid == 256)) || 
      ((vid == 26728) && (pid == 768)) || ((vid == 26728) && (pid == 1024)) || ((vid == 26728) && (pid == 1280)) || 
      ((vid == 26728) && (pid == 1536)) || (
      (vid == 7358) && (pid == 2))) {
      rel = true;
    }
    return rel;
  }
}