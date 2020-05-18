package org.fuse.usecase.ldap;

import org.apache.directory.server.core.api.CoreSession;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.server.protocol.shared.store.LdifLoadFilter;
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
import java.util.Collections;
import java.util.List;

public class OSGILdifLoader {

    private static final Logger LOG = LoggerFactory.getLogger(OSGILdifLoader.class);

    protected CoreSession coreSession;
    protected final ClassLoader loader;
    protected File ldif;
    private int count;
    protected final List<LdifLoadFilter> filters;

    public OSGILdifLoader(CoreSession coreSession, File ldif, List<? extends LdifLoadFilter> filters,
            ClassLoader loader) {
        this.coreSession = coreSession;
        this.ldif = ldif;
        this.loader = loader;

        if (filters == null) {
            this.filters = Collections.emptyList();
        } else {
            this.filters = Collections.unmodifiableList(filters);
        }
    }

    public int execute() {
        InputStream in = null;

        try {
            in = getLdifStream();

            for (LdifEntry ldifEntry : new LdifReader(in)) {
                Dn dn = ldifEntry.getDn();

                if (ldifEntry.isEntry()) {
                    Entry entry = ldifEntry.getEntry();
                    boolean filterAccepted = applyFilters(dn, entry);

                    if (!filterAccepted) {
                        continue;
                    }

                    try {
                        coreSession.lookup(dn);
                        LOG.info("Found {}, will not create.", dn);
                    } catch (Exception e) {
                        try {
                            coreSession.add(new DefaultEntry(
                                    coreSession.getDirectoryService().getSchemaManager(), entry));
                            count++;
                            LOG.info("Created {}.", dn);
                        } catch (LdapException e1) {
                            LOG.info("Could not create entry " + entry, e1);
                        }
                    }
                } else {
                    //modify
                    List<Modification> items = ldifEntry.getModifications();

                    try {
                        coreSession.modify(dn, items);
                        LOG.info("Modified: " + dn + " with modificationItems: " + items);
                    } catch (LdapException e) {
                        LOG.info("Could not modify: " + dn + " with modificationItems: " + items, e);
                    }
                }
            }
        } catch (FileNotFoundException fnfe) {
            LOG.error(I18n.err(I18n.ERR_173));
        } catch (Exception ioe) {
            LOG.error(I18n.err(I18n.ERR_174), ioe);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    LOG.error(I18n.err(I18n.ERR_175), e);
                }
            }
        }

        return count;
    }

    private boolean applyFilters(Dn dn, Entry entry) {
        boolean accept = true;
        final int limit = filters.size();

        if (limit == 0) {
            return true;
        } // don't waste time with loop

        for (int ii = 0; ii < limit; ii++) {
            try {
                accept &= (filters.get(ii)).filter(ldif, dn, entry, coreSession);
            } catch (LdapException e) {
                LOG.warn("filter " + filters.get(ii) + " was bypassed due to failures", e);
            }

            // early bypass if entry is rejected
            if (!accept) {
                return false;
            }
        }
        return true;
    }

    private InputStream getLdifStream() throws FileNotFoundException {
        InputStream in;

        if (ldif.exists()) {
            in = new FileInputStream(ldif);
        } else {
            if (loader != null && (in = loader.getResourceAsStream(ldif.getPath())) != null) {
                return in;
            }

            // if file not on system see if something is bundled with the jar ...
            in = getClass().getResourceAsStream(ldif.getName());
            if (in != null) {
                return in;
            }

            in = ClassLoader.getSystemResourceAsStream(ldif.getName());
            if (in != null) {
                return in;
            }

            throw new FileNotFoundException(I18n.err(I18n.ERR_173));
        }

        return in;
    }

}