package net.polydawn.mdm.contrib.importer.mvn;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import net.polydawn.mdm.contrib.importer.mvn.parsers.*;
import net.polydawn.mdm.contrib.importer.mvn.structs.*;
import net.polydawn.mdm.contrib.importer.mvn.util.*;

public class Main {
	private static void printUsage() {
		System.err.println(
			"Usage:\n" +
			"  mdm-import-mvn <groupId> <artifactId> [outputDir]\n"+
			"\n"+
			"  If no output dir is provided, it defaults to \"{groupId}/{artifactId}-releases\".\n"+
			"  An mdm repo will be created there, or new versions appended to it if present."
		);
	}

	public static void main(String[] args) throws MalformedURLException, IOException, ExecutionException {
		if (args.length < 2 || args.length > 3) {
			printUsage();
			System.exit(1);
		}

		GroupId groupId = new GroupId(args[0]);
		ArtifactId artifactId = new ArtifactId(args[1]);

		String exportName = (args.length >= 3) ? args[2] : groupId.asBlob()+"/"+artifactId.asBlob()+"-releases";

		if (!new File(exportName).exists()) {
			new File(exportName).mkdirs();
			exec("mdm", "release-init", "--name="+artifactId.asBlob(), "--repo="+exportName);
			System.out.println();
		} else {
			System.out.println("path "+exportName+" already exists, skipping release-init");
			System.out.println();
		}

		List<Version> versions = new MetadataParser(curler).fetch(groupId, artifactId);
		boolean gotSome = false;
		for (Version version : versions) {
			String versionTarget = version.asBlob() + ".mvn";

			// skip if we've got this version
			if (0 == execa(new File(exportName), "git", "ls-remote", "--exit-code", ".", "refs/heads/mdm/release/"+versionTarget)) {
				System.out.println("version "+versionTarget+" already exists, skipping");
				System.out.println();
				continue;
			}
			gotSome |= true;

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
			exec("mdm", "release", "--repo="+exportName, "--version="+versionTarget, "--files="+tmpdir.toString());

			// clean up
			for (File f : tmpdir.listFiles()) f.delete();
			tmpdir.delete();
			System.out.println();
		}

		// shrink repo and drop uninteresting logs
		if (gotSome) {
			System.out.println("cleaning up release repo");
			exec(new File(exportName), "git", "reflog", "expire", "--all");
			String conf_pack = System.getenv("PACK");
			System.out.println("  pack configured to: \""+conf_pack+"\"");
			if ("aggressive".equals(conf_pack)) {
				System.out.println("  running aggressive gc");
				exec(new File(exportName), "git", "gc", "--aggressive", "--prune=now");
			} else if ("wide".equals(conf_pack)) {
				System.out.println("  running 'wide' repack");
				// repack into files of a limited size.  this makes a serious effort at deduping, but also refrains
				// from creating overly large packfiles, which is important because git transports don't pull objects
				// out of packs if a client only needs part of the pack.
				exec(new File(exportName), "git", "repack", "--max-pack-size=1M", "--depth=3", "--window=550", "-adf");
				// also do a (non-aggro) gc.  this removes packs that might now be dangling, and also does some other
				// cleanup like packing refs files (can help reduce round trips for a dumb http client).
				exec(new File(exportName), "git", "gc", "--prune=now");
			}
			System.out.println();
		}

		System.out.println("done!");
	}

	private static final String CURL_PREFIX = "http://repo1.maven.org/maven2/";
	private static final Curler curler = new Curler(CURL_PREFIX);


	private static int exec(String... cmd) throws ExecutionException {
		return exec(false, new File("."), cmd);
	}

	private static int execa(String... cmd) throws ExecutionException {
		return exec(true, new File("."), cmd);
	}

	private static int exec(File cwd, String... cmd) throws ExecutionException {
		return exec(false, new File("."), cmd);
	}

	private static int execa(File cwd, String... cmd) throws ExecutionException {
		return exec(true, new File("."), cmd);
	}

	private static int exec(boolean allowNonzero, File cwd, String... cmd) throws ExecutionException {
		try {
			System.out.println("exec: "+Arrays.toString(cmd));
			Process p = new ProcessBuilder(cmd)
				.directory(cwd)
				.redirectOutput(ProcessBuilder.Redirect.INHERIT)
				.redirectError(ProcessBuilder.Redirect.INHERIT)
				.start();
			p.getOutputStream().close();
			p.waitFor();
			int exitCode = p.exitValue();
			if (!allowNonzero && exitCode != 0)
				throw new ExecutionException("executing \""+cmd[0]+"\" returned code "+exitCode, null);
			return exitCode;
		} catch (IOException e) {
			throw new ExecutionException("error executing \""+cmd[0]+"\"", e);
		} catch (InterruptedException e) {
			throw new ExecutionException("error executing \""+cmd[0]+"\"", e);
		}
	}
}
