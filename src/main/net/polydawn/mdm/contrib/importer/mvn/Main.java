package net.polydawn.mdm.contrib.importer.mvn;

import java.io.*;
import java.net.*;
import java.util.*;
import net.polydawn.mdm.contrib.importer.mvn.parsers.*;
import net.polydawn.mdm.contrib.importer.mvn.structs.*;

public class Main {
	public static void main(String[] args) throws MalformedURLException, IOException {
		if (args.length != 2) {
			System.err.println(
				"Usage: exactly two args, groupId and artifactId.\n"+
				"An mdm repo named {groupId}..{artifactId} will be created, or new versions appended to it if present."
			);
		}

		GroupId groupId = new GroupId(args[0]);
		ArtifactId artifactId = new ArtifactId(args[1]);

		List<Version> versions = new MetadataParser(curler).fetch(groupId, artifactId);

		for (Version version : versions) {
			List<String> files = new DirParser(curler).fetch(groupId, artifactId, version).getFileList();

		}
	}

	private static final String CURL_PREFIX = "http://repo1.maven.org/maven2/";
	private static final Curler curler = new Curler(CURL_PREFIX);
}
