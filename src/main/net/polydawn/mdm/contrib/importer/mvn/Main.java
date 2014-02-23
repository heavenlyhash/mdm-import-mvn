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
				"An mdm repo named \"{groupId}/{artifactId}\" will be created, or new versions appended to it if present."
			);
		}

		GroupId groupId = new GroupId(args[0]);
		ArtifactId artifactId = new ArtifactId(args[1]);

		String exportName = groupId.asBlob()+"/"+artifactId.asBlob();

		if (!new File(exportName).exists()) {
			new File(groupId.asBlob()).mkdir();
			exec("mdm", "release-init", "--name="+artifactId.asBlob(), "--repo="+exportName);
			System.out.println();
		} else {
			System.out.println("path "+exportName+" already exists, skipping release-init");
			System.out.println();
		}

		List<Version> versions = new MetadataParser(curler).fetch(groupId, artifactId);
		for (Version version : versions) {
			// skip if we've got this version
			if (0 == exec(new File(exportName), "git", "ls-remote", "--exit-code", ".", "refs/heads/mdm/release/"+version.asBlob())) {
				System.out.println("version "+version.asBlob()+" already exists, skipping");
				System.out.println();
				continue;
			}

			System.out.println("handling version "+version.asBlob()+":");

			// download all our junk to a temporary location
			File tmpdir = FileUtil.createTmpDir(new File("."));
			tmpdir.deleteOnExit();

			// fetch files, save to disk
			List<BlobId> files = new DirParser(curler).fetch(groupId, artifactId, version);
			for (BlobId file : files) {
				// skip files that are checksums.  in our world, checksums are the responsibility of the version control.
				if (file.asBlob().endsWith(".md5")) continue;
				if (file.asBlob().endsWith(".sha1")) continue;

				// pick output filename.  (strip the version name: in our world the version is a property of the version control, not the file name)
				String saveName = file.asBlob().replace("-"+version.asBlob(), "");

				// download and save
				String route = groupId.asPath() + artifactId.asPath() + version.asPath() + file.asPath();
				System.out.println("  curling "+route);
				byte[] blob = curler.curl(route);
				FileUtil.save(blob, new File(tmpdir, saveName));
			}

			// execute mdm release
			exec("mdm", "release", "--repo="+exportName, "--version="+version.asBlob(), "--files="+tmpdir.toString());

			// clean up
			for (File f : tmpdir.listFiles()) f.delete();
			tmpdir.delete();
			System.out.println();
		}

		// shrink repo and drop uninteresting logs
		System.out.println("cleaning up release repo");
		exec(new File(exportName), "git", "reflog", "expire", "--all");
		exec(new File(exportName), "git", "gc", "--aggressive", "--prune=now");
		System.out.println();

		System.out.println("done!");
	}

	private static final String CURL_PREFIX = "http://repo1.maven.org/maven2/";
	private static final Curler curler = new Curler(CURL_PREFIX);


	private static int exec(String... cmd) {
		return exec(new File("."), cmd);
	}

	private static int exec(File cwd, String... cmd) {
		try {
			Process p = new ProcessBuilder(cmd)
				.directory(cwd)
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
