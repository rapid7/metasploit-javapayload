package com.metasploit.meterpreter.android.common;

import java.io.File;

import com.metasploit.meterpreter.CommandManager;
import com.metasploit.meterpreter.ExtensionLoader;

/**
 * Loader class to register all the android commands.
 * 
 * @author mihi
 */
public class Loader implements ExtensionLoader {

	public static File cwd;
	
	public static File expand(String path) {
		File result = new File(path);
		if (!result.isAbsolute())
			result = new File(cwd, path);
		return result;
	}

	public void load(CommandManager mgr) throws Exception {
		cwd = new File(".").getCanonicalFile();
		//mgr.registerCommand("channel_create_stdapi_fs_file", channel_create_stdapi_fs_file.class);

	}
}
