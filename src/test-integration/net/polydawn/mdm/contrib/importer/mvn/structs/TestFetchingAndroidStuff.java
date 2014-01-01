package net.polydawn.mdm.contrib.importer.mvn.structs;

import static org.junit.Assert.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.junit.*;
import net.polydawn.mdm.contrib.importer.mvn.parsers.*;

/**
 * Make all sorts of absurd assumptions about spring repos never changing and the internet
 * being a thing.
 */
public class TestFetchingAndroidStuff {
	private static final String CURL_PREFIX = "http://repo1.maven.org/maven2/";
	private static final Curler curler = new Curler(CURL_PREFIX);
	private static final GroupId groupId = new GroupId("org.springframework.android");
	private static final ArtifactId artifactId = new ArtifactId("spring-android-core");

	@Test
	public void TestFetchingVersions() throws MalformedURLException, IOException {
		List<Version> versions = fetchVersions();

		// i'm not going to be crazy enough to say they'll never release again,
		// but imma say they're never going to unrelease these two,
		// and they'd better damn well be in order.

		assertEquals("1.0.0.RELEASE", versions.get(0).asBlob());
		assertEquals("1.0.1.RELEASE", versions.get(1).asBlob());
	}

	private List<Version> fetchVersions() throws MalformedURLException, IOException {
		return new MetadataParser(curler).fetch(groupId, artifactId);
	}
}
