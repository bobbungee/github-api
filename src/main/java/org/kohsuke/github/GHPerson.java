package org.kohsuke.github;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Common part of {@link GHUser} and {@link GHOrganization}.
 * 
 * @author Kohsuke Kawaguchi
 */
public abstract class GHPerson {
    /*package almost final*/ GitHub root;

    protected String gravatar_id,login;

    protected int public_gist_count,public_repo_count,following_count,id;

    /**
     * Repositories that this user owns.
     */
    private transient Map<String,GHRepository> repositories;

    /**
     * Gets the repositories this user owns.
     */
    public synchronized Map<String,GHRepository> getRepositories() throws IOException {
        if (repositories==null) {
            repositories = Collections.synchronizedMap(new TreeMap<String, GHRepository>());
            for (int i=1; ; i++) {
                Map<String, GHRepository> map = root.retrieve("/repos/show/" + login + "?page=" + i, JsonRepositories.class).wrap(root);
                repositories.putAll(map);
                if (map.isEmpty())  break;
            }
        }

        return Collections.unmodifiableMap(repositories);
    }

    /**
     * Fetches the repository of the given name from GitHub, and return it.
     */
    protected GHRepository refreshRepository(String name) throws IOException {
        if (repositories==null) getRepositories(); // fetch the base first
        GHRepository r = fetchRepository(name);
        repositories.put(name,r);
        return r;
    }

    protected GHRepository fetchRepository(String name) throws IOException {
        return root.retrieve("/repos/show/" + login + '/' + name, JsonRepository.class).wrap(root);
    }

    public GHRepository getRepository(String name) throws IOException {
        return getRepositories().get(name);
    }

    /**
     * Gravatar ID of this user, like 0cb9832a01c22c083390f3c5dcb64105
     */
    public String getGravatarId() {
        return gravatar_id;
    }

    public String getLogin() {
        return login;
    }
}
