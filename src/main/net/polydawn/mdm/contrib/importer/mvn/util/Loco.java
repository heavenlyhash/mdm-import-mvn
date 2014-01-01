package net.polydawn.mdm.contrib.importer.mvn.util;

import java.util.*;

public class Loco {
	public static String join(String join, String... strings) {
		return join(join, Arrays.asList(strings));
	}

	/**
	 * God have mercy.
	 */
	public static String join(String join, Collection<String> strings) {
		if (strings == null || strings.size() == 0) {
			return "";
		} else if (strings.size() == 1) {
			return strings.iterator().next();
		} else {
			StringBuilder sb = new StringBuilder();
			Iterator<String> itr = strings.iterator();
			sb.append(itr.next());
			while (itr.hasNext()) {
				sb.append(join).append(itr.next());
			}
			return sb.toString();
		}

	}
}
