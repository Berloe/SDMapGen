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

    private static final String ALPHANUMERIC = "[a-zA-Z0-9 ]+";

    /**
     * @param artifacts
     * @param reader
     * @param event
     * @throws XMLStreamException
     */
    public static ArrayList<Artifact> dependencyBlock(final XMLStreamReader reader, final int ev) throws XMLStreamException {
        int event = ev;
        final ArrayList<Artifact> artifacts = new ArrayList<>();
        while (reader.hasNext()) {
            if (XMLStreamConstants.START_ELEMENT == event && Constants.DEPENDENCIES.equals(reader.getLocalName())) {
                artifacts.addAll(LoadPomDeps.loadDependencies(reader));
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
     * @param artifacts
     * @param reader
     * @param event
     * @throws XMLStreamException
     */
    public static ArrayList<Artifact> excludeBlock(final XMLStreamReader reader, final int ev) throws XMLStreamException {
        int event = ev;
        final ArrayList<Artifact> artifacts = new ArrayList<>();
        while (reader.hasNext()) {
            if (XMLStreamConstants.START_ELEMENT == event && Constants.EXCLUSIONS.equals(reader.getLocalName())) {
                artifacts.addAll(LoadPomDeps.loadExclusion(reader));
                break;
            }
            if (XMLStreamConstants.END_ELEMENT == event && Constants.EXCLUSIONS.equals(reader.getLocalName())) {
                return artifacts;
            }
            event = reader.next();
        }
        return artifacts;
    }

    public static Artifact loadParentXmlFile(final File conf) throws FileNotFoundException, XMLStreamException {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        final XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(conf));
        while (reader.hasNext()) {
            try {
                final int event = reader.next();
                if (XMLStreamConstants.START_ELEMENT == event && Constants.PARENT.equals(reader.getLocalName())) {
                    return LoadPomDeps.loadParent(reader);

                }
            } catch (final Exception e) {
                // On error parsing, omit
                return null;
            }
        }
        return null;
    }

    public static Map<String, String> loadVarsXmlFile(final File conf) throws FileNotFoundException, XMLStreamException {
        final HashMap<String, String> prop = new HashMap<>();
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        final XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(conf));
        while (reader.hasNext()) {
            final int event = reader.next();
            if (XMLStreamConstants.START_ELEMENT == event && Constants.PROPERTIES.equals(reader.getLocalName())) {
                prop.putAll(LoadPomDeps.loadProperties(reader));
                break;
            }
            if (XMLStreamConstants.END_ELEMENT == event && Constants.PROPERTIES.equals(reader.getLocalName())) {
                return prop;
            }
        }
        return prop;
    }

    /**
     * @param reader
     * @param artfact
     * @return
     * @throws XMLStreamException
     */
    private static String getContent(final XMLStreamReader reader) throws XMLStreamException {
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

    /**
     * @param reader
     * @return
     * @throws XMLStreamException
     */
    private static ArrayList<Artifact> loadDependencies(final XMLStreamReader reader) throws XMLStreamException {
        final ArrayList<Artifact> artifacts = new ArrayList<>();
        while (reader.hasNext()) {
            final int event = reader.next();
            if (XMLStreamConstants.START_ELEMENT == event && Constants.DEPENDENCY.equals(reader.getLocalName())) {
                artifacts.add(LoadPomDeps.loadDependency(reader));
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
    private static Artifact loadDependency(final XMLStreamReader reader) throws XMLStreamException {
        final Artifact artfact = new Artifact();
        while (reader.hasNext()) {
            final int event = reader.next();
            if (XMLStreamConstants.START_ELEMENT == event) {
                switch (reader.getLocalName()) {
                    case Constants.GROUP_ID:
                        artfact.setGroup(LoadPomDeps.getContent(reader));
                        break;
                    case Constants.ARTIFACT_ID:
                        artfact.setArtifact(LoadPomDeps.getContent(reader));
                        break;
                    case Constants.VERSION:
                        artfact.setVersion(LoadPomDeps.getContent(reader));
                        break;
                    case Constants.SCOPE:
                        String scop = LoadPomDeps.getContent(reader);
                        if (scop == null || !scop.matches(LoadPomDeps.ALPHANUMERIC)) {
                            scop = Constants.COMPILE;
                        }
                        artfact.setScope(scop);
                        break;
                    default:
                        break;
                }
            }
            if (XMLStreamConstants.END_ELEMENT == event) {
                if (Constants.DEPENDENCY.equals(reader.getLocalName())) {
                    if (artfact.getScope() == null || artfact.getScope().matches(LoadPomDeps.ALPHANUMERIC)) {
                        final String scop = Constants.COMPILE;
                        artfact.setScope(scop);
                    }
                    return artfact;
                } else if (Constants.EXCLUSION.equals(reader.getLocalName())) {
                    if (artfact.getScope() == null || artfact.getScope().matches(LoadPomDeps.ALPHANUMERIC)) {
                        final String scop = Constants.COMPILE;
                        artfact.setScope(scop);
                    }
                    return artfact;
                }
            }

        }
        if (artfact.getScope() == null || artfact.getScope().matches(LoadPomDeps.ALPHANUMERIC)) {
            final String scop = Constants.COMPILE;
            artfact.setScope(scop);
        }
        return artfact;
    }

    /**
     * @param reader
     * @return
     * @throws XMLStreamException
     */
    private static ArrayList<Artifact> loadExclusion(final XMLStreamReader reader) throws XMLStreamException {
        final ArrayList<Artifact> artifacts = new ArrayList<>();
        while (reader.hasNext()) {
            final int event = reader.next();
            if (XMLStreamConstants.START_ELEMENT == event && Constants.EXCLUSION.equals(reader.getLocalName())) {
                artifacts.add(LoadPomDeps.loadDependency(reader));
            }
            if (XMLStreamConstants.END_ELEMENT == event && Constants.EXCLUSIONS.equals(reader.getLocalName())) {
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
    private static Artifact loadParent(final XMLStreamReader reader) throws XMLStreamException {
        final Artifact artfact = new Artifact();
        while (reader.hasNext()) {
            final int event = reader.next();
            if (XMLStreamConstants.START_ELEMENT == event) {
                switch (reader.getLocalName()) {
                    case Constants.GROUP_ID:
                        artfact.setGroup(LoadPomDeps.getContent(reader));
                        break;
                    case Constants.ARTIFACT_ID:
                        artfact.setArtifact(LoadPomDeps.getContent(reader));
                        break;
                    case Constants.VERSION:
                        artfact.setVersion(LoadPomDeps.getContent(reader));
                        break;
                    default:
                        break;
                }
            }
            if (XMLStreamConstants.END_ELEMENT == event && Constants.PARENT.equals(reader.getLocalName())) {
                return artfact;
            }
        }
        return artfact;
    }

    private static Map<String, String> loadProperties(final XMLStreamReader reader) throws XMLStreamException {
        final HashMap<String, String> prop = new HashMap<>();
        while (reader.hasNext()) {
            final int event = reader.next();
            if (XMLStreamConstants.START_ELEMENT == event) {
                prop.put(reader.getLocalName(), LoadPomDeps.getContent(reader));
            }

            if (XMLStreamConstants.END_ELEMENT == event && Constants.PROPERTIES.equals(reader.getLocalName())) {
                return prop;
            }
        }
        return prop;
    }

    /**
     * @param conf
     * @return
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    protected static ArrayList<Artifact> loadDepsXmlFile(final File conf) throws FileNotFoundException, XMLStreamException {
        final ArrayList<Artifact> artifacts = new ArrayList<>();
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        final XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(conf));
        while (reader.hasNext()) {
            final int event = reader.next();
            if (XMLStreamConstants.START_ELEMENT == event && Constants.DEPENDENCIES.equals(reader.getLocalName())) {
                artifacts.addAll(LoadPomDeps.dependencyBlock(reader, event));
            }
            if (XMLStreamConstants.START_ELEMENT == event && Constants.DEPENDENCY_MANAGEMENT.equals(reader.getLocalName())) {
                artifacts.addAll(LoadPomDeps.dependencyBlock(reader, event));
            }
        }
        return artifacts;
    }

    /**
     * @param conf
     * @return
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    protected static ArrayList<Artifact> loadExcludedXmlFile(final File conf) throws FileNotFoundException, XMLStreamException {
        final ArrayList<Artifact> artifacts = new ArrayList<>();
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        final XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(conf));
        while (reader.hasNext()) {
            final int event = reader.next();
            if (XMLStreamConstants.START_ELEMENT == event && Constants.EXCLUSIONS.equals(reader.getLocalName())) {
                artifacts.addAll(LoadPomDeps.excludeBlock(reader, event));
            }
        }
        return artifacts;
    }

    /**
     *
     */
    private LoadPomDeps() {
        super();
    }

}