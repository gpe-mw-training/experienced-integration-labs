package org.fuse.usecase.ldap;

import org.apache.directory.server.core.api.CoreSession;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.factory.DefaultDirectoryServiceFactory;
import org.apache.directory.server.core.factory.DirectoryServiceFactory;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.store.LdifLoadFilter;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.shared.ldap.model.entry.DefaultEntry;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.entry.Modification;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.ldif.LdifEntry;
import org.apache.directory.shared.ldap.model.ldif.LdifReader;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class EmbedLDAPServer {

    private File workDir;
    private DirectoryService dirService;
    private LdapServer lServer;

    private String ldif;

    private static final Logger LOG = LoggerFactory.getLogger(EmbedLDAPServer.class);

    public static void main(String[] args) throws Exception {
        LOG.info("Start LDAP Server ...");
        EmbedLDAPServer server = new EmbedLDAPServer();
        server.setLdif("org/fuse/usecase/activemq.ldif");
        LOG.info("LDAP Server started");
    }

    public void init() throws Exception {
        DirectoryServiceFactory lFactory = new DefaultDirectoryServiceFactory();
        lFactory.init("Standalone");
        LOG.info("Factory created");

        DirectoryService lService = lFactory.getDirectoryService();
        lService.getChangeLog().setEnabled(false);
        lService.setShutdownHookEnabled(true);
        
        /* DOES NOT WORK  NOT SURE THAT WE NEED IT
         JdbmPartition partition = new JdbmPartition(lService.getSchemaManager());
         partition.setId("ActiveMQ");
         partition.setSuffixDn(new Dn("ou=ActiveMQ,ou=system"));
         lService.addPartition(partition);
         */

        lServer = new LdapServer();
        lServer.setTransports(new TcpTransport("localhost", 33389));
        lServer.setDirectoryService(lService);
        LOG.info("Server initialized");

        lService.startup();
        lServer.start();

        new OSGILdifLoader(lService.getAdminSession(), new File(ldif), null,
                EmbedLDAPServer.class.getClassLoader()).execute();
        LOG.info("LDIF data loaded");
    }

    public void destroy() {
        this.lServer.stop();
    }

    private File getResourceAsStream(String name) {
        URL url = EmbedLDAPServer.class.getResource(name);
        File f;
        try {
            f = new File(url.toURI());
        } catch (URISyntaxException e) {
            f = new File(url.getPath());
        }
        return f;
    }

    public String getLdif() {
        return ldif;
    }

    public void setLdif(String ldif) {
        this.ldif = ldif;
    }
}