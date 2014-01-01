package net.polydawn.mdm.contrib.importer.mvn.structs;

import java.util.*;

/**
 * A {@link GroupId} is the dot-separated thingy. It tends to kind look like your org name and the
 * prefix of your packages if you're a java shop. It's split into dirs in maven http
 * repos.  The next layer of specifity is {@link ArtifactId}.
 */
public class GroupId extends PartialIdentifier.ChunkedString {
	public GroupId(String blob) {
		super(blob);
	}

	public GroupId(List<String> chunks) {
		super(chunks);
	}

	protected String defineChunkSeparator() {
		return ".";
	}
}
