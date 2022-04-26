package com.truking.wms.tool.utils.com.printer.command;

public class FactoryCommand
{
  public static final byte[] ESC_SELFTEST = { 31, 27, 31, -109, 16, 17, 18, 21, 22, 23, 16 };
  public static final byte[] TSC_SELFTEST = "SELFTEST\n".getBytes();

  public static final byte[] POWER = { 31, 27, 31, -88, 16, 17, 18, 19, 20, 21, 119 };

  public static byte[] getSelfTest(int type)
  {
    if ((type > 1) || (type < 0)) {
      return null;
    }
    return type == 0 ? ESC_SELFTEST : TSC_SELFTEST;
  }

  public static byte[] changeWorkMode(int type) {
    if ((type > 2) || (type < 0)) {
      return null;
    }
    byte[] bytes = { 31, 27, 31, -4, 1, 2, 3 };
    if (type == 0)
      bytes[7] = 85;
    else if (type == 1)
      bytes[7] = 51;
    else {
      bytes[7] = 68;
    }
    return bytes;
  }

  public static byte[] searchPower(int type) {
    return type == 0 ? POWER : null;
  }

  public static byte[] updateBluetoothName(String name) {
    if (name.length() <= 9) {
      byte[] bytes = new byte[8 + name.length()];
      bytes[0] = 31;
      bytes[1] = 27;
      bytes[2] = 31;
      bytes[3] = -80;
      bytes[4] = 2;
      bytes[5] = 3;
      bytes[6] = 4;
      bytes[7] = ((byte)name.length());
      for (int i = 0; i < name.length(); i++) {
        bytes[(8 + i)] = ((byte)name.charAt(i));
      }

      return bytes;
    }
    return null;
  }

  public static byte[] updateBluetoothPIN(String PIN)
  {
    if (PIN.length() > 4) {
      return null;
    }

    byte[] bytes = { 31, 27, 31, -79, 2, 3, 4 };

    for (int i = 0; i < PIN.length(); i++) {
      bytes[(7 + i)] = ((byte)PIN.charAt(i));
    }

    return bytes;
  }
}