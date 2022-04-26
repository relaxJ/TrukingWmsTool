/*     */ package com.truking.wms.tool.utils.com.printer.utils;
/*     */ 
/*     */ import android.util.Log;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.LineNumberReader;
/*     */ import java.util.Iterator;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class SerialPortFinder
/*     */ {
/*  13 */   private static final String TAG = SerialPortFinder.class.getSimpleName();
/*  14 */   private Vector<Driver> mDrivers = null;
/*     */ 
/*     */   Vector<Driver> getDrivers()
/*     */     throws IOException
/*     */   {
/*  51 */     if (this.mDrivers == null) {
/*  52 */       this.mDrivers = new Vector();
/*  53 */       LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"));
/*     */       String l;
/*  55 */       while ((l = r.readLine()) != null)
/*     */       {
/*  58 */         String drivername = l.substring(0, 21).trim();
/*  59 */         String[] w = l.split(" +");
/*  60 */         if ((w.length >= 5) && (w[(w.length - 1)].equals("serial"))) {
/*  61 */           Log.d(TAG, "???? Found new driver " + drivername + " on " + w[(w.length - 4)]);
/*  62 */           this.mDrivers.add(new Driver(drivername, w[(w.length - 4)]));
/*     */         }
/*     */       }
/*  65 */       r.close();
/*     */     }
/*  67 */     return this.mDrivers;
/*     */   }
/*     */ 
/*     */   public String[] getAllDevices() {
/*  71 */     Vector devices = new Vector();
/*     */     try
/*     */     {
/*  75 */       Iterator itdriv = getDrivers().iterator();
/*     */       Iterator localIterator1;
/*  76 */       for (; itdriv.hasNext(); 
/*  82 */         localIterator1.hasNext())
/*     */       {
/*  77 */         Driver driver = (Driver)itdriv.next();
/*  78 */         Vector files = driver.getDevices();
/*  79 */         if (files == null) {
/*  80 */           return null;
/*     */         }
/*  82 */         localIterator1 = files.iterator(); continue;
//File file = (File)localIterator1.next();
/*  83 */         //String device = file.getName();
/*  84 */         //String value = String.format("%s (%s)", new Object[] { device, driver.getName() });
/*  85 */        // devices.add(value);
/*     */       }
/*     */     }
/*     */     catch (IOException e) {
/*  89 */       e.printStackTrace();
/*     */     }
/*  91 */     return (String[])devices.toArray(new String[devices.size()]);
/*     */   }
/*     */ 
/*     */   public String[] getAllDevicesPath() {
/*  95 */     Vector devices = new Vector();
/*     */     try
/*     */     {
/*  99 */       Iterator itdriv = getDrivers().iterator();
/*     */       Iterator localIterator1;
/* 100 */       for (; itdriv.hasNext(); 
/* 106 */         localIterator1.hasNext())
/*     */       {
/* 101 */         Driver driver = (Driver)itdriv.next();
/* 102 */         Vector files = driver.getDevices();
/* 103 */         if (files == null) {
/* 104 */           return null;
/*     */         }
/* 106 */         localIterator1 = files.iterator(); continue;
//File file = (File)localIterator1.next();
/* 107 */         //String device = file.getAbsolutePath();
/* 108 */        // devices.add(device);
/*     */       }
/*     */     }
/*     */     catch (IOException e) {
/* 112 */       e.printStackTrace();
/*     */     }
/* 114 */     return (String[])devices.toArray(new String[devices.size()]);
/*     */   }
/*     */ 
/*     */   public class Driver
/*     */   {
/*     */     private String mDriverName;
/*     */     private String mDeviceRoot;
/*  19 */     Vector<File> mDevices = null;
/*     */ 
/*     */     public Driver(String name, String root) {
/*  22 */       this.mDriverName = name;
/*  23 */       this.mDeviceRoot = root;
/*     */     }
/*     */ 
/*     */     public Vector<File> getDevices() {
/*  27 */       if (this.mDevices == null) {
/*  28 */         this.mDevices = new Vector();
/*  29 */         File dev = new File("/dev");
/*  30 */         File[] files = dev.listFiles();
/*  31 */         if (files == null) {
/*  32 */           return null;
/*     */         }
/*     */ 
/*  35 */         for (int i = 0; i < files.length; i++) {
/*  36 */           if (files[i].getAbsolutePath().startsWith(this.mDeviceRoot)) {
/*  37 */             Log.d(SerialPortFinder.TAG, "???? Found new device: " + files[i]);
/*  38 */             this.mDevices.add(files[i]);
/*     */           }
/*     */         }
/*     */       }
/*  42 */       return this.mDevices;
/*     */     }
/*     */ 
/*     */     public String getName() {
/*  46 */       return this.mDriverName;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\WORKSPACE\android_studio_workspace\PrinterDemo-master\PrinterDemo-master\app\libs\printersdkv1.jar
 * Qualified Name:     com.printer.utils.SerialPortFinder
 * JD-Core Version:    0.6.2
 */