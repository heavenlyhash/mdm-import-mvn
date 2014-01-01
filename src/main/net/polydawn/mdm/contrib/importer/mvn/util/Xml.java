package net.polydawn.mdm.contrib.importer.mvn.util;

import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public class Xml {
	public static Document parse(byte[] bats) throws IOException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			return db.parse(new ByteArrayInputStream(bats));
		} catch (ParserConfigurationException e) {
			throw new Error("you what?", e);
		} catch (SAXException e) {
			throw new IOException ("shit xml.", e);
		}
	}
}
