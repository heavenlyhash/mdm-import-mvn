package net.polydawn.mdm.contrib.importer.mvn.util;

import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

public class Xml {
	public static Element parse(byte[] bats) throws IOException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			return db.parse(new ByteArrayInputStream(bats)).getDocumentElement();
		} catch (ParserConfigurationException e) {
			throw new Error("you what?", e);
		} catch (SAXException e) {
			throw new IOException ("shit xml.", e);
		}
	}
}
