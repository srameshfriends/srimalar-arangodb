package srimalar.arangodb.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

public abstract class AQLQueryMap {
    private static final Logger log = LoggerFactory.getLogger(AQLQueryMap.class);
    private static final String START_CHAR = "//--<", END_CHAR = "//-->", ESCAPE_CHAR = "//--!";
    private static Map<String, AQLInfo> namedQueryMap;
    private static Map<String, List<String>> queryFileNamesMap;

    public static void set(Map<String, AQLInfo> queryInfoMap, Map<String, List<String>> fileNamesMap) {
        namedQueryMap = Collections.unmodifiableMap(queryInfoMap);
        queryFileNamesMap = Collections.unmodifiableMap(fileNamesMap);
    }

    public static void set(List<Resource> resources) {
        try {
            Map<String, List<String>> fileNamesMap = new HashMap<>();
            Map<String, AQLInfo> aqlINfoMap = new HashMap<>();
            for (Resource resource : resources) {
                log.info("Loading named queries from : " + resource.getFile());
                try (Stream<String> stream = Files.lines(resource.getFile().toPath())) {
                    List<String> nameList = extractQuery(aqlINfoMap, stream.iterator(), resource.getFilename());
                    fileNamesMap.put(resource.getFilename(), nameList);
                }
            }
            set(aqlINfoMap, fileNamesMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void set(Map<String, List<String>> resourceMap) {
        Map<String, List<String>> fileNamesMap = new HashMap<>();
        Map<String, AQLInfo> aqlINfoMap = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : resourceMap.entrySet()) {
            log.info("Loading named queries from : " + entry.getKey());
            List<String> nameList = extractQuery(aqlINfoMap, entry.getValue().listIterator(), entry.getKey());
            fileNamesMap.put(entry.getKey(), nameList);
        }
        set(aqlINfoMap, fileNamesMap);
    }

    public static Set<String> getFileNames() {
        return queryFileNamesMap.keySet();
    }

    public static String getQuery(String name) {
        if (!namedQueryMap.containsKey(name)) {
            throw new NullPointerException("ERROR : Named query not found " + name);
        }
        return namedQueryMap.get(name).getQuery();
    }

    public static String getDescription(String name) {
        if (!namedQueryMap.containsKey(name)) {
            throw new NullPointerException("ERROR : Named query description not found " + name);
        }
        return namedQueryMap.get(name).getDescription();
    }

    public static String[] getQueries(String... names) {
        String[] result = new String[names.length];
        for (int idx = 0; idx < names.length; idx++) {
            AQLInfo model = namedQueryMap.get(names[idx]);
            if (model == null) {
                throw new NullPointerException("Named query not found : " + names[idx]);
            }
            result[idx] = model.getQuery();
        }
        return result;
    }

    public static String[] getDescriptions(String... names) {
        String[] result = new String[names.length];
        for (int idx = 0; idx < names.length; idx++) {
            AQLInfo model = namedQueryMap.get(names[idx]);
            if (model == null) {
                throw new NullPointerException("Named query description not found : " + names[idx]);
            }
            result[idx] = model.getDescription();
        }
        return result;
    }

    public static String getQuery(String[] names) {
        String[] result = getQueries(names);
        StringBuilder builder = new StringBuilder();
        for (String s : result) {
            builder.append(s).append(" ");
        }
        return builder.toString();
    }

    public static String getDescription(String[] names) {
        String[] result = getDescriptions(names);
        StringBuilder builder = new StringBuilder();
        for (String s : result) {
            builder.append(s).append(" ");
        }
        return builder.toString();
    }

    private static List<String> extractQuery(Map<String, AQLInfo> map, Iterator<String> iterator, String fileName) {
        List<String> nameList = new ArrayList<>();
        List<String> queryLines = new ArrayList<>();
        int lineNumber = 0;
        AQLInfo sqlModel = null;
        while (iterator.hasNext()) {
            String line = iterator.next().trim();
            lineNumber += 1;
            if (line.isEmpty()) {
                continue;
            }
            if (line.startsWith(START_CHAR)) {
                String text = line.replaceFirst(START_CHAR, "").trim();
                if (text.isEmpty()) {
                    throw new IllegalArgumentException("ERROR : Query name should not be empty at the line number : "
                            + lineNumber + " \t " + fileName);
                }
                String[] nameDesc = getQueryNameDescription(text);
                if (map.containsKey(nameDesc[0])) {
                    throw new IllegalArgumentException("ERROR : Duplicate query name at the line number : "
                            + lineNumber + " \t " + fileName);
                }
                if (sqlModel != null) {
                    throw new IllegalArgumentException("ERROR : " + sqlModel.getName() +
                            ", Already started before end next query started at the line number : "
                            + lineNumber + " \t " + fileName);
                }
                sqlModel = new AQLInfo(nameDesc[0], nameDesc[1], fileName);
                queryLines = new ArrayList<>();
            } else if (line.startsWith(END_CHAR) && sqlModel != null) {
                if (!queryLines.isEmpty()) {
                    StringBuilder builder = new StringBuilder();
                    queryLines.forEach(str -> builder.append(str).append(" "));
                    map.put(sqlModel.getName(), sqlModel.clone(builder.toString()));
                    nameList.add(sqlModel.getName());
                    sqlModel = null;
                    queryLines = new ArrayList<>();
                } else {
                    throw new IllegalArgumentException("ERROR : " + sqlModel.getName() +
                            ", empty query not allowed, " + lineNumber + " \t " + fileName);
                }
            } else if (!line.startsWith(ESCAPE_CHAR) && sqlModel != null) {
                queryLines.add(line);
            }
        }
        return nameList;
    }

    private static String[] getQueryNameDescription(String text) {
        int index = text.indexOf(",");
        if (0 > index) {
            return new String[]{text.trim(), ""};
        }
        String name = text.substring(0, index), description = text.substring(index + 1);
        return new String[]{name.trim(), description.trim()};
    }

    public List<String> getNamedQueryList(String fileName) {
        if (!queryFileNamesMap.containsKey(fileName)) {
            throw new NullPointerException("ERROR : Named query file not found " + fileName);
        }
        return queryFileNamesMap.get(fileName);
    }
}
