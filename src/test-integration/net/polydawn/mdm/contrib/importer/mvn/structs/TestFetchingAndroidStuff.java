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
	private static final Version version = new Version("1.0.1.RELEASE");

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

	@Test
	public void TestListingContents() throws MalformedURLException, IOException {
		List<BlobId> files = new DirParser(curler).fetch(groupId, artifactId, version);
		Iterator<BlobId> itr = files.iterator();
		assertEquals("spring-android-core-1.0.1.RELEASE-javadoc.jar"      , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.1.RELEASE-javadoc.jar.asc"  , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.1.RELEASE-javadoc.jar.md5"  , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.1.RELEASE-javadoc.jar.sha1" , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.1.RELEASE-sources.jar"      , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.1.RELEASE-sources.jar.asc"  , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.1.RELEASE-sources.jar.md5"  , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.1.RELEASE-sources.jar.sha1" , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.1.RELEASE.jar"              , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.1.RELEASE.jar.asc"          , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.1.RELEASE.jar.md5"          , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.1.RELEASE.jar.sha1"         , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.1.RELEASE.pom"              , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.1.RELEASE.pom.asc"          , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.1.RELEASE.pom.md5"          , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.1.RELEASE.pom.sha1"         , itr.next().asBlob());
		assertFalse(itr.hasNext());
	}

	/**
	 * Same as {@link #TestListingContents()}, but with some urls being long enough
	 * that their text is truncated and ends in "..>".
	 *
	 * Hilariously, notice how this is actually the version of the test project that
	 * comes BEFORE the one examined in {@link #TestListingContents()}. Evidentally at
	 * some point the maven plugin that publishes .asc files stopped bothering to
	 * generate checksums on them.
	 */
	@Test
	public void TestListingLongerContents() throws MalformedURLException, IOException {
		List<BlobId> files = new DirParser(curler).fetch(groupId, artifactId, new Version("1.0.0.RELEASE"));
		Iterator<BlobId> itr = files.iterator();
		assertEquals("spring-android-core-1.0.0.RELEASE-javadoc.jar"          , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE-javadoc.jar.asc"      , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE-javadoc.jar.asc.md5"  , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE-javadoc.jar.asc.sha1" , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE-javadoc.jar.md5"      , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE-javadoc.jar.sha1"     , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE-sources.jar"          , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE-sources.jar.asc"      , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE-sources.jar.asc.md5"  , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE-sources.jar.asc.sha1" , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE-sources.jar.md5"      , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE-sources.jar.sha1"     , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE.jar"                  , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE.jar.asc"              , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE.jar.asc.md5"          , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE.jar.asc.sha1"         , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE.jar.md5"              , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE.jar.sha1"             , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE.pom"                  , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE.pom.asc"              , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE.pom.asc.md5"          , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE.pom.asc.sha1"         , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE.pom.md5"              , itr.next().asBlob());
		assertEquals("spring-android-core-1.0.0.RELEASE.pom.sha1"             , itr.next().asBlob());
		assertFalse(itr.hasNext());
	}
}
