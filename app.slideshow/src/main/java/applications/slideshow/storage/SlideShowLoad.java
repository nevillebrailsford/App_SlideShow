package applications.slideshow.storage;

import application.audit.AuditService;
import application.definition.ApplicationConfiguration;
import application.storage.AbstractLoadData;
import application.storage.XMLErrorHandler;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import applications.slideshow.model.Directory;
import applications.slideshow.model.XMLConstants;

public class SlideShowLoad extends AbstractLoadData {

    private static final String CLASS_NAME = SlideShowLoad.class.getName();
    private static final Logger LOGGER = ApplicationConfiguration.logger();

    @Override
    public void readData() throws IOException {
        LOGGER.entering(CLASS_NAME, "readData");
        try (InputStream archive = new BufferedInputStream(new FileInputStream(fileName()))) {
            AuditService.suspend();
            readDataFrom(archive);
            AuditService.resume();
        } catch (Exception e) {
            IOException exc = new IOException("SlideShowLoad: Exception occurred - " + e.getMessage(), e);
            LOGGER.throwing(CLASS_NAME, "readData", exc);
            throw exc;
        } finally {
            LOGGER.exiting(CLASS_NAME, "readData");
        }
    }

    private void readDataFrom(InputStream archive) throws Exception {
        LOGGER.entering(CLASS_NAME, "readDataFrom");
        Document document = buildDocument(archive);
        process(document);
        LOGGER.exiting(CLASS_NAME, "readDataFrom");
    }

    private Document buildDocument(InputStream archive) throws Exception {
        LOGGER.entering(CLASS_NAME, "buildDocument");
        Document document = null;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        URL url = SlideShowLoad.class.getResource("slideshow.xsd");
        documentBuilderFactory
                .setSchema(schemaFactory.newSchema(new Source[] { new StreamSource(url.toExternalForm()) }));
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        XMLErrorHandler handler = new XMLErrorHandler();
//        documentBuilder.setErrorHandler(handler);
        document = documentBuilder.parse(archive);
        handler.failFast();
        document.getDocumentElement().normalize();
        LOGGER.exiting(CLASS_NAME, "buildDocument");
        return document;
    }

    private void process(Document document) {
        LOGGER.entering(CLASS_NAME, "process");
        processSlideShows(document);
        processInnerSlideShows(document);
        LOGGER.exiting(CLASS_NAME, "process");
    }

    private void processSlideShows(Document document) {
        LOGGER.entering(CLASS_NAME, "processSlideShows");
        NodeList list = document.getElementsByTagName(XMLConstants.SLIDE_SHOW);
        for (int index = 0; index < list.getLength(); index++) {
            Node node = list.item(index);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element showElement = (Element) node;
                Directory show = new Directory(showElement);
                SlideShowManager.instance().addSlideShow(show);
                addDirectoriesToSlideShow(show, showElement);
            }
        }
        LOGGER.exiting(CLASS_NAME, "processSlideShows");
    }

    private void addDirectoriesToSlideShow(Directory show, Element showElement) {
        LOGGER.entering(CLASS_NAME, "addDirectoriesToSlideShow", show);
        NodeList list = showElement.getElementsByTagName(XMLConstants.FOLDER);
        if (list == null || list.getLength() == 0) {
            return;
        }
        for (int index = 0; index < list.getLength(); index++) {
            Node node = list.item(index);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element folderElement = (Element) node;
                Directory folder = new Directory(folderElement);
                SlideShowManager.instance().addDirectory(show, folder);
            }
        }
        LOGGER.exiting(CLASS_NAME, "addDirectoriesToSlideShow");
    }

    private void processInnerSlideShows(Document document) {
        LOGGER.entering(CLASS_NAME, "processInnerSlideShows");
        NodeList list = document.getElementsByTagName(XMLConstants.SLIDE_SHOW);
        for (int index = 0; index < list.getLength(); index++) {
            Node node = list.item(index);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element showElement = (Element) node;
                Directory temp = new Directory(showElement);
                Directory show = SlideShowManager.instance().slideShow(temp.title());
                addSlideShowsToSlideShow(show, showElement);
            }
        }
        LOGGER.exiting(CLASS_NAME, "processInnerSlideShows");
    }

    private void addSlideShowsToSlideShow(Directory show, Element showRootElement) {
        LOGGER.entering(CLASS_NAME, "addSlideShowsToSlideShow", show);
        NodeList list = showRootElement.getElementsByTagName(XMLConstants.SLIDE_SHOW);
        if (list == null || list.getLength() == 0) {
            return;
        }
        for (int index = 0; index < list.getLength(); index++) {
            Node node = list.item(index);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element showElement = (Element) node;
                Directory newShow = new Directory(showElement);
                SlideShowManager.instance().addDirectory(show, newShow);
            }
        }
        LOGGER.exiting(CLASS_NAME, "addSlideShowsToSlideShow");
    }

}
