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
        documentBuilder.setErrorHandler(handler);
        document = documentBuilder.parse(archive);
        handler.failFast();
        document.getDocumentElement().normalize();
        LOGGER.exiting(CLASS_NAME, "buildDocument");
        return document;
    }

    private void process(Document document) {
        LOGGER.entering(CLASS_NAME, "process");
        processRootSlideShows(document);
        LOGGER.exiting(CLASS_NAME, "process");
    }

    private void processRootSlideShows(Document document) {
        LOGGER.entering(CLASS_NAME, "processRootSlideShows");
        NodeList list = document.getElementsByTagName(XMLConstants.SLIDE_SHOW);
        for (int index = 0; index < list.getLength(); index++) {
            Node node = list.item(index);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element slideShowElement = (Element) node;
                Directory slideShow = new Directory(slideShowElement);
                SlideShowManager.instance().root().add(slideShow);
                processDirectories(slideShowElement, slideShow);
                processSlideShows(slideShowElement, slideShow);
            }
        }
        LOGGER.exiting(CLASS_NAME, "processRootSlideShows");
    }

    private void processDirectories(Element slideShowElement, Directory slideShow) {
        LOGGER.entering(CLASS_NAME, "processDirectories");
        NodeList list = slideShowElement.getChildNodes();
        for (int index = 0; index < list.getLength(); index++) {
            Node node = list.item(index);
            if (node.getNodeName().equals(XMLConstants.DIRECTORIES)) {
                Element directoriesElement = (Element) node;
                processEachDirectory(directoriesElement, slideShow);
            }
        }
        LOGGER.exiting(CLASS_NAME, "processDirectories");
    }

    private void processEachDirectory(Element directoriesElement, Directory slideShow) {
        LOGGER.entering(CLASS_NAME, "processEachDirectory");
        NodeList list = directoriesElement.getChildNodes();
        for (int index = 0; index < list.getLength(); index++) {
            Node node = list.item(index);
            if (node.getNodeName().equals(XMLConstants.DIRECTORY)) {
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element directoryElement = (Element) node;
                    Directory directory = new Directory(directoryElement);
                    slideShow.add(directory);
                }
            }
        }
        LOGGER.exiting(CLASS_NAME, "processEachDirectory");
    }

    private void processSlideShows(Element slideShowElement, Directory slideShow) {
        LOGGER.entering(CLASS_NAME, "processSlideShows");
        NodeList list = slideShowElement.getChildNodes();
        for (int index = 0; index < list.getLength(); index++) {
            Node node = list.item(index);
            if (node.getNodeName().equals(XMLConstants.SLIDE_SHOWS)) {
                Element slideShowsElement = (Element) node;
                processEachSlideShow(slideShowsElement, slideShow);
            }
        }
        LOGGER.exiting(CLASS_NAME, "processSlideShows");
    }

    private void processEachSlideShow(Element slideShowsElement, Directory slideShow) {
        LOGGER.entering(CLASS_NAME, "processEachSlideShow");
        NodeList list = slideShowsElement.getChildNodes();
        for (int index = 0; index < list.getLength(); index++) {
            Node node = list.item(index);
            if (node.getNodeName().equals(XMLConstants.SLIDE_SHOW)) {
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element nestedSlideShowElement = (Element) node;
                    Directory nestedSlideShow = new Directory(nestedSlideShowElement);
                    slideShow.add(nestedSlideShow);
                    processDirectories(nestedSlideShowElement, nestedSlideShow);
                    processSlideShows(nestedSlideShowElement, nestedSlideShow);
                }
            }
        }
        LOGGER.exiting(CLASS_NAME, "processEachSlideShow");
    }

}
