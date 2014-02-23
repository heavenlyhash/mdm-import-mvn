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

		String exportName = groupId.asBlob()+".."+artifactId.asBlob();

		// TODO: check if already exists first.
		exec("mdm", "release-init", "--name="+artifactId.asBlob(), "--repo="+exportName);
		System.out.println();

		List<Version> versions = new MetadataParser(curler).fetch(groupId, artifactId);
		for (Version version : versions) {
			// TODO: check if already exists first.
			// wonder if we should curl maven repos again to make sure they didn't change, because they have before, and it's caused people bad days.  but I don't know how we'd respond, anyway.

			System.out.println("handling version "+version.asBlob()+":");

			List<BlobId> files = new DirParser(curler).fetch(groupId, artifactId, version);

			// TODO: actually curl

			exec("mdm", "release", "--repo="+exportName, "--version="+version.asBlob(), "--files=/tmp/meh");

			System.out.println();
		}
	}

	private static final String CURL_PREFIX = "http://repo1.maven.org/maven2/";
	private static final Curler curler = new Curler(CURL_PREFIX);

	private static int exec(String... cmd) {
		try {
			Process p = new ProcessBuilder(cmd)
				.redirectOutput(ProcessBuilder.Redirect.INHERIT)
				.redirectError(ProcessBuilder.Redirect.INHERIT)
				.start();
			p.getOutputStream().close();
			p.waitFor();
			return p.exitValue();
		} catch (IOException e) {
			e.printStackTrace();
			return Integer.MIN_VALUE;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return Integer.MIN_VALUE;
		}
	}
}
