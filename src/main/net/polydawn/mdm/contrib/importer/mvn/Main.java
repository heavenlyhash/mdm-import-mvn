package net.polydawn.mdm.contrib.importer.mvn;

public class Main {
	public static void main(String[] args) {

	}

	/**
	 *
	 *
	 * {@code
	 * <groupId>org.springframework.android</groupId>
	 * <artifactId>spring-android-core</artifactId>
	 * }
	 */
	public static void getThing(String groupId) {

	}

	/**
	 * Like
	 * @param route
	 */
	public static void curlXml(String route) {

	}

	/**
	 * <p>
	 * Get some bytes from a maven repo. I just hardcoded one at random.
	 * </p>
	 *
	 * <p>
	 * These two url prefixes seem to do approximately the same thing when suffixed:
	 * <ul>
	 * <li>{@code http://search.maven.org/remotecontent?filepath=}
	 * <li>{@code http://repo1.maven.org/maven2/}
	 * </ul>
	 * Though of course they're slightly different in bizzare ways, like serving
	 * different content type headers for some paths, one of which confuses firefox
	 * mightily.
	 * </p>
	 *
	 * <p>
	 * An example route suffix is
	 * {@code org/springframework/android/spring-android-core/maven-metadata.xml}, for
	 * {@code
	 * <groupId>org.springframework.android</groupId>
	 * <artifactId>spring-android-core</artifactId>
	 * }
	 * </p>
	 */
	public static void curlBytes(String route) {

	}

	public static final String CURL_PREFIX = "http://repo1.maven.org/maven2/";
}
