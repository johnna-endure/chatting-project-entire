package parser.url;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Pattern;

public class URLParser {
    private static final Logger logger = LogManager.getLogger(URLParser.class);
    public static boolean validateUrl(String urlFormat, String url) {
        String braceConvertedUrlFormat = Pattern.compile("\\{[\\w\\d]+\\}")
                .matcher(urlFormat)
                .replaceAll("[\\\\w\\\\d]+");
        logger.debug("[validateUrl] braceConvertedUrlFormat : {}", braceConvertedUrlFormat);
        String addBoundaryFormat = "^" + braceConvertedUrlFormat + "$";
        logger.debug("[validateUrl] addBoundaryFormat : {}", addBoundaryFormat);
        return Pattern.compile(addBoundaryFormat).matcher(url).find();
    }
}
