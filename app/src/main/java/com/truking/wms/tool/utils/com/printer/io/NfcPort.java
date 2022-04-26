package com.truking.wms.tool.utils.com.printer.io;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import java.io.IOException;

public class NfcPort
{
  public NfcAdapter NfcAdapter;
  public PendingIntent PendingIntent;
  private Context context;
  private Intent intent;
  public Tag tag;
  public MifareClassic mfc;
  public byte[] KEYFORUM = MifareClassic.KEY_NFC_FORUM;
  public byte[] KEYDEFAULT = MifareClassic.KEY_DEFAULT;

  public NfcPort(Context context) { this.context = context;
    this.NfcAdapter = NfcAdapter.getDefaultAdapter(context);
    this.PendingIntent = PendingIntent.getActivity(context, 0, new Intent(
      context, context.getClass()).addFlags(536870912), 
      0); }

  public void EableNfc()
  {
    if (this.NfcAdapter != null)
      this.NfcAdapter.enableForegroundDispatch((Activity)this.context, this.PendingIntent, null, null);
  }

  public void DisableNfc() {
    if (this.NfcAdapter != null)
      this.NfcAdapter.disableForegroundDispatch((Activity)this.context);
  }

  public boolean checkNFCFunction() {
    if (this.NfcAdapter == null) {
      return false;
    }
    if (!this.NfcAdapter.isEnabled()) {
      return false;
    }
    return true;
  }

  public void PutIntent(Intent intent)
  {
    this.intent = intent;
    this.tag = ((Tag)intent.getParcelableExtra("android.nfc.extra.TAG"));
    this.mfc = MifareClassic.get(this.tag);
  }

  public byte[] GetId() {
    String intentActionStr = this.intent.getAction();
    if (("android.nfc.action.NDEF_DISCOVERED".equals(intentActionStr)) || 
      ("android.nfc.action.TECH_DISCOVERED".equals(intentActionStr)) || 
      ("android.nfc.action.TAG_DISCOVERED".equals(intentActionStr))) {
      return this.tag.getId();
    }
    return null;
  }

  public boolean Connect() {
    if (this.mfc != null) {
      try {
        this.mfc.connect();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      return true;
    }
    return false;
  }

  public String GetType()
  {
    if ((this.mfc != null) && (this.mfc.isConnected())) {
      int type = this.mfc.getType();
      String typeS = "";
      switch (type) {
      case 0:
        typeS = "TYPE_CLASSIC";
        break;
      case 1:
        typeS = "TYPE_PLUS";
        break;
      case 2:
        typeS = "TYPE_PRO";
        break;
      case -1:
        typeS = "TYPE_UNKNOWN";
      }

      return typeS;
    }
    return null;
  }

  public int GetSectorCount() {
    if ((this.mfc != null) && (this.mfc.isConnected())) {
      return this.mfc.getSectorCount();
    }
    return 0;
  }

  public int GetBlockCount() {
    if ((this.mfc != null) && (this.mfc.isConnected())) {
      return this.mfc.getBlockCount();
    }
    return 0;
  }

  public int GetSize() {
    if (this.mfc.isConnected()) {
      return this.mfc.getSize();
    }
    return 0;
  }

  public int GetBlockCountInSector(int Sector) {
    if (this.mfc.isConnected()) {
      return this.mfc.getBlockCountInSector(Sector);
    }
    return 0;
  }

  public int GetSectorFirstBlockNum(int Sector) {
    if (this.mfc.isConnected()) {
      return this.mfc.sectorToBlock(Sector);
    }
    return 0;
  }

  public boolean PairKey(byte[] keyA, byte[] keyB, int Sector) throws IOException {
    boolean auth = false;
    if ((this.mfc == null) || (!this.mfc.isConnected())) {
      return false;
    }
    if ((keyA == null) && (keyB == null))
      return false;
    if (keyA == null) {
      auth = this.mfc.authenticateSectorWithKeyB(Sector, 
        keyB);
    } else if (keyB == null) {
      auth = this.mfc.authenticateSectorWithKeyA(Sector, 
        keyA);
    } else {
      auth = this.mfc.authenticateSectorWithKeyA(Sector, 
        keyA);
      auth = this.mfc.authenticateSectorWithKeyB(Sector, 
        keyB);
    }
    return auth;
  }

  public boolean WriteData(int Block, byte[] data) throws IOException {
    if ((this.mfc == null) || (!this.mfc.isConnected())) {
      return false;
    }
    this.mfc.writeBlock(Block, data);
    return true;
  }

  public byte[] ReadData(int Block) throws IOException {
    if ((this.mfc == null) || (!this.mfc.isConnected())) {
      return null;
    }
    return this.mfc.readBlock(Block);
  }

  public boolean close() throws Exception {
    if ((this.mfc == null) || (!this.mfc.isConnected())) {
      return false;
    }
    this.mfc.close();
    return true;
  }
}