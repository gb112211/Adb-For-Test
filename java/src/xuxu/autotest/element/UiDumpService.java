package xuxu.autotest.element;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

//解析uidump.xml文件
public class UiDumpService extends DefaultHandler {
	private List<UiDump> dumps = null;
	private UiDump dump = null;

	public List<UiDump> getDumps(InputStream xml) {

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = null;
		try {
			parser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		UiDumpService handler = new UiDumpService();
		try {
			parser.parse(xml, handler);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return handler.getDumps();
	}

	private List<UiDump> getDumps() {
		return dumps;
	}

	@Override
	public void startDocument() throws SAXException {
		dumps = new ArrayList<UiDump>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if ("node".equals(qName)) {
			dump = new UiDump();
			dump.setText(attributes.getValue("text"));
			dump.setResourceId(attributes.getValue("resource-id"));
			dump.setClassName(attributes.getValue("class"));
			dump.setContentDesc(attributes.getValue("content-desc"));
			dump.setCheckable(attributes.getValue("checkable"));
			dump.setChecked(attributes.getValue("checked"));
			dump.setClickable(attributes.getValue("clickable"));
			dump.setBounds(attributes.getValue("bounds"));

			dumps.add(dump);
		}
	}
}
