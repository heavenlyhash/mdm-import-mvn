package net.polydawn.mdm.contrib.importer.mvn.util;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public class Xml {
	public static Element parse(byte[] bats) throws IOException {

		// replace <hr> tags with blank spaces
		// fuck you, that's why
		List<Integer> bads = allIndexOf(bats, new byte[] { '<', 'h', 'r', '>' });
		for (int bad : bads) {
			bats[bad++] = ' ';
			bats[bad++] = ' ';
			bats[bad++] = ' ';
			bats[bad++] = ' ';
		}

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			return db.parse(new ByteArrayInputStream(bats)).getDocumentElement();
		} catch (ParserConfigurationException e) {
			throw new Error("you what?", e);
		} catch (SAXException e) {
			throw new IOException("shit xml.", e);
		}
	}

	// get some Knuth-Morris-Pratt all up in here
	public static List<Integer> allIndexOf(byte[] data, byte[] pattern) {
		int[] failure = computeKmpFailure(pattern);

		int j = 0;
		if (data.length == 0) return Collections.emptyList();
		if (pattern.length > data.length) return Collections.emptyList();

		List<Integer> answer = new LinkedList<Integer>();
		for (int i = 0; i < data.length; i++) {
			while (j > 0 && pattern[j] != data[i]) {
				j = failure[j - 1];
			}
			if (pattern[j] == data[i]) {
				j++;
			}
			if (j == pattern.length) {
				answer.add(i - pattern.length + 1);
				j = 0;
			}
		}
		return answer;
	}

	private static int[] computeKmpFailure(byte[] pattern) {
		int[] failure = new int[pattern.length];

		int j = 0;
		for (int i = 1; i < pattern.length; i++) {
			while (j > 0 && pattern[j] != pattern[i])
				j = failure[j - 1];
			if (pattern[j] == pattern[i])
				j++;
			failure[i] = j;
		}

		return failure;
	}
}
