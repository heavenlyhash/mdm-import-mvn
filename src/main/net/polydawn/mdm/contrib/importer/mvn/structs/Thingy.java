package net.polydawn.mdm.contrib.importer.mvn.structs;

import java.util.*;

/**
 * I guess I wish I could call this an "artifact", but it's really
 * "the set of crap one pom refers to", which can be plural artifacts. It's also not an
 * {@link ArtifactId} that's sufficient to look up one of these; rather, it's the tuple of
 * {@link GroupId}, {@link ArtifactId}, and {@link Version}, so clearly this couldn't be
 * sanely referred to as an artifact, right?
 */
public class Thingy {
	public Thingy(List<String> files) {
		this.files = files;
	}

	/**
	 * This is just a list of pure files. We don't know anything about them &mdash
	 * Maven doesn't either. So, we're just going to assume the publisher of a project
	 * knew what they were doing when they published, and that all of these files have
	 * meaning to <i>someone</i> somewhere.
	 */
	private final List<String> files;

	public List<String> getFileList() {
		return this.files;
	}
}
