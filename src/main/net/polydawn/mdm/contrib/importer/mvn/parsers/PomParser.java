package net.polydawn.mdm.contrib.importer.mvn.parsers;

import java.io.*;
import java.net.*;
import net.polydawn.mdm.contrib.importer.mvn.structs.*;
import net.polydawn.mdm.contrib.importer.mvn.util.*;
import org.w3c.dom.*;

public class PomParser {
	public PomParser(Curler curler) {
		this.curler = curler;
	}

	private final Curler curler;

	public Object fetch(GroupId groupId, ArtifactId artifactId, Version version) throws MalformedURLException, IOException {
		String path =
				groupId.asPath() +
				artifactId.asPath() +
				version.asPath() +
				artifactId.asBlob() +
				"-" +
				version.asBlob() +
				".pom";
		Element xml = Xml.parse(curler.curl(path));

		/*
		 * Example format:
		 *
			<?xml version="1.0" encoding="UTF-8"?>
			<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0"
			    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			  <modelVersion>4.0.0</modelVersion>
			  <groupId>org.springframework.android</groupId>
			  <artifactId>spring-android-core</artifactId>
			  <version>1.0.1.RELEASE</version>
			  <name>Spring for Android Core</name>
			  <description>Spring for Android Core</description>
			  <url>https://github.com/SpringSource/spring-android</url>
			  <organization>
			    <name>SpringSource</name>
			    <url>http://springsource.org/spring-android</url>
			  </organization>
			  <licenses>
			    <license>
			      <name>The Apache Software License, Version 2.0</name>
			      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
			      <distribution>repo</distribution>
			    </license>
			  </licenses>
			  <developers>
			    <developer>
			      <id>royclarkson</id>
			      <name>Roy Clarkson</name>
			      <email>rclarkson@vmware.com</email>
			    </developer>
			  </developers>
			  <scm>
			    <connection>scm:git:git://github.com/SpringSource/spring-android</connection>
			    <developerConnection>scm:git:git://github.com/SpringSource/spring-android</developerConnection>
			    <url>https://github.com/SpringSource/spring-android</url>
			  </scm>
			  <dependencies>
			    <dependency>
			      <groupId>com.google.android</groupId>
			      <artifactId>android</artifactId>
			      <version>4.1.1.4</version>
			      <scope>provided</scope>
			    </dependency>
			  </dependencies>
			</project>
		 *
		 * So, right, the problem with this... is it doesn't actually *list any of the fucking artifact files*.
		 * If you `ls` that dir, there's a ".jar", "-sources.jar", and "-javadoc.jar".
		 * But how the fuck are you supposed to find that out from this file?  You can't.
		 * And nowhere in this pom, nor the parent metadata xml, was a project type of "java" specified, even.
		 * You basically have to read maven's mind, here, apparently.
		 *
		 * Oh.  Okay, so... apparently, "jar" is the default... classifier?  Type?  Packaging?  The docs can't keep it straight and neither can I.
		 * And the rest of the "-sources" and "-javadoc" classifiers (pretty sure those are definitely classifiers)...
		 * they don't mean anything.  Unless someone asks for them.  Huh.
		 * And as far as I can tell, there really *isn't* an API in maven to ask for those secondary artifacts.
		 * Other plugins that talk about sources or whatever just ask for them because that's what *that plugin* expects the file to be called.
		 *
		 * Related experiment:
		 * `mvn org.apache.maven.plugins:maven-dependency-plugin:2.1:get -DrepoUrl=url -Dartifact=org.springframework.android:spring-android-core:1.0.1.RELEASE`
		 * (and yes, -DrepoUrl=bullshit is *necessary*... if you don't provide it, it errors, but if you do, then maven calls your bullshit and drops in a reasonable default, yes I'm serious)
		 * just gets these two files:
		 * - spring-android-core-1.0.1.RELEASE.jar
		 * - spring-android-core-1.0.1.RELEASE.jar.sha1
		 * none of the other classifiers, not even the .md5 for the jar, or the .asc with the sig.
		 *
		 * So, I guess we're going to curl the html listing made for humans,
		 * and extract the godforsaken links to actual files from that.
		 */

		// fuck
		return null;
	}
}
