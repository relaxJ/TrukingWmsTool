package com.truking.wms.tool.utils.com.printer.command;

import android.graphics.Bitmap;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

public class LabelCommand
{
  private static final String DEBUG_TAG = "LabelCommand";
  Vector<Byte> Command = null;

  public LabelCommand()
  {
    this.Command = new Vector();
  }

  public LabelCommand(int width, int height, int gap)
  {
    this.Command = new Vector(4096, 1024);
    addSize(width, height);
    addGap(gap);
  }

  public void clrCommand()
  {
    this.Command.clear();
  }

  public void addStrToCommand(String str, String encode)
  {
    byte[] bs = null;
    if (!str.equals("")) {
      try {
        bs = str.getBytes(encode);
      }
      catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      for (int i = 0; i < bs.length; i++)
        this.Command.add(Byte.valueOf(bs[i]));
    }
  }

  public void addGap(int gap)
  {
    String str = "GAP " + gap + " mm," + 0 + " mm" + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addSize(int width, int height)
  {
    String str = "SIZE " + width + " mm," + height + " mm" + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addCashdrwer(FOOT m, int t1, int t2)
  {
    String str = "CASHDRAWER " + m.getValue() + "," + t1 + "," + t2 + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addOffset(int offset)
  {
    String str = "OFFSET " + offset + " mm" + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addSpeed(SPEED speed)
  {
    String str = "SPEED " + speed.getValue() + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addDensity(DENSITY density)
  {
    String str = "DENSITY " + density.getValue() + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addDirection(DIRECTION direction, MIRROR mirror)
  {
    String str = "DIRECTION " + direction.getValue() + ',' + mirror.getValue() + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addReference(int x, int y)
  {
    String str = "REFERENCE " + x + "," + y + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addShif(int shift)
  {
    String str = "SHIFT " + shift + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addCls()
  {
    String str = "CLS\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addFeed(int dot)
  {
    String str = "FEED " + dot + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addBackFeed(int dot)
  {
    String str = "BACKFEED " + dot + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addFormFeed()
  {
    String str = "FORMFEED\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addHome()
  {
    String str = "HOME\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addPrint(int m, int n)
  {
    String str = "PRINT " + m + "," + n + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addPrint(int m)
  {
    String str = "PRINT " + m + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addCodePage(CODEPAGE page)
  {
    String str = "CODEPAGE " + page.getValue() + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addSound(int level, int interval)
  {
    String str = "SOUND " + level + "," + interval + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addLimitFeed(int n)
  {
    String str = "LIMITFEED " + n + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addSelfTest()
  {
    String str = "SELFTEST\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addBar(int x, int y, int width, int height)
  {
    String str = "BAR " + x + "," + y + "," + width + "," + height + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addText(int x, int y, FONTTYPE font, ROTATION rotation, FONTMUL Xscal, FONTMUL Yscal, String text)
  {

  }
  public void addText(String attr1,String attr2,String attr3,String attr4,String attr5)
  {
//    String str = "TEXT " + x + "," + y + "," + "\"" + font.getValue() + "\"" + "," + rotation.getValue() + "," +
//      Xscal.getValue() + "," + Yscal.getValue() + "," + "\"" + text + "\"" + "\r\n";
    String str = "^XA\n" +
            "^CW1,E:SIMSUN.TTF^FS\n" +
            "^CI28\n" +
            "^FO60,7^A1N,45,45^FH^FD（新）"+attr1+"^FS\n" +
            "^FO0,45^GB900,3,3^FS\n" +
            "^FO60,70^A1N,35,35^FH^FD（旧）"+attr2+"^FS\n" +
            "^FO10,130^A1N,30,30^FH^FD物料名称：^FS\n" +
            "^FO10,160^A1N,25,25^FH^FD"+attr3+"^FS\n" +
            "^FO10,210^A1N,30,30^FH^FD图号规格：^FS\n" +
            "^FO10,240^A1N,25,25^FH^FD"+attr4+"^FS\n" +
            "^FO10,300^A1N,30,30^FH^FD单位： "+attr5+"^FS\n" +
            "^FO300,145\n" +
            "^BQN,2,10,N,Y,Y,D^FD  ,,,"+attr1+",,^FS\n" +
            "^CF0,30\n" +
            "^FO150,205\n" +
            "^XZ\n";
    addStrToCommand(str, "UTF-8");
//    if (font.equals(FONTTYPE.TRADITIONAL_CHINESE))
//      addStrToCommand(str, "Big5");
//    else if (font.equals(FONTTYPE.KOREAN))
//      addStrToCommand(str, "EUC_KR");
//    else
//      addStrToCommand(str, "GB2312");
  }

  public void add1DBarcode(int x, int y, BARCODETYPE type, int height, READABEL readable, ROTATION rotation, String content)
  {
    int narrow = 2; int width = 2;
    String str = "BARCODE " + x + "," + y + "," + "\"" + type.getValue() + "\"" + "," + height + "," + 
      readable.getValue() + "," + rotation.getValue() + "," + narrow + "," + width + "," + "\"" + content + 
      "\"" + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void add1DBarcode(int x, int y, BARCODETYPE type, int height, READABEL readable, ROTATION rotation, int narrow, int width, String content)
  {
    String str = "BARCODE " + x + "," + y + "," + "\"" + type.getValue() + "\"" + "," + height + "," + readable.getValue() + 
      "," + rotation.getValue() + "," + narrow + "," + width + "," + "\"" + content + "\"" + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addBox(int x, int y, int xend, int yend, int thickness)
  {
    String str = "BOX " + x + "," + y + "," + xend + "," + yend + "," + thickness + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addBitmap(int x, int y, BITMAP_MODE mode, int nWidth, Bitmap b)
  {
    if (b != null) {
      int width = (nWidth + 7) / 8 * 8;
      int height = b.getHeight() * width / b.getWidth();
      Log.d("BMP", "bmp.getWidth() " + b.getWidth());
      Bitmap grayBitmap = GpUtils.toGrayscale(b);
      Bitmap rszBitmap = GpUtils.resizeImage(grayBitmap, width, height);
      byte[] src = GpUtils.bitmapToBWPix(rszBitmap);
      height = src.length / width;
      width /= 8;
      String str = "BITMAP " + x + "," + y + "," + width + "," + height + "," + mode.getValue() + ",";
      addStrToCommand(str, "GB2312");
      byte[] codecontent = GpUtils.pixToLabelCmd(src);
      for (int k = 0; k < codecontent.length; k++) {
        this.Command.add(Byte.valueOf(codecontent[k]));
      }
      Log.d("LabelCommand", "codecontent" + codecontent);
    }
  }

  public void addBitmapByMethod(int x, int y, BITMAP_MODE mode, int nWidth, Bitmap b) {
    if (b != null) {
      int width = (nWidth + 7) / 8 * 8;
      int height = b.getHeight() * width / b.getWidth();
      Log.d("BMP", "bmp.getWidth() " + b.getWidth());
      Bitmap rszBitmap = GpUtils.resizeImage(b, width, height);
      Bitmap grayBitmap = GpUtils.filter(rszBitmap, width, height);
      byte[] src = GpUtils.bitmapToBWPix(grayBitmap);
      height = src.length / width;
      width /= 8;
      String str = "BITMAP " + x + "," + y + "," + width + "," + height + "," + mode.getValue() + ",";
      addStrToCommand(str, "GB2312");
      byte[] codecontent = GpUtils.pixToLabelCmd(src);
      for (int k = 0; k < codecontent.length; k++) {
        this.Command.add(Byte.valueOf(codecontent[k]));
      }
      Log.d("LabelCommand", "codecontent" + codecontent);
    }
  }

  public void addBitmap(int x, int y, int nWidth, Bitmap bmp) {
    if (bmp != null) {
      int width = (nWidth + 7) / 8 * 8;
      int height = bmp.getHeight() * width / bmp.getWidth();
      Log.d("BMP", "bmp.getWidth() " + bmp.getWidth());
      Bitmap rszBitmap = GpUtils.resizeImage(bmp, width, height);
      byte[] bytes = GpUtils.printTscDraw(x, y, BITMAP_MODE.OVERWRITE, rszBitmap);
      for (int i = 0; i < bytes.length; i++) {
        this.Command.add(Byte.valueOf(bytes[i]));
      }
      addStrToCommand("\r\n", "GB2312");
    }
  }

  public void addErase(int x, int y, int xwidth, int yheight)
  {
    String str = "ERASE " + x + "," + y + "," + xwidth + "," + yheight + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addReverse(int x, int y, int xwidth, int yheight)
  {
    String str = "REVERSE " + x + "," + y + "," + xwidth + "," + yheight + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addQRCode(int x, int y, EEC level, int cellwidth, ROTATION rotation, String data)
  {
    String str = "QRCODE " + x + "," + y + "," + level.getValue() + "," + cellwidth + "," + 'A' + "," + 
      rotation.getValue() + "," + "\"" + data + "\"" + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public Vector<Byte> getCommand()
  {
    return this.Command;
  }

  public void addQueryPrinterType()
  {
    String str = new String();
    str = "~!T\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addQueryPrinterStatus()
  {
    this.Command.add(Byte.valueOf((byte)27));
    this.Command.add(Byte.valueOf((byte)33));
    this.Command.add(Byte.valueOf((byte)63));
  }

  public void addResetPrinter()
  {
    this.Command.add(Byte.valueOf((byte)27));
    this.Command.add(Byte.valueOf((byte)33));
    this.Command.add(Byte.valueOf((byte)82));
  }

  public void addQueryPrinterLife()
  {
    String str = "~!@\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addQueryPrinterMemory()
  {
    String str = "~!A\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addQueryPrinterFile()
  {
    String str = "~!F\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addQueryPrinterCodePage()
  {
    String str = "~!I\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addPeel(EscCommand.ENABLE enable)
  {
    if (enable.getValue() == 0) {
      String str = "SET PEEL " + enable.getValue() + "\r\n";
      addStrToCommand(str, "GB2312");
    }
  }

  public void addTear(EscCommand.ENABLE enable)
  {
    String str = "SET TEAR " + enable.getValue() + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addCutter(EscCommand.ENABLE enable)
  {
    String str = "SET CUTTER " + enable.getValue() + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addCutterBatch()
  {
    String str = "SET CUTTER BATCH\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addCutterPieces(short number)
  {
    String str = "SET CUTTER " + number + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addReprint(EscCommand.ENABLE enable)
  {
    String str = "SET REPRINT " + enable.getValue() + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addPrintKey(EscCommand.ENABLE enable)
  {
    String str = "SET PRINTKEY " + enable.getValue() + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addPrintKey(int m)
  {
    String str = "SET PRINTKEY " + m + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addPartialCutter(EscCommand.ENABLE enable)
  {
    String str = "SET PARTIAL_CUTTER " + enable.getValue() + "\r\n";

    addStrToCommand(str, "GB2312");
  }

  public void addQueryPrinterStatus(RESPONSE_MODE mode)
  {
    String str = "SET RESPONSE " + mode.getValue() + "\r\n";
    addStrToCommand(str, "GB2312");
  }

  public void addUserCommand(String command) {
    addStrToCommand(command, "GB2312");
  }

  public static enum BARCODETYPE
  {
    CODE128("128"), CODE128M("128M"), EAN128("EAN128"), ITF25("25"), ITF25C("25C"), CODE39("39"), CODE39C(
      "39C"),  CODE39S("39S"), CODE93("93"), EAN13("EAN13"), EAN13_2("EAN13+2"), EAN13_5("EAN13+5"), EAN8(
      "EAN8"),  EAN8_2("EAN8+2"), EAN8_5("EAN8+5"), CODABAR("CODA"), POST("POST"), UPCA(
      "UPCA"),  UPCA_2("UPCA+2"), UPCA_5("UPCA+5"), UPCE("UPCE13"), UPCE_2("UPCE13+2"), UPCE_5(
      "UPCE13+5"),  CPOST("CPOST"), MSI("MSI"), MSIC(
      "MSIC"),  PLESSEY("PLESSEY"), ITF14("ITF14"), EAN14("EAN14");

    private final String value;

    private BARCODETYPE(String value) { this.value = value; }

    public String getValue()
    {
      return this.value;
    }
  }

  public static enum BITMAP_MODE
  {
    OVERWRITE(0), OR(1), XOR(2);

    private final int value;

    private BITMAP_MODE(int value) { this.value = value; }

    public int getValue()
    {
      return this.value;
    }
  }

  public static enum CODEPAGE
  {
    PC437(437), PC850(850), PC852(852), PC860(860), PC863(863), PC865(865), WPC1250(1250), WPC1252(1252), WPC1253(
      1253),  WPC1254(1254);

    private final int value;

    private CODEPAGE(int value) { this.value = value; }

    public int getValue()
    {
      return this.value;
    }
  }

  public static enum DENSITY
  {
    DNESITY0(0), DNESITY1(1), DNESITY2(2), DNESITY3(3), DNESITY4(4), DNESITY5(5), DNESITY6(6), DNESITY7(
      7),  DNESITY8(8), DNESITY9(
      9),  DNESITY10(10), DNESITY11(11), DNESITY12(12), DNESITY13(13), DNESITY14(14), DNESITY15(15);

    private final int value;

    private DENSITY(int value) { this.value = value; }

    public int getValue()
    {
      return this.value;
    }
  }

  public static enum DIRECTION {
    FORWARD(0), BACKWARD(1);

    private final int value;

    private DIRECTION(int value) { this.value = value; }

    public int getValue()
    {
      return this.value;
    }
  }

  public static enum EEC
  {
    LEVEL_L("L"), LEVEL_M("M"), LEVEL_Q("Q"), LEVEL_H("H");

    private final String value;

    private EEC(String value) { this.value = value; }

    public String getValue()
    {
      return this.value;
    }
  }

  public static enum FONTMUL
  {
    MUL_1(1), MUL_2(2), MUL_3(3), MUL_4(4), MUL_5(5), MUL_6(6), MUL_7(7), MUL_8(8), MUL_9(9), MUL_10(10);

    private final int value;

    private FONTMUL(int value) { this.value = value; }

    public int getValue()
    {
      return this.value;
    }
  }

  public static enum FONTTYPE {
    FONT_1("1"), FONT_2("2"), FONT_3("3"), FONT_4("4"), FONT_5("5"), FONT_6("6"), FONT_7("7"), FONT_8("8"), FONT_9(
      "9"),  FONT_10("10"), SIMPLIFIED_CHINESE("TSS24.BF2"), TRADITIONAL_CHINESE("TST24.BF2"), KOREAN("K");

    private final String value;

    private FONTTYPE(String value) { this.value = value; }

    public String getValue()
    {
      return this.value;
    }
  }

  public static enum FOOT
  {
    F2(0), F5(1);

    private final int value;

    private FOOT(int value) { this.value = value; }

    public int getValue()
    {
      return this.value;
    }
  }

  public static enum MIRROR
  {
    NORMAL(0), MIRROR(1);

    private final int value;

    private MIRROR(int value) { this.value = value; }

    public int getValue()
    {
      return this.value;
    }
  }

  public static enum READABEL
  {
    DISABLE(0), EANBEL(1);

    private final int value;

    private READABEL(int value) { this.value = value; }

    public int getValue()
    {
      return this.value;
    }
  }

  public static enum RESPONSE_MODE
  {
    ON("ON"), OFF("OFF"), BATCH("BATCH");

    private final String value;

    private RESPONSE_MODE(String value) { this.value = value; }

    public String getValue()
    {
      return this.value;
    }
  }

  public static enum ROTATION
  {
    ROTATION_0(0), ROTATION_90(90), ROTATION_180(180), ROTATION_270(270);

    private final int value;

    private ROTATION(int value) { this.value = value; }

    public int getValue()
    {
      return this.value;
    }
  }

  public static enum SPEED
  {
    SPEED1DIV5(1.5F), SPEED2(2.0F), SPEED3(3.0F), SPEED4(4.0F);

    private final float value;

    private SPEED(float value) { this.value = value; }

    public float getValue()
    {
      return this.value;
    }
  }
}