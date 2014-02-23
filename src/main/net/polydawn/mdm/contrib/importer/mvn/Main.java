package net.polydawn.mdm.contrib.importer.mvn;

import java.io.*;
import java.net.*;
import java.util.*;
import net.polydawn.mdm.contrib.importer.mvn.parsers.*;
import net.polydawn.mdm.contrib.importer.mvn.structs.*;
import net.polydawn.mdm.contrib.importer.mvn.util.*;

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

		if (!new File(exportName).exists()) {
			exec("mdm", "release-init", "--name="+artifactId.asBlob(), "--repo="+exportName);
			System.out.println();
		} else {
			System.out.println("path "+exportName+" already exists, skipping release-init");
			System.out.println();
		}

		List<Version> versions = new MetadataParser(curler).fetch(groupId, artifactId);
		for (Version version : versions) {
			if (new File(exportName, ".git/refs/heads/mdm/release/"+version.asBlob()).exists()) {
				System.out.println("version "+version.asBlob()+" already exists, skipping");
				continue;
			}

			System.out.println("handling version "+version.asBlob()+":");

			File tmpdir = FileUtil.createTmpDir(new File("."));
			tmpdir.deleteOnExit();

			List<BlobId> files = new DirParser(curler).fetch(groupId, artifactId, version);
			for (BlobId file : files) {
				String route = groupId.asPath() + artifactId.asPath() + version.asPath() + file.asPath();
				System.out.println("  curling "+route);
				byte[] blob = curler.curl(route);
				// TODO: parse out the version name from the blob, discard it: in our world the version is a property of the version control, not the file name
				FileUtil.save(blob, new File(tmpdir, file.asBlob()));
			}

			exec("mdm", "release", "--repo="+exportName, "--version="+version.asBlob(), "--files="+tmpdir.toString());
			tmpdir.delete();

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
