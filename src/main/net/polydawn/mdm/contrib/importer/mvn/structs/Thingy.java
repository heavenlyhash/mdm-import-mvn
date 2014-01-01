package net.polydawn.mdm.contrib.importer.mvn.structs;

/**
 * I guess I wish I could call this an "artifact", but it's really
 * "the set of crap one pom refers to", which can be plural artifacts. It's also not an
 * {@link ArtifactId} that's sufficient to look up one of these; rather, it's the tuple of
 * {@link GroupId}, {@link ArtifactId}, and {@link Version}, so clearly this couldn't be
 * sanely referred to as an artifact, right?
 */
public class Thingy {

}
