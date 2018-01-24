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
public final class Dconf {
    private static Dconf dconf;

    /**
     * @return
     * @throws Throwable
     */
    public static synchronized Dconf getInstance() throws Throwable {
        if (null != Dconf.dconf) {
            return Dconf.dconf;
        }
        Dconf.dconf = new Dconf();

        final XMLInputFactory factory = XMLInputFactory.newInstance();
        final XMLStreamReader reader = factory.createXMLStreamReader(Dconf.class.getResource("mapperConfig.xml").openStream());
        while (reader.hasNext()) {
            final int event = reader.next();
            if (XMLStreamConstants.START_ELEMENT == event && "repoLoader".equals(reader.getLocalName())) {
                final String className = Dconf.getContent(reader);
                Dconf.dconf.repoImpl = (IRepoProvider) Class.forName(className).newInstance();
            }
            if (XMLStreamConstants.START_ELEMENT == event && "mapper".equals(reader.getLocalName())) {
                final String className = Dconf.getContent(reader);
                Dconf.dconf.mapper.add((IMapper) Class.forName(className).newInstance());
            }
            if (XMLStreamConstants.START_ELEMENT == event && "mapperAbs".equals(reader.getLocalName())) {
                final String className = Dconf.getContent(reader);
                Dconf.dconf.mapperAbs.add((IMapper) Class.forName(className).newInstance());
            }
            if (XMLStreamConstants.START_ELEMENT == event && "notNullAnot".equals(reader.getLocalName())) {
                final String className = Dconf.getContent(reader);
                Dconf.dconf.ignoreNullAnotation.add(className);
            }
        }
        return Dconf.dconf;
    }

    /**
     * @param reader
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

    private final List<IMapper> mapper = new ArrayList<>();
    private final List<IMapper> mapperAbs = new ArrayList<>();

    private IRepoProvider repoImpl;

    private final List<String> ignoreNullAnotation = new ArrayList<>();

    private Dconf() {
        super();
    }

    public boolean containsNotNullAnot(final ArrayList<String> anotations) {
        for (final String anot : anotations) {
            if (ignoreNullAnotation.contains(anot)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     */
    public void dispose() {
        Dconf.dconf = null;
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

    /**
     * @param path
     * @return
     * @throws Throwable
     */
    public IRepoProvider getRepoNewInstance(final Path path) throws Throwable {
        Dconf.dconf.repoImpl = Dconf.getInstance().getRepoImpl().getClass().getConstructor(Path.class).newInstance(path);
        return Dconf.dconf.repoImpl;

    }

}
