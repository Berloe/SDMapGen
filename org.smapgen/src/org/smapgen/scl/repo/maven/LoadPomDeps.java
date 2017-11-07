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

    private static final String PROPERTIES = "properties";
    private static final String PARENT = "parent";
    private static final String COMPILE = "compile";
    private static final String SCOPE = "scope";
    private static final String VERSION = "version";
    private static final String ARTIFACT_ID = "artifactId";
    private static final String GROUP_ID = "groupId";
    private static final String DEPENDENCY = "dependency";
    private static final String DEPENDENCY_MANAGEMENT = "dependencyManagement";
    private static final String DEPENDENCIES = "dependencies";

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
        XMLStreamReader reader =  factory.createXMLStreamReader(new FileInputStream(conf));
        while(reader.hasNext()){
          int event = reader.next();
          if (XMLStreamConstants.START_ELEMENT==event && LoadPomDeps.DEPENDENCIES.equals(reader.getLocalName())){
              artifacts.addAll(dependencyBlock(reader,event));
          }
          if (XMLStreamConstants.START_ELEMENT==event && LoadPomDeps.DEPENDENCY_MANAGEMENT.equals(reader.getLocalName())){
              artifacts.addAll(dependencyBlock(reader,event));
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
    public static ArrayList<Artifact> dependencyBlock(XMLStreamReader reader,int ev)
            throws XMLStreamException {
        int event = ev;
        ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
        while(reader.hasNext()){
          if (XMLStreamConstants.START_ELEMENT==event && LoadPomDeps.DEPENDENCIES.equals(reader.getLocalName())){
              artifacts.addAll(loadDependencies(reader));
              break;
          }
          if (XMLStreamConstants.END_ELEMENT==event && LoadPomDeps.DEPENDENCIES.equals(reader.getLocalName())){
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
        while(reader.hasNext()){
            int event = reader.next();
            if (XMLStreamConstants.START_ELEMENT==event && LoadPomDeps.DEPENDENCY.equals(reader.getLocalName())){
                artifacts.add(loadDependency(reader));
            }
            if (XMLStreamConstants.END_ELEMENT==event && LoadPomDeps.DEPENDENCIES.equals(reader.getLocalName())){
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
        while(reader.hasNext()){
            int event = reader.next();
            if (XMLStreamConstants.START_ELEMENT==event){
                switch (reader.getLocalName()) {
                    case LoadPomDeps.GROUP_ID:
                        artfact.setGroup(getContent(reader));
                        break;
                    case LoadPomDeps.ARTIFACT_ID:
                        artfact.setArtifact(getContent(reader));
                        break;
                    case LoadPomDeps.VERSION:
                        artfact.setVersion(getContent(reader));
                        break;
                    case LoadPomDeps.SCOPE:
                        String scop;
                        scop=getContent(reader);
                        if (scop== null || scop.trim().length()==0){
                            scop=LoadPomDeps.COMPILE;
                        }
                        artfact.setScope(scop);
                        break;
                }
            }
            if (XMLStreamConstants.END_ELEMENT==event && LoadPomDeps.DEPENDENCY.equals(reader.getLocalName())){
                String scop = null;
                if (artfact.getScope()== null || artfact.getScope().trim().length()==0){
                    scop=LoadPomDeps.COMPILE;
                }
                artfact.setScope(scop);
                return artfact;
            }
        }
        String scop = null;
        if (artfact.getScope()== null || artfact.getScope().trim().length()==0){
            scop=LoadPomDeps.COMPILE;
        }
        artfact.setScope(scop);
        return artfact;     
    }
    /**
     * @param reader
     * @return
     * @throws XMLStreamException
     */
    private static Artifact loadParent(XMLStreamReader reader) throws XMLStreamException {
        Artifact artfact = new Artifact();
        while(reader.hasNext()){
            int event = reader.next();
            if (XMLStreamConstants.START_ELEMENT==event){
                switch (reader.getLocalName()) {
                    case LoadPomDeps.GROUP_ID:
                        artfact.setGroup(getContent(reader));
                        break;
                    case LoadPomDeps.ARTIFACT_ID:
                        artfact.setArtifact(getContent(reader));
                        break;
                    case LoadPomDeps.VERSION:
                        artfact.setVersion(getContent(reader));
                        break;
                }
            }
            if (XMLStreamConstants.END_ELEMENT==event && LoadPomDeps.PARENT.equals(reader.getLocalName())){
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
        int event=0;
        String result = null;
        while(reader.hasNext() && XMLStreamConstants.END_ELEMENT!= event){
            event = reader.next();
            if (XMLStreamConstants.CHARACTERS==event){
                result = reader.getText().trim();
            }
        }
        return result;
    }

    public static Artifact loadParentXmlFile(File conf) throws FileNotFoundException, XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader =  factory.createXMLStreamReader(new FileInputStream(conf));
        while(reader.hasNext()){
            try{
                int event = reader.next();
                if (XMLStreamConstants.START_ELEMENT==event && LoadPomDeps.PARENT.equals(reader.getLocalName())){
                    return loadParent(reader);

                }
            }catch(Exception e){
                // On error parsing, omit
                return null;
            }
        }
        return null;
    }

    public static Map<String,String> loadVarsXmlFile(File conf) throws FileNotFoundException, XMLStreamException  {
        HashMap<String,String> prop = new HashMap<String,String>();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader =  factory.createXMLStreamReader(new FileInputStream(conf));
        while(reader.hasNext()){
          int event = reader.next();
          if (XMLStreamConstants.START_ELEMENT==event && LoadPomDeps.PROPERTIES.equals(reader.getLocalName())){
              prop.putAll(loadProperties(reader));
              break;
          }
          if (XMLStreamConstants.END_ELEMENT==event && LoadPomDeps.PROPERTIES.equals(reader.getLocalName())){
              return prop;
          }
        }
        return prop;
    }

    private static Map<String,String> loadProperties(XMLStreamReader reader) throws XMLStreamException{
        HashMap<String,String> prop = new HashMap<String,String>();
        while(reader.hasNext()){
            int event = reader.next();
            if (XMLStreamConstants.START_ELEMENT==event){
                prop.put(reader.getLocalName(), getContent(reader));
            }
            
            if (XMLStreamConstants.END_ELEMENT==event && LoadPomDeps.PROPERTIES.equals(reader.getLocalName())){
                return prop;
            }
        }
        return prop;
    }

}