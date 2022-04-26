package com.truking.wms.tool.utils.com.printer.command;

import android.graphics.Bitmap;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

public class CpclCommand
{
  private static final String DEBUG_TAG = "CpclCommand";
  Vector<Byte> Command = null;

  public CpclCommand()
  {
    this.Command = new Vector();
  }

  public void clrCommand() {
    this.Command.clear();
  }

  private void addStrToCommand(String str)
  {
    byte[] bs = null;
    if (!str.equals("")) {
      try {
        bs = str.getBytes("GB2312");
      }
      catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      for (int i = 0; i < bs.length; i++)
        this.Command.add(Byte.valueOf(bs[i]));
    }
  }

  private void addStrToCommand(String str, TEXT_FONT font)
  {
    byte[] bs = null;
    if (!str.equals("")) {
      try {
        switch (font) {
        case FONT_0:
          bs = str.getBytes("gb18030");
          break;
        case FONT_1:
          bs = str.getBytes("big5");
          break;
        case FONT_10:
          bs = str.getBytes("gb18030");
          break;
        case FONT_11:
          bs = str.getBytes("gbk");
          break;
        case FONT_13:
          bs = str.getBytes("gbk");
          break;
        case FONT_24:
          bs = str.getBytes("gb18030");
          break;
        case FONT_3:
          bs = str.getBytes("gb18030");
          break;
        case FONT_42:
          bs = str.getBytes("big5");
          break;
        case FONT_43:
          bs = str.getBytes("gb18030");
          break;
        case FONT_44:
          bs = str.getBytes("gb18030");
          break;
        case FONT_2:
        case FONT_20:
        case FONT_4:
        case FONT_41:
        default:
          bs = str.getBytes("gb2312");
        }

      }
      catch (UnsupportedEncodingException e)
      {
        e.printStackTrace();
      }
      for (int i = 0; i < bs.length; i++)
        this.Command.add(Byte.valueOf(bs[i]));
    }
  }

  public void addInitializePrinter()
  {
    String str = "! 0 200 200 210 1\r\n";
    addStrToCommand(str);
  }

  public void addInitializePrinter(int qty) {
    String str = "! 0 200 200 210 " + qty + "\r\n";
    addStrToCommand(str);
  }
  public void addInitializePrinter(int height, int qty) {
    String str = "! 0 200 200 " + height + " " + qty + "\r\n";
    addStrToCommand(str);
  }

  public void addInitializePrinter(int offset, int height, int qty) {
    String str = "! " + offset + " 200 200 " + height + " " + qty + "\r\n";
    addStrToCommand(str);
  }

  public void addPrint()
  {
    String str = "PRINT\r\n";
    addStrToCommand(str);
  }

  public void addText(TEXT_FONT font, int x, int y, String text)
  {
    String str = "TEXT " + font.getValue() + " 0 " + x + " " + y + " " + 
      text + "\r\n";
    addStrToCommand(str, font);
  }
  public void addText(TEXT_FONT font, int size, int x, int y, String text) {
    String str = "TEXT " + font.getValue() + " " + size + " " + x + " " + y + " " + 
      text + "\r\n";
    addStrToCommand(str, font);
  }
  public void addText90(TEXT_FONT font, int x, int y, String text) {
    String str = "TEXT90 " + font.getValue() + " 0 " + x + " " + y + " " + 
      text + "\r\n";
    addStrToCommand(str, font);
  }

  public void addText180(TEXT_FONT font, int x, int y, String text) {
    String str = "TEXT180 " + font.getValue() + " 0 " + x + " " + y + " " + 
      text + "\r\n";
    addStrToCommand(str, font);
  }

  public void addText270(TEXT_FONT font, int x, int y, String text) {
    String str = "TEXT270 " + font.getValue() + " 0 " + x + " " + y + " " + 
      text + "\r\n";
    addStrToCommand(str, font);
  }

  public void addText(TEXTFONT font, int x, int y, String text, ALIGNMENT align, int n) throws NumberFormatException, UnsupportedEncodingException {
    boolean var1 = false;
    boolean var2 = false;
    boolean var3 = false;
    boolean var4 = false;
    if ((n & 0x1) == 1) {
      var1 = true;
    }

    if ((n & 0x2) == 2) {
      var2 = true;
    }

    if ((n & 0x4) == 4) {
      var3 = true;
    }

    if ((n & 0x8) == 8) {
      var4 = true;
    }
    if (var1) {
      addSetbold(BOLD.ON);
    }

    if (var4) {
      addSetmag(1, 2);
    }

    if (var3) {
      addSetmag(2, 1);
    }

    if ((var4 & var3)) {
      addSetmag(2, 2);
    }
    addJustification(align);
    switch (font) {
    case FONT_18:
      addText(TEXT_FONT.FONT_55, x, y, text);
      break;
    case FONT_24:
      addText(TEXT_FONT.FONT_8, x, y, text);
      break;
    case FONT_32:
      addText(TEXT_FONT.FONT_4, x, y, text);
    }

    if (var2)
    {
      float var13;
      if (var3)
        var13 = Integer.valueOf(x).intValue() + text.getBytes("GBK").length * Float.parseFloat(font.getValue());
      else {
        var13 = Integer.valueOf(x).intValue() + text.getBytes("GBK").length * Float.parseFloat(font.getValue()) / 2.0F;
      }

      if (var4)
        addInverseLine(x, y, (int)var13, y, Integer.valueOf(font.getValue()).intValue() * 2);
      else {
        addInverseLine(x, y, (int)var13, y, Integer.valueOf(font.getValue()).intValue());
      }
    }
    if ((var3 | var4)) {
      addSetmag(1, 1);
    }
    if (var1) {
      addSetbold(BOLD.OFF);
    }
    addJustification(ALIGNMENT.LEFT);
  }

  public void addText180(TEXTFONT font, int x, int y, String text, int n) throws NumberFormatException, UnsupportedEncodingException
  {
    boolean var1 = false;
    boolean var2 = false;
    boolean var3 = false;
    boolean var4 = false;
    if ((n & 0x1) == 1) {
      var1 = true;
    }

    if ((n & 0x2) == 2) {
      var2 = true;
    }

    if ((n & 0x4) == 4) {
      var3 = true;
    }

    if ((n & 0x8) == 8) {
      var4 = true;
    }
    if (var1) {
      addSetbold(BOLD.ON);
    }

    if (var4) {
      addSetmag(1, 2);
    }

    if (var3) {
      addSetmag(2, 1);
    }

    if ((var4 & var3)) {
      addSetmag(2, 2);
    }
    addJustification(ALIGNMENT.LEFT);
    switch (font) {
    case FONT_18:
      addText(TEXT_FONT.FONT_55, x, y, text);
      break;
    case FONT_24:
      addText(TEXT_FONT.FONT_8, x, y, text);
      break;
    case FONT_32:
      addText(TEXT_FONT.FONT_4, x, y, text);
    }

    if (var2)
    {
      float var13;
      if (var3)
        var13 = text.getBytes("GBK").length * Float.parseFloat(font.getValue());
      else {
        var13 = text.getBytes("GBK").length * Float.parseFloat(font.getValue()) / 2.0F;
      }

      if (var4)
        addInverseLine((int)(x - var13), y - Integer.valueOf(font.getValue()).intValue() * 2, x, y - Integer.valueOf(font.getValue()).intValue() * 2, Integer.valueOf(font.getValue()).intValue() * 2);
      else {
        addInverseLine((int)(x - var13), y - Integer.valueOf(font.getValue()).intValue(), x, y - Integer.valueOf(font.getValue()).intValue(), Integer.valueOf(font.getValue()).intValue());
      }
    }
    if ((var3 | var4)) {
      addSetmag(1, 1);
    }
    if (var1)
      addSetbold(BOLD.OFF);
  }

  public void addTextConcat(int x, int y, String[] var)
  {
    String str = "CONCAT " + x + " " + y + "\r\n";
    for (int i = 0; i < var.length; i++) {
      str = str + var[i] + "\r\n";
    }
    str = str + "ENDCONCAT\r\n";
    addStrToCommand(str);
  }

  public void addCount(String value)
  {
    String str = "COUNT " + value + "\r\n";
    addStrToCommand(str);
  }

  public void addSetmag(int w, int h)
  {
    if (w > 16)
      w = 16;
    else if (w < 1) {
      w = 1;
    }
    if (h > 16)
      h = 16;
    else if (h < 1) {
      h = 1;
    }
    String str = "SETMAG " + w + " " + h + "\r\n";
    addStrToCommand(str);
  }

  public void addBarcode(COMMAND command, CPCLBARCODETYPE type, int height, int x, int y, String text)
  {
    int width = 2; int ratio = 2;
    switch (type) {
    case CODE128:
      width = 2;
      ratio = 1;
      break;
    case CODE39:
      width = 2;
      ratio = 1;
      break;
    case CODE93:
      width = 2;
      ratio = 1;
      break;
    case EAN_13:
      width = 2;
      ratio = 1;
      break;
    case EAN_8:
      width = 2;
      ratio = 2;
      break;
    case UPC_A:
      width = 1;
      ratio = 0;
      break;
    case CODABAR:
      width = 2;
      ratio = 1;
      break;
    case UPC_E:
      width = 2;
      ratio = 2;
    }

    String str = command.getValue() + " " + type.getValue() + " " + width + " " + ratio + 
      " " + height + " " + x + " " + y + " " + text + "\r\n";
    addStrToCommand(str);
  }

  public void addBarcode(COMMAND command, CPCLBARCODETYPE type, int height, int x, int y, int number, int offset, String text)
  {
    int width = 2; int ratio = 2;
    switch (type) {
    case CODE128:
      width = 2;
      ratio = 1;
      break;
    case CODE39:
      width = 2;
      ratio = 1;
      break;
    case CODE93:
      width = 2;
      ratio = 1;
      break;
    case EAN_13:
      width = 2;
      ratio = 1;
      break;
    case EAN_8:
      width = 2;
      ratio = 2;
      break;
    case UPC_A:
      width = 1;
      ratio = 0;
      break;
    case CODABAR:
      width = 2;
      ratio = 1;
      break;
    case UPC_E:
      width = 2;
      ratio = 2;
    }

    addBarcodeText(number, offset);
    String str = command.getValue() + " " + type.getValue() + " " + width + " " + ratio + 
      " " + height + " " + x + " " + y + " " + text + "\r\n";
    addStrToCommand(str);
    addBarcodeTextOff();
  }

  public void addBarcode(COMMAND command, CPCLBARCODETYPE type, int width, BARCODERATIO ratio, int height, int x, int y, String text)
  {
    String str = command.getValue() + " " + type.getValue() + " " + width + " " + ratio.getValue() + 
      " " + height + " " + x + " " + y + " " + text + "\r\n";
    addStrToCommand(str);
  }

  public void addBarcode(COMMAND command, CPCLBARCODETYPE type, int width, BARCODERATIO ratio, int height, int x, int y, int number, int offset, String text)
  {
    addBarcodeText(number, offset);
    String str2 = command.getValue() + " " + type.getValue() + " " + width + " " + ratio.getValue() + 
      " " + height + " " + x + " " + y + " " + text + "\r\n";
    addStrToCommand(str2);
    addBarcodeTextOff();
  }

  public void addPdf417(COMMAND command, int x, int y, String data)
  {
    int xd = 2; int yd = 6; int c = 3; int s = 1;
    String str = command.getValue() + " PDF417 " + x + " " + y + " XD " + xd + " YD " + yd + " C " + c + " S " + s + "\r\n" + data + "\r\n" + "ENDPDF\r\n";
    addStrToCommand(str);
  }

  public void addPdf417(COMMAND command, int x, int y, int xd, int yd, int c, int s, String data) {
    String str = command.getValue() + " PDF417 " + x + " " + y + " XD " + xd + " YD " + yd + " C " + c + " S " + s + "\r\n" + data + "\r\n" + "ENDPDF\r\n";
    addStrToCommand(str);
  }

  public void addBarcodeText(int font, int offset)
  {
    String str = "BARCODE-TEXT " + font + " 0 " + offset + "\r\n";
    addStrToCommand(str);
  }

  public void addBarcodeTextOff()
  {
    String str = "BARCODE-TEXT OFF\r\n";
    addStrToCommand(str);
  }

  public void addBQrcode(int x, int y, int n, int u, String text)
  {
    if (n > 2)
      n = 2;
    else if (n < 1) {
      n = 1;
    }
    if (u > 32)
      u = 32;
    else if (u < 1) {
      u = 1;
    }
    String str = "BARCODE QR " + x + " " + y + " M " + n + " U " + u + "\r\n" + "MA," + text + "\r\n" + "ENDQR\r\n";
    addStrToCommand(str);
  }

  public void addBQrcode(int x, int y, String text)
  {
    int n = 2; int u = 6;
    String str = "BARCODE QR " + x + " " + y + " M " + n + " U " + u + "\r\n" + "MA," + text + "\r\n" + "ENDQR\r\n";
    addStrToCommand(str);
  }

  public void addVBQrcode(int x, int y, int n, int u, String text) {
    if (n > 2)
      n = 2;
    else if (n < 1) {
      n = 1;
    }
    if (u > 32)
      u = 32;
    else if (u < 1) {
      u = 1;
    }
    String str = "VBARCODE QR " + x + " " + y + " M " + n + " U " + u + "\r\n" + "MA," + text + "\r\n" + "ENDQR\r\n";
    addStrToCommand(str);
  }

  public void addVBQrcode(int x, int y, String text)
  {
    int n = 2; int u = 6;
    String str = "VBARCODE QR " + x + " " + y + " M " + n + " U " + u + "\r\n" + "MA," + text + "\r\n" + "ENDQR\r\n";
    addStrToCommand(str);
  }

  public void addBox(int x, int y, int xend, int yend, int thickness)
  {
    String str = "BOX " + x + " " + y + " " + xend + " " + yend + " " + thickness + "\r\n";
    addStrToCommand(str);
  }

  public void addLine(int x, int y, int xend, int yend, int width)
  {
    String str = "LINE " + x + " " + y + " " + xend + " " + yend + " " + width + "\r\n";
    addStrToCommand(str);
  }

  public void addInverseLine(int x, int y, int xend, int yend, int width)
  {
    String str = "INVERSE-LINE " + x + " " + y + " " + xend + " " + yend + " " + width + "\r\n";
    addStrToCommand(str);
  }

  public void addEGraphics(int x, int y, int nWidth, Bitmap bitmap)
  {
    if (bitmap != null) {
      int width = (nWidth + 7) / 8 * 8;
      int height = bitmap.getHeight() * width / bitmap.getWidth();
      Bitmap grayBitmap = GpUtils.toGrayscale(bitmap);
      Bitmap rszBitmap = GpUtils.resizeImage(grayBitmap, width, height);
      byte[] src = GpUtils.bitmapToBWPix(rszBitmap);
      height = src.length / width;
      byte[] codecontent = GpUtils.pixToEscRastBitImageCmd(src);
      String data = toHexString1(codecontent);
      String str = "EG " + width / 8 + " " + height + " " + x + " " + y + " " + data + "\r\n";
      addStrToCommand(str);
    }
  }

  public String toHexString1(byte[] b)
  {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < b.length; i++) {
      buffer.append(toHexString2(b[i]));
    }
    return buffer.toString();
  }

  public String toHexString2(byte b)
  {
    String s = Integer.toHexString(b & 0xFF);
    if (s.length() == 1) {
      return "0" + s.toUpperCase();
    }
    return s.toUpperCase();
  }

  public void addCGraphics(int x, int y, int nWidth, Bitmap bitmap) {
    if (bitmap != null) {
      int width = (nWidth + 7) / 8 * 8;
      int height = bitmap.getHeight() * width / bitmap.getWidth();
      Bitmap grayBitmap = GpUtils.toGrayscale(bitmap);
      Bitmap rszBitmap = GpUtils.resizeImage(grayBitmap, width, height);
      byte[] src = GpUtils.bitmapToBWPix(rszBitmap);
      height = src.length / width;
      String str = "CG " + width / 8 + " " + height + " " + x + " " + y + " ";
      addStrToCommand(str);
      byte[] codecontent = GpUtils.pixToEscRastBitImageCmd(src);
      for (int k = 0; k < codecontent.length; k++) {
        this.Command.add(Byte.valueOf(codecontent[k]));
      }
      addStrToCommand("\r\n");
    }
  }

  public void addJustification(ALIGNMENT align)
  {
    String str = align.getValue() + "\r\n";
    addStrToCommand(str);
  }
  public void addJustification(ALIGNMENT align, int end) {
    String str = align.getValue() + " " + end + "\r\n";
    addStrToCommand(str);
  }

  public void addPagewidth(int width)
  {
    String str = "PAGE-WIDTH " + width + "\r\n";
    addStrToCommand(str);
  }

  public void addSpeed(CPCLSPEED level)
  {
    String str = "SPEED " + level.getValue() + "\r\n";
    addStrToCommand(str);
  }

  public void addCountry(COUNTRY name)
  {
    String str = "COUNTRY " + name.getValue() + "\r\n";
    addStrToCommand(str);
  }

  public void addBeep(int beep_length)
  {
    String str = "BEEP " + beep_length + "\r\n";
    addStrToCommand(str);
  }

  public void addQueryPrinterStatus()
  {
    this.Command.add(Byte.valueOf((byte)27));
    this.Command.add(Byte.valueOf((byte)104));
  }

  public void addForm()
  {
    String str = "FORM\r\n";
    addStrToCommand(str);
  }

  public void addNote(String text)
  {
    String str = ";" + text + "\r\n";
    addStrToCommand(str);
  }

  public void addEnd()
  {
    String str = "END\r\n";
    addStrToCommand(str);
  }

  public void addSetsp(int spacing)
  {
    String str = "SETSP " + spacing + "\r\n";
    addStrToCommand(str);
  }

  public void addSetbold(BOLD value)
  {
    String str = "SETBOLD " + value.getValue() + "\r\n";
    addStrToCommand(str);
  }

  public void addSetlf(int height)
  {
    String str = "!U1 SETLF " + height + "\r\n";
    addStrToCommand(str);
  }

  public void addSetlp(int font, int size, int spacing) {
    String str = "!U1 SETLP " + font + " " + size + " " + spacing + "\r\n";
    addStrToCommand(str);
  }

  public void addPREtension(int length)
  {
    String str = "PRE-TENSION " + length + "\r\n";
    addStrToCommand(str);
  }

  public void addPOSTtension(int length)
  {
    String str = "POST-TENSION " + length + "\r\n";
    addStrToCommand(str);
  }

  public void addWait(int time)
  {
    String str = "WAIT " + time + "\r\n";
    addStrToCommand(str);
  }

  public void addUserCommand(byte[] command) {
    for (int i = 0; i < command.length; i++)
      this.Command.add(Byte.valueOf(command[i]));
  }

  public Vector<Byte> getCommand()
  {
    return this.Command;
  }

  public static enum ALIGNMENT
  {
    CENTER("CENTER"), LEFT("LEFT"), RIGHT("RIGHT");

    private final String value;

    private ALIGNMENT(String value) { this.value = value; }

    public String getValue()
    {
      return this.value;
    }
  }

  public static enum BARCODERATIO
  {
    Point0("0"), Point1("1"), Point2("2"), Point3("3"), Point4("4"), Point20("20"), 
    Point21("21"), Point22("22"), Point23("23"), Point24("24"), Point25("25"), Point26("26"), 
    Point27("27"), Point28("28"), Point29("29"), Point30("30");

    private final String value;

    private BARCODERATIO(String value) {
      this.value = value;
    }

    public String getValue() {
      return this.value;
    }
  }

  public static enum BOLD
  {
    ON("1"), OFF("0");

    private final String value;

    private BOLD(String value) { this.value = value; }

    public String getValue()
    {
      return this.value;
    }
  }

  public static enum COMMAND
  {
    BARCODE("BARCODE"), VBARCODE("VBARCODE");

    private final String value;

    private COMMAND(String value) { this.value = value; }

    public String getValue()
    {
      return this.value;
    }
  }

  public static enum COUNTRY
  {
    PC850("PC850"), PC852("PC852"), PC860("PC860"), FRANCE("PC863"), PC865("PC865"), PC866("PC866"), PC858("PC858"), PC747("PC747"), PC864("PC864"), PC1001("PC1001"), 
    PT1251("PT1251"), WPC1253("WPC1253"), WPC1254("WPC1254"), WPC1257("WPC1257"), KATAKANA("KATAKANA"), WEST_EUROPE("WEST_EUROPE"), GREEK("GREEK"), HEBREW("HEBREW"), 
    EAST_EUROPE("EAST_EUROPE"), IRAN("IRAN"), IRANII("IRANII"), LATVIAN("LATVIAN"), ARABIC("ARABIC"), 
    UYGUR("UYGUR"), THAI("THAI"), USA("PC473");

    private final String value;

    private COUNTRY(String value) { this.value = value; }

    public String getValue()
    {
      return this.value;
    }
  }

  public static enum CPCLBARCODETYPE
  {
    CODE128("128"), UPC_A("UPCA"), UPC_E("UPCE"), EAN_13("EAN13"), EAN_8(
      "EAN8"),  CODE39("39"), CODE93("93"), CODABAR("CODABAR");

    private final String value;

    private CPCLBARCODETYPE(String value) { this.value = value; }

    public String getValue()
    {
      return this.value;
    }
  }

  public static enum CPCLSPEED
  {
    SPEED0("0"), SPEED1("1"), SPEED2("2"), SPEED3("3"), SPEED4("4"), SPEED5("5");

    private final String value;

    private CPCLSPEED(String value) { this.value = value; }

    public String getValue()
    {
      return this.value;
    }
  }

  public static enum TEXTFONT
  {
    FONT_18("18"), FONT_24("24"), FONT_32("32");

    private final String value;

    private TEXTFONT(String value) { this.value = value; }

    public String getValue()
    {
      return this.value;
    }
  }

  public static enum TEXT_FONT
  {
    FONT_0("0"), FONT_1("1"), FONT_2("2"), FONT_3("3"), FONT_4("4"), FONT_5(
      "5"),  FONT_6("6"), FONT_7("7"), FONT_8("8"), FONT_10("10"), FONT_11("11"), FONT_13(
      "13"),  FONT_20("20"), FONT_24("24"), FONT_41("41"), FONT_42(
      "42"),  FONT_43("43"), FONT_44("44"), FONT_45("45"), FONT_46(
      "46"),  FONT_47("47"), FONT_48("48"), FONT_49("49"), FONT_55("55");

    private final String value;

    private TEXT_FONT(String value) { this.value = value; }

    public String getValue()
    {
      return this.value;
    }
  }
}