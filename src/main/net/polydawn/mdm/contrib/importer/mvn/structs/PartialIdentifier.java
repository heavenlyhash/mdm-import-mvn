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

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((this.blob == null) ? 0 : this.blob.hashCode());
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			PlainString other = (PlainString) obj;
			if (this.blob == null) {
				if (other.blob != null) return false;
			} else if (!this.blob.equals(other.blob)) return false;
			return true;
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

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((this.chunks == null) ? 0 : this.chunks.hashCode());
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			ChunkedString other = (ChunkedString) obj;
			if (this.chunks == null) {
				if (other.chunks != null) return false;
			} else if (!this.chunks.equals(other.chunks)) return false;
			return true;
		}
	}
}
