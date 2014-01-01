package net.polydawn.mdm.contrib.importer.mvn.parsers;

import java.io.*;
import java.net.*;
import java.util.*;
import net.polydawn.mdm.contrib.importer.mvn.structs.*;
import net.polydawn.mdm.contrib.importer.mvn.util.*;
import org.w3c.dom.*;

public class MetadataParser {
	public MetadataParser(Curler curler) {
		this.curler = curler;
	}

	private final Curler curler;

	public List<Version> fetch(GroupId groupId, ArtifactId artifactId) throws MalformedURLException, IOException {
		Element xml = Xml.parse(curler.curl(groupId.asBlob() + artifactId.asPath() + "maven-metadata.xml"));

		/*
		 * Example format:
		 *
			<?xml version="1.0" encoding="UTF-8"?>
			<metadata>
			  <groupId>org.springframework.android</groupId>
			  <artifactId>spring-android-core</artifactId>
			  <versioning>
			    <latest>1.0.1.RELEASE</latest>
			    <release>1.0.1.RELEASE</release>
			    <versions>
			      <version>1.0.0.RELEASE</version>
			      <version>1.0.1.RELEASE</version>
			    </versions>
			    <lastUpdated>20121207031623</lastUpdated>
			  </versioning>
			</metadata>
		 *
		 * We could also get maven-metadata.xml.md5 or maven-metadata.xml.sha1, but honestly,
		 * I'm fine assuming the byte stream wasn't corrupted if the xml parsed, the tcp crc passed,
		 * and so forth.  And it's not like there's a security property gained by verifying a
		 * checksum that I just downloaded over http.
		 */

		NodeList nl = xml.getElementsByTagName("version");
		List<Version> answer = new ArrayList<Version>(nl.getLength());
		// it's cool how NodeList doesn't implement collection or even iterable
		for (int i = 0; i < nl.getLength(); i++) {
			answer.add(new Version(nl.item(i).getNodeValue()));
		}
		return answer;
	}
}
