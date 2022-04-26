package com.truking.wms.tool.utils.com.printer.command;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EscCommand
{
  private static final String DEBUG_TAG = "EscCommand";
  Vector<Byte> Command = null;

  public EscCommand()
  {
    this.Command = new Vector(4096, 1024);
  }

  private void addArrayToCommand(byte[] array)
  {
    for (int i = 0; i < array.length; i++)
      this.Command.add(Byte.valueOf(array[i]));
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

  private void addStrToCommand(String str, String charset)
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

  private void addStrToCommandUTF8Encoding(String str, int length)
  {
    byte[] bs = null;
    if (!str.equals("")) {
      try {
        bs = str.getBytes("UTF-8");
      }
      catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      Log.d("EscCommand", "bs.length" + bs.length);
      if (length > bs.length)
        length = bs.length;
      Log.d("EscCommand", "length" + length);
      for (int i = 0; i < length; i++)
        this.Command.add(Byte.valueOf(bs[i]));
    }
  }

  private void addStrToCommand(String str, int length)
  {
    byte[] bs = null;
    if (!str.equals("")) {
      try {
        bs = str.getBytes("GB2312");
      }
      catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      Log.d("EscCommand", "bs.length" + bs.length);
      if (length > bs.length)
        length = bs.length;
      Log.d("EscCommand", "length" + length);
      for (int i = 0; i < length; i++)
        this.Command.add(Byte.valueOf(bs[i]));
    }
  }

  public void addHorTab()
  {
    byte[] command = { 9 };
    addArrayToCommand(command);
  }

  public void addText(String text)
  {
    addStrToCommand(text);
  }

  public void addText(String text, String charsetName)
  {
    addStrToCommand(text, charsetName);
  }

  public void addArabicText(String text)
  {
    text = GpUtils.reverseLetterAndNumber(text);
    text = GpUtils.splitArabic(text);
    String[] fooInput = text.split("\\n");
    for (String in : fooInput)
    {
      byte[] output = GpUtils.string2Cp864(in);
      for (int i = 0; i < output.length; i++)
        if (output[i] == -16)
          addArrayToCommand(new byte[] { 27, 116, 29, -124, 27, 116, 22 });
        else if (output[i] == 127)
          this.Command.add(Byte.valueOf((byte)-41));
        else
          this.Command.add(Byte.valueOf(output[i]));
    }
  }

  public void addPrintAndLineFeed()
  {
    byte[] command = { 10 };
    addArrayToCommand(command);
  }

  public void RealtimeStatusTransmission(STATUS status)
  {
    byte[] command = { 16, 4 };
    command[2] = status.getValue();
    addArrayToCommand(command);
  }

  public void addGeneratePluseAtRealtime(LabelCommand.FOOT foot, byte t)
  {
    byte[] command = { 16, 20, 1 };
    command[3] = ((byte)foot.getValue());
    if (t > 8)
      t = 8;
    command[4] = t;
    addArrayToCommand(command);
  }

  public void addSound(byte n, byte t)
  {
    byte[] command = { 27, 66 };
    if (n < 0)
      n = 1;
    else if (n > 9) {
      n = 9;
    }
    if (t < 0)
      t = 1;
    else if (t > 9) {
      t = 9;
    }

    command[2] = n;
    command[3] = t;
    addArrayToCommand(command);
  }

  public void addSetRightSideCharacterSpacing(byte n)
  {
    byte[] command = { 27, 32 };
    command[2] = n;
    addArrayToCommand(command);
  }

  public Vector<Byte> getCommand()
  {
    return this.Command;
  }

  public void addSelectPrintModes(FONT font, ENABLE emphasized, ENABLE doubleheight, ENABLE doublewidth, ENABLE underline)
  {
    byte temp = 0;
    if (font == FONT.FONTB) {
      temp = 1;
    }
    if (emphasized == ENABLE.ON) {
      temp = (byte)(temp | 0x8);
    }
    if (doubleheight == ENABLE.ON) {
      temp = (byte)(temp | 0x10);
    }
    if (doublewidth == ENABLE.ON) {
      temp = (byte)(temp | 0x20);
    }
    if (underline == ENABLE.ON) {
      temp = (byte)(temp | 0x80);
    }
    byte[] command = { 27, 33 };
    command[2] = temp;
    addArrayToCommand(command);
  }

  public void addSetAbsolutePrintPosition(short n)
  {
    byte[] command = { 27, 36 };
    byte nl = (byte)(n % 256);
    byte nh = (byte)(n / 256);
    command[2] = nl;
    command[3] = nh;
    addArrayToCommand(command);
  }

  public void addSelectOrCancelUserDefineCharacter(ENABLE enable)
  {
    byte[] command = { 27, 37 };
    if (enable == ENABLE.ON)
      command[2] = 1;
    else
      command[2] = 0;
    addArrayToCommand(command);
  }

  public void addTurnUnderlineModeOnOrOff(UNDERLINE_MODE underline)
  {
    byte[] command = { 27, 45 };
    command[2] = underline.getValue();
    addArrayToCommand(command);
  }

  public void addSelectDefualtLineSpacing()
  {
    byte[] command = { 27, 50 };
    addArrayToCommand(command);
  }

  public void addSetLineSpacing(byte n)
  {
    byte[] command = { 27, 51 };
    command[2] = n;
    addArrayToCommand(command);
  }

  public void addCancelUserDefinedCharacters(byte n)
  {
    byte[] command = { 27, 63 };
    if ((n >= 32) && (n <= 126))
      command[2] = n;
    else
      command[2] = 32;
    addArrayToCommand(command);
  }

  public void addInitializePrinter()
  {
    byte[] command = { 27, 64 };
    addArrayToCommand(command);
  }

  public void addTurnEmphasizedModeOnOrOff(ENABLE enabel)
  {
    byte[] command = { 27, 69 };
    command[2] = enabel.getValue();
    addArrayToCommand(command);
  }

  public void addTurnDoubleStrikeOnOrOff(ENABLE enabel)
  {
    byte[] command = { 27, 71 };
    command[2] = enabel.getValue();
    addArrayToCommand(command);
  }

  public void addPrintAndFeedPaper(byte n)
  {
    byte[] command = { 27, 74 };
    command[2] = n;
    addArrayToCommand(command);
  }

  public void addSelectCharacterFont(FONT font)
  {
    byte[] command = { 27, 77 };
    command[2] = font.getValue();
    addArrayToCommand(command);
  }

  public void addSelectInternationalCharacterSet(CHARACTER_SET set)
  {
    byte[] command = { 27, 82 };
    command[2] = set.getValue();
    addArrayToCommand(command);
  }

  public void addTurn90ClockWiseRotatin(ENABLE enabel)
  {
    byte[] command = { 27, 86 };
    command[2] = enabel.getValue();
    addArrayToCommand(command);
  }

  public void addSetRelativePrintPositon(short n)
  {
    byte[] command = { 27, 92 };
    byte nl = (byte)(n % 256);
    byte nh = (byte)(n / 256);
    command[2] = nl;
    command[3] = nh;
    addArrayToCommand(command);
  }

  public void addSelectJustification(JUSTIFICATION just)
  {
    byte[] command = { 27, 97 };
    command[2] = just.getValue();
    addArrayToCommand(command);
  }

  public void addPrintAndFeedLines(byte n)
  {
    byte[] command = { 27, 100 };
    command[2] = n;
    addArrayToCommand(command);
  }

  public void addGeneratePlus(LabelCommand.FOOT foot, byte t1, byte t2)
  {
    byte[] command = { 27, 112 };
    command[2] = ((byte)foot.getValue());
    command[3] = t1;
    command[4] = t2;
    addArrayToCommand(command);
  }

  public void addSelectCodePage(CODEPAGE page)
  {
    byte[] command = { 27, 116 };
    command[2] = page.getValue();
    addArrayToCommand(command);
  }

  public void addTurnUpsideDownModeOnOrOff(ENABLE enable)
  {
    byte[] command = { 27, 123 };
    command[2] = enable.getValue();
    addArrayToCommand(command);
  }

  public void addSetCharcterSize(WIDTH_ZOOM width, HEIGHT_ZOOM height)
  {
    byte[] command = { 29, 33 };
    byte temp = 0;
    temp = (byte)(temp | width.getValue());
    temp = (byte)(temp | height.getValue());
    command[2] = temp;
    addArrayToCommand(command);
  }

  public void addTurnReverseModeOnOrOff(ENABLE enable)
  {
    byte[] command = { 29, 66 };
    command[2] = enable.getValue();
    addArrayToCommand(command);
  }

  public void addSelectPrintingPositionForHRICharacters(HRI_POSITION position)
  {
    byte[] command = { 29, 72 };
    command[2] = position.getValue();
    addArrayToCommand(command);
  }

  public void addSetLeftMargin(short n)
  {
    byte[] command = { 29, 76 };
    byte nl = (byte)(n % 256);
    byte nh = (byte)(n / 256);
    command[2] = nl;
    command[3] = nh;
    addArrayToCommand(command);
  }

  public void addSetHorAndVerMotionUnits(byte x, byte y)
  {
    byte[] command = { 29, 80 };
    command[2] = x;
    command[3] = y;
    addArrayToCommand(command);
  }

  public void addCutAndFeedPaper(byte length)
  {
    byte[] command = { 29, 86, 66 };
    command[3] = length;
    addArrayToCommand(command);
  }

  public void addCutPaper()
  {
    byte[] command = { 29, 86, 1 };
    addArrayToCommand(command);
  }

  public void addSetPrintingAreaWidth(short width)
  {
    byte nl = (byte)(width % 256);
    byte nh = (byte)(width / 256);
    byte[] command = { 29, 87 };
    command[2] = nl;
    command[3] = nh;
    addArrayToCommand(command);
  }

  public void addSetAutoSatusBack(ENABLE enable)
  {
    byte[] command = { 29, 97 };
    if (enable == ENABLE.OFF)
      command[2] = 0;
    else
      command[2] = -1;
    addArrayToCommand(command);
  }

  public void addSetFontForHRICharacter(FONT font)
  {
    byte[] command = { 29, 102 };
    command[2] = font.getValue();
    addArrayToCommand(command);
  }

  public void addSetBarcodeHeight(byte height)
  {
    byte[] command = { 29, 104 };
    command[2] = height;
    addArrayToCommand(command);
  }

  public void addSetBarcodeWidth(byte width)
  {
    byte[] command = { 29, 119 };
    if (width > 6)
      width = 6;
    if (width < 2)
      width = 1;
    command[2] = width;
    addArrayToCommand(command);
  }

  public void addSetKanjiFontMode(ENABLE DoubleWidth, ENABLE DoubleHeight, ENABLE Underline)
  {
    byte[] command = { 28, 33 };
    byte temp = 0;
    if (DoubleWidth == ENABLE.ON)
      temp = (byte)(temp | 0x4);
    if (DoubleHeight == ENABLE.ON)
      temp = (byte)(temp | 0x8);
    if (Underline == ENABLE.ON)
      temp = (byte)(temp | 0x80);
    command[2] = temp;
    addArrayToCommand(command);
  }

  public void addSelectKanjiMode()
  {
    byte[] command = { 28, 38 };
    addArrayToCommand(command);
  }

  public void addSetKanjiUnderLine(UNDERLINE_MODE underline)
  {
    byte[] command = { 28, 45 };
    command[3] = underline.getValue();
    addArrayToCommand(command);
  }

  public void addCancelKanjiMode()
  {
    byte[] command = { 28, 46 };
    addArrayToCommand(command);
  }

  public void addSetKanjiLefttandRightSpace(byte left, byte right)
  {
    byte[] command = { 28, 83 };
    command[2] = left;
    command[3] = right;
    addArrayToCommand(command);
  }

  public void addSetQuadrupleModeForKanji(ENABLE enable)
  {
    byte[] command = { 28, 87 };
    command[2] = enable.getValue();
    addArrayToCommand(command);
  }

  public void addRastBitImage(Bitmap bitmap, int nWidth, int nMode)
  {
    if (bitmap != null) {
      int width = (nWidth + 7) / 8 * 8;
      int height = bitmap.getHeight() * width / bitmap.getWidth();
      Bitmap grayBitmap = GpUtils.toGrayscale(bitmap);
      Bitmap rszBitmap = GpUtils.resizeImage(grayBitmap, width, height);
      byte[] src = GpUtils.bitmapToBWPix(rszBitmap);
      byte[] command = new byte[8];
      height = src.length / width;
      command[0] = 29;
      command[1] = 118;
      command[2] = 48;
      command[3] = ((byte)(nMode & 0x1));
      command[4] = ((byte)(width / 8 % 256));
      command[5] = ((byte)(width / 8 / 256));
      command[6] = ((byte)(height % 256));
      command[7] = ((byte)(height / 256));
      addArrayToCommand(command);
      byte[] codecontent = GpUtils.pixToEscRastBitImageCmd(src);
      for (int k = 0; k < codecontent.length; k++)
        this.Command.add(Byte.valueOf(codecontent[k]));
    }
    else {
      Log.d("BMP", "bmp.  null ");
    }
  }

  public void addOriginRastBitImage(Bitmap bitmap, int nWidth, int nMode) {
    if (bitmap != null) {
      int width = (nWidth + 7) / 8 * 8;
      int height = bitmap.getHeight() * width / bitmap.getWidth();
      Bitmap rszBitmap = GpUtils.resizeImage(bitmap, width, height);
      byte[] data = GpUtils.printEscDraw(rszBitmap);
      addArrayToCommand(data);
    } else {
      Log.d("BMP", "bmp.  null ");
    }
  }

  public void addRastBitImageWithMethod(Bitmap bitmap, int nWidth, int nMode, int method)
  {
    if (bitmap != null) {
      int width = (nWidth + 7) / 8 * 8;
      int height = bitmap.getHeight() * width / bitmap.getWidth();
      Bitmap resizeImage = GpUtils.resizeImage(bitmap, width, height);
      Bitmap rszBitmap = GpUtils.filter(resizeImage, resizeImage.getWidth(), resizeImage.getHeight());

      byte[] src = GpUtils.bitmapToBWPix(rszBitmap);
      byte[] command = new byte[8];
      height = src.length / width;
      command[0] = 29;
      command[1] = 118;
      command[2] = 48;
      command[3] = ((byte)(nMode & 0x1));
      command[4] = ((byte)(width / 8 % 256));
      command[5] = ((byte)(width / 8 / 256));
      command[6] = ((byte)(height % 256));
      command[7] = ((byte)(height / 256));
      addArrayToCommand(command);
      byte[] codecontent = GpUtils.pixToEscRastBitImageCmd(src);
      for (int k = 0; k < codecontent.length; k++)
        this.Command.add(Byte.valueOf(codecontent[k]));
    }
    else {
      Log.d("BMP", "bmp.  null ");
    }
  }

  public void addDownloadNvBitImage(Bitmap[] bitmap)
  {
    if (bitmap != null) {
      Log.d("BMP", "bitmap.length " + bitmap.length);
      int n = bitmap.length;
      if (n > 0) {
        byte[] command = new byte[3];
        command[0] = 28;
        command[1] = 113;
        command[2] = ((byte)n);
        addArrayToCommand(command);
        for (int i = 0; i < n; i++) {
          int height = (bitmap[i].getHeight() + 7) / 8 * 8;
          int width = bitmap[i].getWidth() * height / bitmap[i].getHeight();
          Bitmap grayBitmap = GpUtils.toGrayscale(bitmap[i]);
          Bitmap rszBitmap = GpUtils.resizeImage(grayBitmap, width, height);
          byte[] src = GpUtils.bitmapToBWPix(rszBitmap);
          height = src.length / width;
          Log.d("BMP", "bmp  Width " + width);
          Log.d("BMP", "bmp  height " + height);
          byte[] codecontent = GpUtils.pixToEscNvBitImageCmd(src, width, height);
          for (int k = 0; k < codecontent.length; k++)
            this.Command.add(Byte.valueOf(codecontent[k]));
        }
      }
    }
    else {
      Log.d("BMP", "bmp.  null ");
      return;
    }
  }

  public void addPrintNvBitmap(byte n, byte mode) {
    byte[] command = { 28, 112 };
    command[2] = n;
    command[3] = mode;
    addArrayToCommand(command);
  }

  public void addUPCA(String content)
  {
    byte[] command = new byte[4];
    command[0] = 29;
    command[1] = 107;
    command[2] = 65;
    command[3] = 11;
    if (content.length() < command[3])
      return;
    addArrayToCommand(command);
    addStrToCommand(content, 11);
  }

  public void addUPCE(String content)
  {
    byte[] command = new byte[4];
    command[0] = 29;
    command[1] = 107;
    command[2] = 66;
    command[3] = 11;
    if (content.length() < command[3])
      return;
    addArrayToCommand(command);
    addStrToCommand(content, command[3]);
  }

  public void addEAN13(String content)
  {
    byte[] command = new byte[4];
    command[0] = 29;
    command[1] = 107;
    command[2] = 67;
    command[3] = 12;
    if (content.length() < command[3])
      return;
    addArrayToCommand(command);
    Log.d("EscCommand", "content.length" + content.length());
    addStrToCommand(content, command[3]);
  }

  public void addEAN8(String content)
  {
    byte[] command = new byte[4];
    command[0] = 29;
    command[1] = 107;
    command[2] = 68;
    command[3] = 7;
    if (content.length() < command[3])
      return;
    addArrayToCommand(command);
    addStrToCommand(content, command[3]);
  }

  @SuppressLint({"DefaultLocale"})
  public void addCODE39(String content)
  {
    byte[] command = new byte[4];
    command[0] = 29;
    command[1] = 107;
    command[2] = 69;
    command[3] = ((byte)content.length());
    content = content.toUpperCase();
    addArrayToCommand(command);
    addStrToCommand(content, command[3]);
  }

  public void addITF(String content)
  {
    byte[] command = new byte[4];
    command[0] = 29;
    command[1] = 107;
    command[2] = 70;
    command[3] = ((byte)content.length());
    addArrayToCommand(command);
    addStrToCommand(content, command[3]);
  }

  public void addCODABAR(String content)
  {
    byte[] command = new byte[4];
    command[0] = 29;
    command[1] = 107;
    command[2] = 71;
    command[3] = ((byte)content.length());
    addArrayToCommand(command);
    addStrToCommand(content, command[3]);
  }

  public void addCODE93(String content)
  {
    byte[] command = new byte[4];
    command[0] = 29;
    command[1] = 107;
    command[2] = 72;
    command[3] = ((byte)content.length());
    addArrayToCommand(command);
    addStrToCommand(content, command[3]);
  }

  public void addCODE128(String content)
  {
    byte[] command = new byte[4];
    command[0] = 29;
    command[1] = 107;
    command[2] = 73;
    command[3] = ((byte)content.length());
    addArrayToCommand(command);
    addStrToCommand(content, command[3]);
  }

  public String genCodeC(String content) {
    List bytes = new ArrayList(20);
    int len = content.length();
    bytes.add(Byte.valueOf((byte)123));
    bytes.add(Byte.valueOf((byte)67));
    for (int i = 0; i < len; i += 2) {
      int ken = (content.charAt(i) - '0') * 10;
      int bits = content.charAt(i + 1) - '0';
      int current = ken + bits;
      bytes.add(Byte.valueOf((byte)current));
    }
    byte[] bb = new byte[bytes.size()];
    for (int i = 0; i < bb.length; i++) {
      bb[i] = ((Byte)bytes.get(i)).byteValue();
    }

    return new String(bb, 0, bb.length);
  }

  public String genCodeB(String content) {
    return String.format("{B%s", new Object[] { content });
  }

  public String genCode128(String content) {
    String regex = "([^0-9])";
    String[] str = content.split(regex);

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(content);

    String splitString = null;
    int strlen = str.length;

    if ((strlen > 0) && 
      (matcher.find())) {
      splitString = matcher.group(0);
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < strlen; i++) {
      String first = str[i];
      int len = first.length();
      int result = len % 2;
      if (result == 0) {
        String codeC = genCodeC(first);
        sb.append(codeC);
      } else {
        sb.append(genCodeB(String.valueOf(first.charAt(0))));
        sb.append(genCodeC(first.substring(1, first.length())));
      }
      if (splitString != null) {
        sb.append(genCodeB(splitString));
        splitString = null;
      }
    }

    return sb.toString();
  }

  public void addSelectSizeOfModuleForQRCode(byte n)
  {
    byte[] command = { 29, 40, 107, 3, 0, 49, 67, 3 };
    command[7] = n;
    addArrayToCommand(command);
  }

  public void addSelectErrorCorrectionLevelForQRCode(byte n)
  {
    byte[] command = { 29, 40, 107, 3, 0, 49, 69 };
    command[7] = n;
    addArrayToCommand(command);
  }

  public void addStoreQRCodeData(String content)
  {
    byte[] command = { 29, 40, 107, 0, 0, 49, 80, 48 };
    command[3] = ((byte)((content.getBytes().length + 3) % 256));
    command[4] = ((byte)((content.getBytes().length + 3) / 256));
    addArrayToCommand(command);

    byte[] bs = null;
    if (!content.equals("")) {
      try {
        bs = content.getBytes("utf-8");
      }
      catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      for (int i = 0; i < bs.length; i++)
        this.Command.add(Byte.valueOf(bs[i]));
    }
  }

  public void addPrintQRCode()
  {
    byte[] command = { 29, 40, 107, 3, 0, 49, 81, 48 };
    addArrayToCommand(command);
  }

  public void addQueryPrinterStatus()
  {
    byte[] command = { 16, 4, 2 };
    addArrayToCommand(command);
  }

  public void addUserCommand(byte[] command) {
    addArrayToCommand(command);
  }

  public static enum CHARACTER_SET
  {
    USA(0), FRANCE(1), GERMANY(2), UK(3), DENMARK_I(4), SWEDEN(5), ITALY(6), SPAIN_I(7), JAPAN(8), NORWAY(
      9),  DENMARK_II(10), SPAIN_II(11), LATIN_AMERCIA(12), KOREAN(13), SLOVENIA(14), CHINA(15);

    private final int value;

    private CHARACTER_SET(int value) { this.value = value; }

    public byte getValue()
    {
      return (byte)this.value;
    }
  }

  public static enum CODEPAGE
  {
    PC437(0), KATAKANA(1), PC850(2), PC860(3), PC863(4), PC865(5), WEST_EUROPE(6), GREEK(7), HEBREW(8), EAST_EUROPE(
      9),  IRAN(10), WPC1252(16), PC866(17), PC852(18), PC858(19), IRANII(20), LATVIAN(21), ARABIC(22), PT151(
      23),  PC747(24), WPC1257(25), VIETNAM(27), PC864(28), PC1001(29), UYGUR(30), THAI(255);

    private final int value;

    private CODEPAGE(int value) { this.value = value; }

    public byte getValue()
    {
      return (byte)this.value;
    }
  }

  public static enum ENABLE
  {
    OFF(0), ON(1);

    private final int value;

    private ENABLE(int value) { this.value = value; }

    public byte getValue()
    {
      return (byte)this.value;
    }
  }

  public static enum FONT
  {
    FONTA(0), FONTB(1);

    private final int value;

    private FONT(int value) { this.value = value; }

    public byte getValue()
    {
      return (byte)this.value;
    }
  }

  public static enum HEIGHT_ZOOM
  {
    MUL_1(0), MUL_2(1), MUL_3(2), MUL_4(3), MUL_5(4), MUL_6(5), MUL_7(6), MUL_8(7);

    private final int value;

    private HEIGHT_ZOOM(int value) { this.value = value; }

    public byte getValue()
    {
      return (byte)this.value;
    }
  }

  public static enum HRI_POSITION {
    NO_PRINT(0), ABOVE(1), BELOW(2), ABOVE_AND_BELOW(3);

    private final int value;

    private HRI_POSITION(int value) { this.value = value; }

    public byte getValue()
    {
      return (byte)this.value;
    }
  }

  public static enum JUSTIFICATION
  {
    LEFT(0), CENTER(1), RIGHT(2);

    private final int value;

    private JUSTIFICATION(int value) { this.value = value; }

    public byte getValue()
    {
      return (byte)this.value;
    }
  }

  public static enum STATUS
  {
    PRINTER_STATUS(1), PRINTER_OFFLINE(2), PRINTER_ERROR(3), PRINTER_PAPER(4);

    private final int value;

    private STATUS(int value) { this.value = value; }

    public byte getValue()
    {
      return (byte)this.value;
    }
  }

  public static enum UNDERLINE_MODE
  {
    OFF(0), UNDERLINE_1DOT(1), UNDERLINE_2DOT(2);

    private final int value;

    private UNDERLINE_MODE(int value) { this.value = value; }

    public byte getValue()
    {
      return (byte)this.value;
    }
  }

  public static enum WIDTH_ZOOM
  {
    MUL_1(0), MUL_2(16), MUL_3(32), MUL_4(48), MUL_5(64), MUL_6(80), MUL_7(96), MUL_8(112);

    private final int value;

    private WIDTH_ZOOM(int value) { this.value = value; }

    public byte getValue()
    {
      return (byte)this.value;
    }
  }
}