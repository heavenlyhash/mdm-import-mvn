package net.polydawn.mdm.contrib.importer.mvn.structs;

import java.util.*;
import net.polydawn.mdm.contrib.importer.mvn.util.*;

/**
 * A {@link GroupId} is the dot-separated thingy. It tends to kind look like your org name and the
 * prefix of your packages if you're a java shop. It's split into dirs in maven http
 * repos.  The next layer of specifity is {@link ArtifactId}.
 */
public class GroupId {
	public GroupId(String blob) {
		this(Arrays.asList(blob.split(".")));
	}

	public GroupId(List<String> chunks) {
		this.chunks = chunks;
	}

	private final List<String> chunks;

	public String asBlob() {
		return Loco.join(".", chunks);
	}

	public String asPath() {
		return Loco.join("/", chunks)+"/";
	}
}
