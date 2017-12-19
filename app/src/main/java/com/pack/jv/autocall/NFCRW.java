package com.pack.jv.autocall;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by João on 12/04/2017.
 */

public class NFCRW {

    public static String NdefRead(Tag tag, String ID) {
        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();


        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    String result = readText(ndefRecord);
                    result = AES.decrypt(ID, result);
                    return result;

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return null;

    }

    public static boolean NdefWrite(String text, Tag tag, String ID) throws IOException, FormatException {
        boolean flag = true;
        try {
            String encryptedData = AES.encrypt(ID, text);
            NdefRecord[] records = {createRecord(text)};
            NdefMessage message = new NdefMessage(records);
            NdefMessage checkMessage = null;
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                ndef.writeNdefMessage(message);
                checkMessage = ndef.getNdefMessage();
                ndef.close();
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    format.connect();
                    format.format(message);
                    checkMessage = ndef.getNdefMessage();
                }
            }
            String checkMessageStr = NdefReadHelper(checkMessage, ID);
            if (!text.equals(checkMessageStr)) flag = false;
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }

        return flag;
    }

    private static String NdefReadHelper(NdefMessage msg, String ID) {
        NdefRecord[] records = msg.getRecords();
        for (NdefRecord ndefRecord : records) {
            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    String result = readText(ndefRecord);
                    result = AES.decrypt(ID, result);
                    return result;

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return null;


    }

    private static String readText(NdefRecord record) throws UnsupportedEncodingException {
        byte[] payload = record.getPayload();

        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        int languageCodeLength = payload[0] & 0063;

        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }

    private static NdefRecord createRecord(String text) throws UnsupportedEncodingException {

        //create the message in according with the standard
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;

        byte[] payload = new byte[1 + langLength + textLength];
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);


        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
        return recordNFC;
    }

    private static byte[] createByteRecord(String text) throws Exception {
        //create the message in according with the standard
        byte[] textBytes = text.getBytes(Charset.forName("UTF-8"));
        int messageLength = textBytes.length + 4;
        int completeBytes = 16 - messageLength % 16;
        byte[] messageLengthBytes = ByteBuffer.allocate(4).putInt(messageLength).array();

        byte[] payload = new byte[messageLength + completeBytes];
        System.arraycopy(messageLengthBytes,0,payload,0,4);
        System.arraycopy(textBytes,0,payload,4,textBytes.length);

        return payload;
    }

    public static boolean nfcWrite(String text, Tag tag, String id) {
        MifareClassic mfc = MifareClassic.get(tag);
        try {
            mfc.connect();
            String encryptedData = AES.encrypt(id, text);
            byte[] message = createByteRecord(encryptedData);
            int messageBlocks = message.length / 16;
            int sectorIndex = 6;
            int blockIndex = mfc.sectorToBlock(sectorIndex);
            int byteIndex = 0;
            boolean sectorFlag = true;
            for(int i = 0; i < messageBlocks; i++){
                if(i%4 == 3){
                    sectorIndex++;
                    blockIndex ++;
                    sectorFlag = true;
                }
                if(sectorFlag){
                    boolean authA = mfc.authenticateSectorWithKeyA(sectorIndex, MifareClassic.KEY_DEFAULT);
                    sectorFlag = false;
                }
                byte[] blockMsg = new byte[16];
                System.arraycopy(message, byteIndex, blockMsg, 0, 16);
                mfc.writeBlock(blockIndex, blockMsg);
                byteIndex += 16;
                blockIndex++;
            }
            mfc.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static String nfcRead(Tag tag, String id){
        String text;
        MifareClassic mfc = MifareClassic.get(tag);
        try {
            mfc.connect();
            boolean sectorFlag = false;
            int sectorIndex = 6;
            int blockIndex = mfc.sectorToBlock(sectorIndex);
            boolean authA = mfc.authenticateSectorWithKeyA(sectorIndex,MifareClassic.KEY_DEFAULT);
            byte[] firstBlock = mfc.readBlock(blockIndex);
            byte[] lengthBlock = new byte[4];
            System.arraycopy(firstBlock,0,lengthBlock,0,4);
            int messageLength = ByteBuffer.wrap(lengthBlock).getInt();
            byte[] message = new byte[messageLength - 4];
            int messageIndex = 0;
            System.arraycopy(firstBlock,4,message,messageIndex,12);
            messageIndex+=12;
            int numBlocks = ((messageLength + 16 - messageLength%16)/16);
            for(int i = 1; i < numBlocks; i++){
                blockIndex++;
                if (i%4 == 3){
                    sectorIndex++;
                    blockIndex++;
                    sectorFlag = true;
                }
                if(sectorFlag){
                    mfc.authenticateSectorWithKeyA(sectorIndex,MifareClassic.KEY_DEFAULT);
                    sectorFlag = false;
                }
                if(i== numBlocks-1){
                    byte[] blockMsg = mfc.readBlock(blockIndex);
                    System.arraycopy(blockMsg,0,message,messageIndex,messageLength%16);
                }
                else{
                    byte[] blockMsg = mfc.readBlock(blockIndex);
                    System.arraycopy(blockMsg,0,message,messageIndex,16);
                    messageIndex+=16;
                }
            }
            text = new String(message, "UTF-8");
            text = AES.decrypt(id, text);
            mfc.close();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return text;
    }


}
