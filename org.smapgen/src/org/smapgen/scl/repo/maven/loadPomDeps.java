package org.smapgen.scl.repo.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class loadPomDeps {

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
              artifacts.addAll(loadDependencies(reader));
              break;
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
                        artfact.setGroup(getContent(reader,artfact));
                        break;
                    case "artifactId":
                        artfact.setArtifact(getContent(reader,artfact));
                        break;
                    case "version":
                        artfact.setVersion(getContent(reader,artfact));
                        break;
                }
            }
            if (XMLStreamConstants.END_ELEMENT==event && "dependency".equals(reader.getLocalName())){
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
    private static String getContent(XMLStreamReader reader, Artifact artfact) throws XMLStreamException {
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

}