package net.polydawn.mdm.contrib.importer.mvn.parsers;

import java.io.*;
import java.net.*;
import java.util.*;
import net.polydawn.mdm.contrib.importer.mvn.structs.*;
import net.polydawn.mdm.contrib.importer.mvn.util.*;
import org.w3c.dom.*;

public class DirParser {
	public DirParser(Curler curler) {
		this.curler = curler;
	}

	private final Curler curler;

	public List<BlobId> fetch(GroupId groupId, ArtifactId artifactId, Version version) throws MalformedURLException, IOException {
		Element xml = Xml.parse(curler.curl(groupId.asPath() + artifactId.asPath() + version.asPath()));

		/*
		 * Example format:
		 *
			<html>
			<head><title>Index of /maven2/org/springframework/android/spring-android-core/1.0.1.RELEASE/</title></head>
			<body bgcolor="white">
			<h1>Index of /maven2/org/springframework/android/spring-android-core/1.0.1.RELEASE/</h1><hr><pre><a href="../">../</a>
			<a href="spring-android-core-1.0.1.RELEASE-javadoc.jar">spring-android-core-1.0.1.RELEASE-javadoc.jar</a>      07-Dec-2012 03:10              241838
			<a href="spring-android-core-1.0.1.RELEASE-javadoc.jar.asc">spring-android-core-1.0.1.RELEASE-javadoc.jar.asc</a>  07-Dec-2012 03:10                 183
			<a href="spring-android-core-1.0.1.RELEASE-javadoc.jar.md5">spring-android-core-1.0.1.RELEASE-javadoc.jar.md5</a>  07-Dec-2012 03:10                  32
			<a href="spring-android-core-1.0.1.RELEASE-javadoc.jar.sha1">spring-android-core-1.0.1.RELEASE-javadoc.jar.sha1</a> 07-Dec-2012 03:10                  40
			<a href="spring-android-core-1.0.1.RELEASE-sources.jar">spring-android-core-1.0.1.RELEASE-sources.jar</a>      07-Dec-2012 03:10              107484
			<a href="spring-android-core-1.0.1.RELEASE-sources.jar.asc">spring-android-core-1.0.1.RELEASE-sources.jar.asc</a>  07-Dec-2012 03:10                 183
			<a href="spring-android-core-1.0.1.RELEASE-sources.jar.md5">spring-android-core-1.0.1.RELEASE-sources.jar.md5</a>  07-Dec-2012 03:10                  32
			<a href="spring-android-core-1.0.1.RELEASE-sources.jar.sha1">spring-android-core-1.0.1.RELEASE-sources.jar.sha1</a> 07-Dec-2012 03:10                  40
			<a href="spring-android-core-1.0.1.RELEASE.jar">spring-android-core-1.0.1.RELEASE.jar</a>              07-Dec-2012 03:10              113156
			<a href="spring-android-core-1.0.1.RELEASE.jar.asc">spring-android-core-1.0.1.RELEASE.jar.asc</a>          07-Dec-2012 03:10                 183
			<a href="spring-android-core-1.0.1.RELEASE.jar.md5">spring-android-core-1.0.1.RELEASE.jar.md5</a>          07-Dec-2012 03:10                  32
			<a href="spring-android-core-1.0.1.RELEASE.jar.sha1">spring-android-core-1.0.1.RELEASE.jar.sha1</a>         07-Dec-2012 03:10                  40
			<a href="spring-android-core-1.0.1.RELEASE.pom">spring-android-core-1.0.1.RELEASE.pom</a>              07-Dec-2012 03:10                1560
			<a href="spring-android-core-1.0.1.RELEASE.pom.asc">spring-android-core-1.0.1.RELEASE.pom.asc</a>          07-Dec-2012 03:10                 183
			<a href="spring-android-core-1.0.1.RELEASE.pom.md5">spring-android-core-1.0.1.RELEASE.pom.md5</a>          07-Dec-2012 03:10                  32
			<a href="spring-android-core-1.0.1.RELEASE.pom.sha1">spring-android-core-1.0.1.RELEASE.pom.sha1</a>         07-Dec-2012 03:10                  40
			</pre><hr></body>
			</html>
		 *
		 * Parsing this is obviously insane,
		 * but see comments in the middle of {@link PomParser} for why.
		 */

		NodeList nl = xml.getElementsByTagName("a"); // god have mercy
		List<BlobId> answer = new ArrayList<BlobId>(nl.getLength());
		// start from 1, because zero is "../" and there's no other discriminators ffs
		for (int i = 1; i < nl.getLength(); i++) {
			answer.add(new BlobId(nl.item(i).getTextContent()));
		}
		return answer;
	}
}

