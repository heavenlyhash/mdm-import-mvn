package net.polydawn.mdm.contrib.importer.mvn.parsers;

import java.io.*;
import java.net.*;

public class Curler {
	public Curler(String repoUrlPrefix) {
		this.repoUrlPrefix = repoUrlPrefix;
	}

	/**
	 * <p>
	 * These two url prefixes seem to do approximately the same thing when suffixed:
	 * <ul>
	 * <li>{@code http://search.maven.org/remotecontent?filepath=}
	 * <li>{@code http://repo1.maven.org/maven2/}
	 * </ul>
	 * Though of course they're slightly different in bizzare ways, like serving
	 * different content type headers for some paths, one of which confuses firefox
	 * mightily.
	 * </p>
	 *
	 * <p>
	 * There's all sorts of fancypants stuff about a transport-agnostic "wagon"
	 * protocol for maven, but I have no reason to care that much and am not
	 * interested in downloading 450 jars to get at it.
	 * </p>
	 */
	private final String repoUrlPrefix;

	/**
	 * <p>
	 * Get some bytes from a maven repo. Nothing fancy: blocks, and throws it all at
	 * you in one big in-memory array.
	 * </p>
	 *
	 * <p>
	 * An example route suffix is
	 * {@code org/springframework/android/spring-android-core/maven-metadata.xml}, for
	 * {@code
	 * <groupId>org.springframework.android</groupId>
	 * <artifactId>spring-android-core</artifactId>
	 * }
	 * </p>
	 *
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public byte[] curl(String route) throws MalformedURLException, IOException {
		return readUrlRaw(new URL(repoUrlPrefix + route));
	}

	private static byte[] readUrlRaw(URL request) throws IOException {
		InputStream in = null;
		try {
			URLConnection conn = request.openConnection();
			in = conn.getInputStream();
			checkErrorCode(conn);
			return readRaw(in);
		} finally {
			if (in != null) in.close();
		}
	}

	private static byte[] readRaw(InputStream ins) throws IOException {
		try {
			byte[] buf = new byte[CHUNK_SIZE];
			int k;
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			while ((k = ins.read(buf)) != -1) {
				bs.write(buf, 0, k);
			}
			return bs.toByteArray();
		} finally {
			ins.close();
		}
	}

	private static final int CHUNK_SIZE = 8192;

	private static void checkErrorCode(URLConnection conn) throws IOException {
		if (conn instanceof HttpURLConnection) {
			int code = ((HttpURLConnection) conn).getResponseCode();
			if (code / 100 != 2) throw new IOException("Could not connect to '" + conn.getURL() + "' via HTTP; gave error code " + code + ".");
		}
	}
}
