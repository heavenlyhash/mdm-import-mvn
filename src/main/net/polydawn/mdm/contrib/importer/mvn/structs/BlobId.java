package net.polydawn.mdm.contrib.importer.mvn.structs;

import net.polydawn.mdm.contrib.importer.mvn.parsers.*;

/**
 * I guess I wish I could call this an "artifact", but it's really one member of
 * "the set of crap one pom refers to", which can be plural artifacts. It's also not an
 * {@link ArtifactId} that's sufficient to look up one of these; rather, it's the tuple of
 * {@link GroupId}, {@link ArtifactId}, and {@link Version}, so clearly this couldn't be
 * sanely referred to as an artifact, right?
 * <p>
 * I'm just going to call it a blob id. This is a term that exists nowhere that I know of
 * in the maven literature, but after reading
 * https://maven.apache.org/pom.html#Maven_Coordinates an n'th time, I still can't figure
 * out why I should care about "packaging" versus "classifier", and I can't find any way
 * to parse either of them out of the data I see in a maven repository (and "type" is
 * arguably in there somewhere, but only from some perspectives? how that can be used in
 * specifying a dependency but not actually used in the coordinate system is beyond my
 * reckoning), so I'm just making up a name for this gaping void in identifiers, and it's
 * as close to a terminal id for a real thing-with-content that we appear to ever see, so,
 * BlobId it is.
 * <p>
 * Notice that for reasons relating to the above insanity (and see also the comment block
 * in the middle of {@link PomParser}), this does not actually come from parsing any pom
 * or xml files at all... it comes from parsing an html page intended for human
 * consumption ({@link DirParser} performs this job).
 */
public class BlobId extends PartialIdentifier.PlainString {
	public BlobId(String blob) {
		super(blob);
	}

	@Override
	public String asPath() {
		// this is the terminal of a path, so unlike most, it doesn't want a trailing slash
		return asBlob();
	}
}
