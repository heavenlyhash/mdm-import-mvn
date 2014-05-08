package net.polydawn.mdm.contrib.importer.mvn.parsers;

import java.io.*;
import java.net.*;
import java.util.*;
import net.polydawn.mdm.contrib.importer.mvn.structs.*;
import net.polydawn.mdm.contrib.importer.mvn.util.*;
import org.w3c.dom.*;

/**
 * A discoverer for a version lists, which completely ignores the maven-metadata.xml (for
 * use when that file completely fucking lies, which happens ALL THE TIME since maven
 * central apparently performs NO VALIDATION WHATSOEVER on these and DOESN'T ACTUALLY VARY
 * BEHAVIOR DEPENDING ON THEIR CONTENTS) and does more shitty html parsing.
 *
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 *
 */
public class MetadataDisregardingParser {
	public MetadataDisregardingParser(Curler curler) {
		this.curler = curler;
	}

	private final Curler curler;

	public List<Version> fetch(GroupId groupId, ArtifactId artifactId) throws MalformedURLException, IOException {
		Element xml = Xml.parse(curler.curl(groupId.asPath() + artifactId.asPath()));

		/*
		 * Example of the problem with maven-metadata.xml:
		 *
		 * Content of http://repo1.maven.org/maven2/commons-discovery/commons-discovery/
		 *
			<?xml version="1.0" encoding="UTF-8"?>
			<metadata modelVersion="1.1.0">
			  <groupId>commons-discovery</groupId>
			  <artifactId>commons-discovery</artifactId>
			  <versioning>
			    <latest>1.0-dev</latest>
			    <release>1.0-dev</release>
			    <versions>
			      <version>0.2</version>
			      <version>0.4</version>
			      <version>0.5</version>
			      <version>1.0-dev</version>
			    </versions>
			    <lastUpdated>20130313174531</lastUpdated>
			  </versioning>
			</metadata>
		 *
		 * Now compare this to...
		 *
		 * Content of http://repo1.maven.org/maven2/commons-discovery/commons-discovery/:
		 *
			<html>
			<head><title>Index of /maven2/commons-discovery/commons-discovery/</title></head>
			<body bgcolor="white">
			<h1>Index of /maven2/commons-discovery/commons-discovery/</h1><hr><pre><a href="../">../</a>
			<a href="0.1/">0.1/</a>                                               04-Jan-2007 19:17                   -
			<a href="0.2/">0.2/</a>                                               04-Jan-2007 19:17                   -
			<a href="0.2-dev/">0.2-dev/</a>                                           01-Nov-2005 05:03                   -
			<a href="0.4/">0.4/</a>                                               13-Mar-2013 17:45                   -
			<a href="0.5/">0.5/</a>                                               08-Mar-2013 22:30                   -
			<a href="20030211.213356/">20030211.213356/</a>                                   04-Jan-2007 19:17                   -
			<a href="20040218.194635/">20040218.194635/</a>                                   04-Jan-2007 19:17                   -
			<a href="maven-metadata.xml">maven-metadata.xml</a>                                 13-Mar-2013 17:45                 455
			<a href="maven-metadata.xml.md5">maven-metadata.xml.md5</a>                             13-Mar-2013 17:45                  32
			<a href="maven-metadata.xml.sha1">maven-metadata.xml.sha1</a>                            13-Mar-2013 17:45                  40
			</pre><hr></body>
			</html>
		 *
		 * See how those aren't even close?  The metadata lists a "1.0-dev", which doesn't actually exist,
		 * doesn't list a "0.1" (which does exist), and lists a "2.0-dev" which doesn't actually exist,
		 * and fails to make mention of two other release versions named by date.
		 *
		 * See also other examples of failure:
		 *   - xpp3:
		 *     - http://repo1.maven.org/maven2/xpp3/xpp3_min/maven-metadata.xml
		 *     - http://repo1.maven.org/maven2/xpp3/xpp3_min/
		 *     - http://mvnrepository.com/artifact/xpp3/xpp3_min
		 *     - http://search.maven.org/remotecontent?filepath=xpp3/xpp3_min/maven-metadata.xml
		 *     - http://search.maven.org/#browse|-1915506483
		 *     - http://search.maven.org/#search|gav|1|g%3A%22xpp3%22%20AND%20a%3A%22xpp3_min%22
		 *   - wsld4j:
		 *     - http://central.maven.org/maven2/wsdl4j/wsdl4j/maven-metadata.xml
		 *     - http://central.maven.org/maven2/wsdl4j/wsdl4j/
		 *
		 * Aether -- the Sonatype second-try-to-find-our-own-anuses abandonware (hucked over the wall to
		 * the eclipse foundation to avoid the admission of defeat, but let's call it what it is) --
		 * also trusts the maven-metadata.xml file for an accurate version list, and so is also effectively useless.
		 *
		 * Also I downloaded an animal-sniffer to find that out.  No, really:

			[INFO] --- animal-sniffer-maven-plugin:1.7:check (check-java-1.5-compat) @ aether-demo-snippets ---
			[INFO] Checking unresolved references to org.codehaus.mojo.signature:java15:1.0

		 *
		 * What is this strange hell
		 *
		 * Where are the spike pits and acid pools, I wish to throw myself on their mercy
		 *
		 */

		NodeList nl = xml.getElementsByTagName("a"); // god have mercy
		List<Version> answer = new ArrayList<Version>(nl.getLength());
		// start from 1, because zero is "../" and there's no other discriminators ffs
		for (int i = 1; i < nl.getLength(); i++) {
			// i don't *think* any dirs that's not a version name is allowed here,
			// and i *think* they always have trailing slashes for dirs...
			String prayersAndBlood = nl.item(i).getAttributes().getNamedItem("href").getTextContent();
			if (prayersAndBlood.endsWith("/"))
				answer.add(new Version(prayersAndBlood.substring(0, prayersAndBlood.length()-1)));
		}
		return answer;
	}
}
