package ro.clockworks.urlsh;

import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MappingManager {
    private static final Logger logger = LoggerFactory.getLogger(MappingManager.class);

    private static final String URLS_NAME = "urls";
    private static final String PROPS_NAME = "data";
    private static final String INDEX_NAME = "allocated";

    private final MVStore store;
    private final MVMap<String, String> mvMap;
    private final MVMap<String, Long> mvProps;

    public MappingManager(Main params) {
        store = new MVStore.Builder()
                .autoCommitDisabled()
                .fileName(params.getMvStore())
                .cacheSize(params.getCacheMB())
                .open();
        mvMap = store.openMap(URLS_NAME);
        mvProps = store.openMap(PROPS_NAME);
        if (!mvProps.containsKey(INDEX_NAME)) {
            mvProps.put(INDEX_NAME, 26L);
            store.commit();
        }
        logger.info("Opened store with {} active URLs and {} allocated URLs", mvMap.size(), mvProps.get(INDEX_NAME));
    }

    public String getMapping(String urlid) {
        return mvMap.get(urlid);
    }

    public String createMapping(String url) {
        String urlid = incrementUrlId();
        mvMap.put(urlid, url);
        store.commit();
        return urlid;
    }

    public void close() {
        store.commit();
        store.close();
    }

    private String incrementUrlId() {
        Long index = mvProps.get(INDEX_NAME);
        mvProps.put(INDEX_NAME, index + 1L);
        char[] str = Long.toString(index, 26).toCharArray();
        int strlen = str.length;
        for (int i = 0; i < strlen; i++) {
            str[i] += str[i] > '9' ? 10 : 49;
        }
        return new String(str);
    }
}
