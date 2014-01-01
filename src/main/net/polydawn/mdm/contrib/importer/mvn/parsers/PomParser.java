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
		 */

		// fuck
		return null;
	}
}
