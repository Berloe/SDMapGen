package org.smapgen.scl.repo.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public final class LoadPomDeps {

    /**
     * 
     */
    private LoadPomDeps() {
        super();
    }

    /**
     * @param conf
     * @return
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    protected static ArrayList<Artifact> loadDepsXmlFile(File conf) throws FileNotFoundException, XMLStreamException {
        ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(conf));
        while (reader.hasNext()) {
            int event = reader.next();
            if (XMLStreamConstants.START_ELEMENT == event && Constants.DEPENDENCIES.equals(reader.getLocalName())) {
                artifacts.addAll(dependencyBlock(reader, event));
            }
            if (XMLStreamConstants.START_ELEMENT == event
                    && Constants.DEPENDENCY_MANAGEMENT.equals(reader.getLocalName())) {
                artifacts.addAll(dependencyBlock(reader, event));
            }
        }
        return artifacts;
    }

    /**
     * @param artifacts
     * @param reader
     * @param event
     * @throws XMLStreamException
     */
    public static ArrayList<Artifact> dependencyBlock(XMLStreamReader reader, int ev) throws XMLStreamException {
        int event = ev;
        ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
        while (reader.hasNext()) {
            if (XMLStreamConstants.START_ELEMENT == event && Constants.DEPENDENCIES.equals(reader.getLocalName())) {
                artifacts.addAll(loadDependencies(reader));
                break;
            }
            if (XMLStreamConstants.END_ELEMENT == event && Constants.DEPENDENCIES.equals(reader.getLocalName())) {
                return artifacts;
            }
            event = reader.next();
        }
        return artifacts;
    }

    /**
     * @param reader
     * @return
     * @throws XMLStreamException
     */
    private static ArrayList<Artifact> loadDependencies(XMLStreamReader reader) throws XMLStreamException {
        ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
        while (reader.hasNext()) {
            int event = reader.next();
            if (XMLStreamConstants.START_ELEMENT == event && Constants.DEPENDENCY.equals(reader.getLocalName())) {
                artifacts.add(loadDependency(reader));
            }
            if (XMLStreamConstants.END_ELEMENT == event && Constants.DEPENDENCIES.equals(reader.getLocalName())) {
                return artifacts;
            }
        }
        return artifacts;

    }

    /**
     * @param reader
     * @return
     * @throws XMLStreamException
     */
    private static Artifact loadDependency(XMLStreamReader reader) throws XMLStreamException {
        Artifact artfact = new Artifact();
        boolean ignoreByExclusion = false;
        while (reader.hasNext()) {
            int event = reader.next();
            if (XMLStreamConstants.START_ELEMENT == event && !ignoreByExclusion) {
                switch (reader.getLocalName()) {
                    case Constants.GROUP_ID:
                        artfact.setGroup(getContent(reader));
                        break;
                    case Constants.ARTIFACT_ID:
                        artfact.setArtifact(getContent(reader));
                        break;
                    case Constants.VERSION:
                        artfact.setVersion(getContent(reader));
                        break;
                    case Constants.SCOPE:
                        String scop = getContent(reader);
                        if (scop == null || !scop.matches("[a-zA-Z0-9 ]+")) {
                            scop = Constants.COMPILE;
                        }
                        artfact.setScope(scop);
                        break;
                    case Constants.EXCLUSIONS:
                        ignoreByExclusion=true;
                        break;
                    default:
                        break;
                }
            }
            if (XMLStreamConstants.END_ELEMENT == event){
                if (Constants.DEPENDENCY.equals(reader.getLocalName())) {
                    if (artfact.getScope() == null || artfact.getScope().matches("[a-zA-Z0-9 ]+")) {
                        String scop = Constants.COMPILE;
                        artfact.setScope(scop);
                    }
                    return artfact;
                }else if( Constants.EXCLUSIONS.equals(reader.getLocalName())) {
                    ignoreByExclusion=false;
                }
            }
           
        }
        if (artfact.getScope() == null || artfact.getScope().matches("[a-zA-Z0-9 ]+")) {
            String scop = Constants.COMPILE;
            artfact.setScope(scop);
        }
        return artfact;
    }

    /**
     * @param reader
     * @return
     * @throws XMLStreamException
     */
    private static Artifact loadParent(XMLStreamReader reader) throws XMLStreamException {
        Artifact artfact = new Artifact();
        while (reader.hasNext()) {
            int event = reader.next();
            if (XMLStreamConstants.START_ELEMENT == event) {
                switch (reader.getLocalName()) {
                    case Constants.GROUP_ID:
                        artfact.setGroup(getContent(reader));
                        break;
                    case Constants.ARTIFACT_ID:
                        artfact.setArtifact(getContent(reader));
                        break;
                    case Constants.VERSION:
                        artfact.setVersion(getContent(reader));
                        break;
                    default: break;
                }
            }
            if (XMLStreamConstants.END_ELEMENT == event && Constants.PARENT.equals(reader.getLocalName())) {
                return artfact;
            }
        }
        return artfact;
    }

    /**
     * @param reader
     * @param artfact
     * @return
     * @throws XMLStreamException
     */
    private static String getContent(XMLStreamReader reader) throws XMLStreamException {
        int event = 0;
        String result = null;
        while (reader.hasNext() && XMLStreamConstants.END_ELEMENT != event) {
            event = reader.next();
            if (XMLStreamConstants.CHARACTERS == event) {
                result = reader.getText().trim();
            }
        }
        return result;
    }

    public static Artifact loadParentXmlFile(File conf) throws FileNotFoundException, XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(conf));
        while (reader.hasNext()) {
            try {
                int event = reader.next();
                if (XMLStreamConstants.START_ELEMENT == event && Constants.PARENT.equals(reader.getLocalName())) {
                    return loadParent(reader);

                }
            } catch (Exception e) {
                // On error parsing, omit
                return null;
            }
        }
        return null;
    }

    public static Map<String, String> loadVarsXmlFile(File conf) throws FileNotFoundException, XMLStreamException {
        HashMap<String, String> prop = new HashMap<String, String>();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(conf));
        while (reader.hasNext()) {
            int event = reader.next();
            if (XMLStreamConstants.START_ELEMENT == event && Constants.PROPERTIES.equals(reader.getLocalName())) {
                prop.putAll(loadProperties(reader));
                break;
            }
            if (XMLStreamConstants.END_ELEMENT == event && Constants.PROPERTIES.equals(reader.getLocalName())) {
                return prop;
            }
        }
        return prop;
    }

    private static Map<String, String> loadProperties(XMLStreamReader reader) throws XMLStreamException {
        HashMap<String, String> prop = new HashMap<String, String>();
        while (reader.hasNext()) {
            int event = reader.next();
            if (XMLStreamConstants.START_ELEMENT == event) {
                prop.put(reader.getLocalName(), getContent(reader));
            }

            if (XMLStreamConstants.END_ELEMENT == event && Constants.PROPERTIES.equals(reader.getLocalName())) {
                return prop;
            }
        }
        return prop;
    }

}