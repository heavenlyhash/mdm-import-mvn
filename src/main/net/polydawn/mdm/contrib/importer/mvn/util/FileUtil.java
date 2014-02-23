package net.polydawn.mdm.contrib.importer.mvn.util;

import java.io.*;
import java.util.*;

public class FileUtil {
	public static void save(byte[] bytes, File file) throws IOException {
		OutputStream os = null;
		try {
			os = new BufferedOutputStream(new FileOutputStream(file));
			os.write(bytes);
		} finally {
			if (os != null) os.close();
		}
	}

	public static File createTmpDir(File parent) {
		while (true) {
			File f = new File(parent, ".tmp."+UUID.randomUUID().toString());
			if (f.mkdirs()) {
				return f;
			}
		}
	}
}
