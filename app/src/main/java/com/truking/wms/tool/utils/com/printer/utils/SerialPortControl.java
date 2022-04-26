/*    */ package com.truking.wms.tool.utils.com.printer.utils;
/*    */ 
/*    */ import android.util.Log;
/*    */ import java.io.File;
/*    */ import java.io.FileDescriptor;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class SerialPortControl
/*    */ {
/*    */   private static final String TAG = "SerialPortControl";
/*    */   private FileDescriptor mFd;
/*    */   private FileInputStream mFileInputStream;
/*    */   private FileOutputStream mFileOutputStream;
/*    */ 
/*    */   static
/*    */   {
/* 69 */     System.loadLibrary("serial_port");
/*    */   }
/*    */ 
/*    */   public SerialPortControl(File device, int baudrate, int flags)
/*    */     throws SecurityException, IOException
/*    */   {
/* 29 */     if (device == null) {
/* 30 */       return;
/*    */     }
/* 32 */     if ((!device.canRead()) || (!device.canWrite()))
/*    */     {
/*    */       try
/*    */       {
/* 36 */         Process su = Runtime.getRuntime().exec("/system/bin/su");
/* 37 */         String cmd = "chmod 666 " + device.getAbsolutePath() + "\n" + "exit\n";
/* 38 */         su.getOutputStream().write(cmd.getBytes());
/* 39 */         if ((su.waitFor() != 0) || (!device.canRead()) || (!device.canWrite()))
/* 40 */           throw new SecurityException();
/*    */       }
/*    */       catch (Exception e) {
/* 43 */         Log.e("SerialPortControl", "SerialPortControl.class use shell command error", e);
/*    */       }
/*    */     }
/*    */ 
/* 47 */     this.mFd = open(device.getAbsolutePath(), baudrate, flags);
/* 48 */     if (this.mFd == null) {
/* 49 */       Log.e("SerialPortControl", "native open returns null");
/* 50 */       throw new IOException();
/*    */     }
/* 52 */     this.mFileInputStream = new FileInputStream(this.mFd);
/* 53 */     this.mFileOutputStream = new FileOutputStream(this.mFd);
/*    */   }
/*    */ 
/*    */   public InputStream getInputStream() {
/* 57 */     return this.mFileInputStream;
/*    */   }
/*    */ 
/*    */   public OutputStream getOutputStream() {
/* 61 */     return this.mFileOutputStream;
/*    */   }
/*    */ 
/*    */   private native FileDescriptor open(String paramString, int paramInt1, int paramInt2);
/*    */ 
/*    */   public native void close();
/*    */ }

/* Location:           D:\WORKSPACE\android_studio_workspace\PrinterDemo-master\PrinterDemo-master\app\libs\printersdkv1.jar
 * Qualified Name:     com.printer.utils.SerialPortControl
 * JD-Core Version:    0.6.2
 */