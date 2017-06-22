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
public class LoadPomDeps {

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
          if (XMLStreamConstants.START_ELEMENT==event && "dependencies".equals(reader.getLocalName())){
              artifacts.addAll(dependencyBlock(reader));
          }
          if (XMLStreamConstants.START_ELEMENT==event && "dependencyManagement".equals(reader.getLocalName())){
              artifacts.addAll(dependencyBlock(reader));
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
    public static ArrayList<Artifact> dependencyBlock(XMLStreamReader reader)
            throws XMLStreamException {
        int event=0;
        ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
        while(reader.hasNext()){
          if (XMLStreamConstants.START_ELEMENT==event && "dependencies".equals(reader.getLocalName())){
              artifacts.addAll(loadDependencies(reader));
              break;
          }
          if (XMLStreamConstants.END_ELEMENT==event && "dependencies".equals(reader.getLocalName())){
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
            if (XMLStreamConstants.START_ELEMENT==event && "dependency".equals(reader.getLocalName())){
                artifacts.add(loadDependency(reader));
            }
            if (XMLStreamConstants.END_ELEMENT==event && "dependencies".equals(reader.getLocalName())){
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
                    case "groupId":
                        artfact.setGroup(getContent(reader));
                        break;
                    case "artifactId":
                        artfact.setArtifact(getContent(reader));
                        break;
                    case "version":
                        artfact.setVersion(getContent(reader));
                        break;
                    case "scope":
                        String scop;
                        scop=getContent(reader);
                        if (scop== null || "".equals(scop.trim())){
                            scop="compile";
                        }
                        artfact.setScope(scop);
                        break;
                }
            }
            if (XMLStreamConstants.END_ELEMENT==event && "dependency".equals(reader.getLocalName())){
                String scop = null;
                if (artfact.getScope()== null || "".equals(artfact.getScope().trim())){
                    scop="compile";
                }
                artfact.setScope(scop);
                return artfact;
            }
        }
        String scop = null;
        if (artfact.getScope()== null || "".equals(artfact.getScope().trim())){
            scop="compile";
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
                    case "groupId":
                        artfact.setGroup(getContent(reader));
                        break;
                    case "artifactId":
                        artfact.setArtifact(getContent(reader));
                        break;
                    case "version":
                        artfact.setVersion(getContent(reader));
                        break;
                }
            }
            if (XMLStreamConstants.END_ELEMENT==event && "parent".equals(reader.getLocalName())){
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
                if (XMLStreamConstants.START_ELEMENT==event && "parent".equals(reader.getLocalName())){
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
          if (XMLStreamConstants.START_ELEMENT==event && "properties".equals(reader.getLocalName())){
              prop.putAll(loadProperties(reader));
              break;
          }
          if (XMLStreamConstants.END_ELEMENT==event && "properties".equals(reader.getLocalName())){
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
            
            if (XMLStreamConstants.END_ELEMENT==event && "properties".equals(reader.getLocalName())){
                return prop;
            }
        }
        return prop;
    }

}