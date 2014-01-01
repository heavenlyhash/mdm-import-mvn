package net.polydawn.mdm.contrib.importer.mvn.structs;

import java.util.*;
import net.polydawn.mdm.contrib.importer.mvn.util.*;

public interface PartialIdentifier {
	/**
	 * The default stringy format for a thing.
	 */
	String asBlob();

	/**
	 * The slash-separated format of a thing that can be catted to form a path for
	 * fetching stuff.
	 */
	String asPath();



	public static abstract class PlainString implements PartialIdentifier {
		protected PlainString(String blob) {
			this.blob = blob;
		}

		private final String blob;

		public String asBlob() {
			return blob;
		}

		public String asPath() {
			return blob + "/";
		}
	}



	public static abstract class ChunkedString implements PartialIdentifier {
		public ChunkedString(String blob) {
			this.chunks = Arrays.asList(blob.split(defineChunkSeparator()));
		}

		public ChunkedString(List<String> chunks) {
			this.chunks = chunks;
		}

		protected abstract String defineChunkSeparator();

		private final List<String> chunks;

		public String asBlob() {
			return Loco.join(defineChunkSeparator(), chunks);
		}

		public String asPath() {
			return Loco.join("/", chunks) + "/";
		}
	}
}
