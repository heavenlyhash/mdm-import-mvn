package net.polydawn.mdm.contrib.importer.mvn.parsers;

import java.io.*;
import java.net.*;
import net.polydawn.mdm.contrib.importer.mvn.structs.*;

public class MetadataParser {
	public MetadataParser(Curler curler) {
		this.curler = curler;
	}

	private final Curler curler;

	public void fetch(GroupId groupId, ArtifactId artifactId) throws MalformedURLException, IOException {
		byte[] bats = curler.curl(groupId.asBlob()+artifactId.asPath()+"/maven-metadata.xml");

	}
}
