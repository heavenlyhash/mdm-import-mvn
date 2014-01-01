package net.polydawn.mdm.contrib.importer.mvn.structs;

/**
 * A thing that identifies an artifact, if you believe the name; of course, it doesn't,
 * really. Combined with a {@GroupId}, this points at all versions of a thing.
 * And by "a thing" I mean an arbitrary number of things and files, because you can have
 * multiple "secondary artifacts" of different "type".
 */
public class ArtifactId {
	public ArtifactId(String blob) {
		this.blob = blob;
	}

	private final String blob;

	public String asBlob() {
		return blob;
	}

	public String asPath() {
		return blob;
	}
}
