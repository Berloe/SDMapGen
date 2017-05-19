package org.smapgen.dconf;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.smapgen.scl.repo.IRepoProvider;
import org.smapgen.sdm.map.mappers.common.IMapper;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public class Dconf {
    static Dconf dconf= null;
    public static synchronized Dconf getInstance () throws Throwable{
        if (null !=dconf){
            return dconf;
        }
        dconf = new Dconf();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader =  factory.createXMLStreamReader(Dconf.class.getResource("mapperConfig.xml").openStream());
        while(reader.hasNext()){
          int event = reader.next();
          if (XMLStreamConstants.START_ELEMENT==event && "repoLoader".equals(reader.getLocalName())){
              String className = getContent(reader);
              dconf.repoImpl =  (IRepoProvider) Class.forName(className).newInstance();
          }
          if (XMLStreamConstants.START_ELEMENT==event && "mapper".equals(reader.getLocalName())){
              String className = getContent(reader);
              dconf.mapper.add((IMapper)Class.forName(className).newInstance());
          }
          if (XMLStreamConstants.START_ELEMENT==event && "mapperAbs".equals(reader.getLocalName())){
              String className = getContent(reader);
              dconf.mapperAbs.add((IMapper)Class.forName(className).newInstance());
          }
        }
        return dconf;
    }
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
    List<IMapper> mapper = new ArrayList<IMapper>();
    List<IMapper> mapperAbs = new ArrayList<IMapper>();
    IRepoProvider repoImpl = null;
    private Dconf() {
        super();
    }
    
    public void dispose(){
        dconf=null;
    }
    
    /**
     * @return the mapper
     */
    public List<IMapper> getMapper() {
        return mapper;
    }
    /**
     * @return the mapperAbs
     */
    public List<IMapper> getMapperAbs() {
        return mapperAbs;
    }
    /**
     * @return the repoImpl
     */
    public IRepoProvider getRepoImpl() {
        return repoImpl;
    }
    public IRepoProvider getRepoNewInstance(Path path) throws Throwable{
        dconf.repoImpl =Dconf.getInstance().getRepoImpl().getClass().getConstructor(Path.class).newInstance(path);
        return dconf.repoImpl;
        
    }
}
