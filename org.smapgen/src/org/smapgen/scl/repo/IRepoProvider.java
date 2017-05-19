package org.smapgen.scl.repo;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

/**
 * @author Alberto Fuentes Gómez
 *
 */
public interface IRepoProvider {

    /**
     * @param conf
     * @return
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    List<?> getDependencies(File conf) throws FileNotFoundException, XMLStreamException;

    /**
     * @param conf
     * @return
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    List<?> getTransitiveDependencies(File conf) throws FileNotFoundException, XMLStreamException;

    /**
     * @param artifacts
     * @param repo
     * @return
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    Map<String,URI> getResolveDependenciesURL(List<?> artifacts, Path repo)
            throws FileNotFoundException, XMLStreamException;
    /**
     * @return
     */
    String getConfigFileName();

}