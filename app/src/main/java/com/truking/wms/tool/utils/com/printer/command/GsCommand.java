package com.truking.wms.tool.utils.com.printer.command;

import android.graphics.Bitmap;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

public class GsCommand
{
  private static final String DEBUG_TAG = "GsCommand";
  Vector<Byte> Command = null;

  public GsCommand() { this.Command = new Vector(); }

  public void clrCommand() {
    this.Command.clear();
  }

  private void addArrayToCommand(byte[] array) {
    for (int i = 0; i < array.length; i++)
      this.Command.add(Byte.valueOf(array[i]));
  }

  public void addClrAndCursorReset()
  {
    byte[] command = { 31, 27, 31, 67, 76, 78, 78 };
    addArrayToCommand(command);
  }

  public void addClr()
  {
    byte[] command = { 31, 27, 31, 67, 76, 78, 82 };
    addArrayToCommand(command);
  }

  public void addOpenBackLight()
  {
    byte[] command = { 31, 27, 31, 76, 78, 69 };
    addArrayToCommand(command);
  }

  public void addCloseBackLight()
  {
    byte[] command = { 31, 27, 31, 76, 78, 68 };
    addArrayToCommand(command);
  }

  public void addOpenCursor()
  {
    byte[] command = { 31, 27, 31, 80, 78, 69 };
    addArrayToCommand(command);
  }

  public void addCloseCursor()
  {
    byte[] command = { 31, 27, 31, 80, 78, 68 };
    addArrayToCommand(command);
  }

  public void addCloseBackLightTime(int L, int H)
  {
    byte[] command = { 31, 27, 31, 78, 79, 70, 70, (byte)L, (byte)H };
    addArrayToCommand(command);
  }

  public void addCursorLoca(int x, int y)
  {
    if (x > 127) {
      x = 127;
    }
    if (y > 63) {
      y = 63;
    }
    byte[] command = { 31, 27, 31, 79, 85, 82, (byte)x, (byte)y };
    addArrayToCommand(command);
  }

  public void addBitmap(Bitmap bitmap, int nWidth)
  {
    if (bitmap != null) {
      int width = (nWidth + 7) / 8 * 8;
      int height = bitmap.getHeight() * width / bitmap.getWidth();
      Bitmap grayBitmap = GpUtils.toGrayscale(bitmap);
      Bitmap rszBitmap = GpUtils.resizeImage(grayBitmap, width, height);
      byte[] src = GpUtils.bitmapToBWPix(rszBitmap);
      byte[] command = new byte[4];
      height = src.length / width;
      command[0] = ((byte)(width / 8 % 256));
      command[1] = ((byte)(width / 8 / 256));
      command[2] = ((byte)(height % 256));
      command[3] = ((byte)(height / 256));
      byte[] codecontent = GpUtils.pixToEscRastBitImageCmd(src);
      byte[] b = { 29, 118, 48 };
      byte[] bytes = new byte[b.length + command.length + codecontent.length];
      System.arraycopy(b, 0, bytes, 0, b.length);
      System.arraycopy(command, 0, bytes, b.length, command.length);
      System.arraycopy(codecontent, 0, bytes, b.length + command.length, codecontent.length);
      addArrayToCommand(bytes);
    }
  }

  public void addContrast(int n)
  {
    if (n > 21) {
      n = 21;
    }
    byte[] command = { 31, 27, 31, 96, (byte)n };
    addArrayToCommand(command);
  }

  public void addBrightness(int n)
  {
    if (n > 5) {
      n = 5;
    }
    byte[] command = { 31, 27, 31, 97, (byte)n };
    addArrayToCommand(command);
  }

  public void addTextAndCursorReset(String str)
    throws UnsupportedEncodingException
  {
    byte[] datas = str.getBytes("gb2312");
    byte[] com = new byte[datas.length + 5];
    com[0] = 31;
    com[1] = 27;
    com[2] = 31;
    com[3] = -52;
    com[4] = ((byte)datas.length);
    for (int i = 5; i < datas.length + 5; i++) {
      com[i] = datas[(i - 5)];
    }
    addArrayToCommand(com);
  }

  public void addText(String str)
    throws UnsupportedEncodingException
  {
    byte[] datas = str.getBytes("gb2312");
    byte[] com = new byte[datas.length + 5];
    com[0] = 31;
    com[1] = 27;
    com[2] = 31;
    com[3] = -51;
    com[4] = ((byte)datas.length);
    for (int i = 5; i < datas.length + 5; i++) {
      com[i] = datas[(i - 5)];
    }
    addArrayToCommand(com);
  }

  public void addOpenAutoIndentation(int n)
  {
    byte[] command = { 31, 27, 31, -83, (byte)n };
    addArrayToCommand(command);
  }

  public void addObtainAutoCloseBackLightTime()
  {
    byte[] command = { 29, -120, 83 };
    addArrayToCommand(command);
  }

  public void addObtainBackLightState()
  {
    byte[] command = { 29, -120, 76 };
    addArrayToCommand(command);
  }

  public void addObtainCursorLoca()
  {
    byte[] command = { 29, -120, 88 };
    addArrayToCommand(command);
  }

  public void addObtainLineAndColumn()
  {
    byte[] command = { 29, -120, 82 };
    addArrayToCommand(command);
  }
}