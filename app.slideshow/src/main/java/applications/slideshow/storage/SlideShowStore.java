package applications.slideshow.storage;

import application.definition.ApplicationConfiguration;
import application.storage.AbstractStoreData;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import applications.slideshow.model.Directory;
import applications.slideshow.model.XMLConstants;

public class SlideShowStore extends AbstractStoreData {
    private static final String CLASS_NAME = SlideShowStore.class.getName();
    private static final Logger LOGGER = ApplicationConfiguration.logger();

    @Override
    public void storeData() throws IOException {
        LOGGER.entering(CLASS_NAME, "storeData");
        try (OutputStream archive = new BufferedOutputStream(new FileOutputStream(fileName()))) {
            writeDataTo(archive);
        } catch (Exception e) {
            IOException exc = new IOException("PropertyStore: Exception occurred - " + e.getMessage(), e);
            LOGGER.throwing(CLASS_NAME, "storeData", exc);
            throw exc;
        } finally {
            LOGGER.exiting(CLASS_NAME, "storeData");
        }
    }

    private void writeDataTo(OutputStream archive) throws IOException {
        LOGGER.entering(CLASS_NAME, "writeDataTo");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            writeDataTo(document);
            writeXML(document, archive);
        } catch (ParserConfigurationException e) {
            LOGGER.warning("Caught exception: " + e.getMessage());
            IOException exc = new IOException(e.getMessage());
            LOGGER.throwing(CLASS_NAME, "readDataFrom", exc);
            throw exc;
        } finally {
            LOGGER.exiting(CLASS_NAME, "writeDataTo");
        }
    }

    private void writeDataTo(Document document) {
        LOGGER.entering(CLASS_NAME, "writeDataTo", document);
        Element rootElement = document.createElement(XMLConstants.ROOT);
        document.appendChild(rootElement);
        addSlideShowElements(document, rootElement);
        LOGGER.exiting(CLASS_NAME, "writeDataTo");
    }

    private void addSlideShowElements(Document document, Element slideShowsRootElement) {
        LOGGER.entering(CLASS_NAME, "addSlideShowElements");
        Element slideShowsElement = document.createElement(XMLConstants.SLIDE_SHOWS);
        slideShowsRootElement.appendChild(slideShowsElement);
        for (Directory slideShow : SlideShowManager.instance().slideShows()) {
            Element slideShowElement = buildElementFor(slideShow, document);
            slideShowsElement.appendChild(slideShowElement);
        }
        LOGGER.exiting(CLASS_NAME, "addSlideShowElements");
    }

    private Element buildElementFor(Directory slideShow, Document document) {
        LOGGER.entering(CLASS_NAME, "buildElementFor", new Object[] { slideShow, document });
        Element slideShowElement = slideShow.buildElement(document);
        Element directoriesElement = document.createElement(XMLConstants.DIRECTORIES);
        slideShowElement.appendChild(directoriesElement);
        for (Directory directory : slideShow.directories()) {
            directoriesElement.appendChild(buildElementForDirectory(directory, document));
        }
        Element slideShowsElement = document.createElement(XMLConstants.SLIDE_SHOWS);
        slideShowElement.appendChild(slideShowsElement);
        for (Directory innerSlideShow : SlideShowManager.instance().slideShows(slideShow)) {
            slideShowsElement.appendChild(buildElementFor(innerSlideShow, document));
        }
        LOGGER.exiting(CLASS_NAME, "buildElementFor");
        return slideShowElement;
    }

    private Element buildElementForDirectory(Directory directory, Document document) {
        LOGGER.entering(CLASS_NAME, "buildElementFor", new Object[] { directory, document });
        Element documentElement = directory.buildElement(document);
        LOGGER.exiting(CLASS_NAME, "buildElementFor");
        return documentElement;
    }

    private void writeXML(Document doc, OutputStream output) throws IOException {
        LOGGER.entering(CLASS_NAME, "writeXML", new Object[] { doc, output });
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(output);
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new IOException(e.getMessage());
        } finally {
            LOGGER.exiting(CLASS_NAME, "writeXML", new Object[] { doc, output });
        }
    }

}
