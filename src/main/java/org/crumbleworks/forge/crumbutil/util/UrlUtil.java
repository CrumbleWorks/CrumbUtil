package org.crumbleworks.forge.crumbutil.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility to validate URLs
 * 
 * @author Michael Stocker
 * @since 1.0
 */
public final class UrlUtil {

    private static final Logger logger = LoggerFactory.getLogger(UrlUtil.class);

    /* CHARACTER LISTS */
    // According to http://www.ietf.org/rfc/rfc3986.txt
    public static final String URL_UNRESERVED_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String URL_UNRESERVED_SPECIAL_CHARS = "-._~";
    public static final String URL_UNRESERVED = URL_UNRESERVED_CHARS + URL_UNRESERVED_SPECIAL_CHARS;
    public static final String URL_RESERVED_GEN_DELIMS = ":/?#[]@";
    public static final String URL_RESERVED_SUB_DELIMS = "!$&'()*+,;=";
    public static final String URL_CHAR_ENCODING_SIGN = "%";

    public static final String URL_ALLOWED_CHARS = URL_UNRESERVED + URL_RESERVED_GEN_DELIMS + URL_RESERVED_SUB_DELIMS + URL_CHAR_ENCODING_SIGN;

    /* CHARACTER LISTS REGEXED */
    public static final String REGEX_URL_UNRESERVED = "[\\w.~-]";
    public static final String RUU = REGEX_URL_UNRESERVED;

    public static final String REGEX_HEX_ENCODED_CHAR = "%[\\p{XDigit}]{2}";
    public static final String RHD = REGEX_HEX_ENCODED_CHAR;

    public static final String REGEX_PATH_CHARACTERS = "[\\w.~\\-!$&'()*+,;=:@]";

    public static final String REGEX_SCHEME = "[A-Za-z][A-Za-z\\d+.-]*:"; // Also called 'protocol'
    public static final String REGEX_AUTHORATIVE_DECLARATION = "/{2}";
    public static final String REGEX_USERINFO = "(?:" + RUU + "|" + RHD + ")+(?::(?:" + RUU + "|" + RHD + ")+)?@";
    public static final String REGEX_DOMAIN = "(?:[\\w](?:[-\\w]*[\\w])?\\.){1,126}[\\w](?:[-\\w]*[\\w])?\\.?";
    public static final String REGEX_PORT = ":[\\d]+";
    public static final String REGEX_PATH = "/(?:" + REGEX_PATH_CHARACTERS + "|" + RHD + ")*";
    public static final String REGEX_QUERY = "\\?(?:" + RUU + "+(?:=(?:[\\w.~+-]|" + RHD + ")+)?)(?:[&|;]" + RUU + "+(?:=(?:[\\w.~+-]|" + RHD + ")+)?)*";
    // FRAGMENTs don't need to be parsed as they won't be sent to the server anyways

    public static final String REGEX_URL = "(?:" + REGEX_SCHEME + REGEX_AUTHORATIVE_DECLARATION + ")?(?:" + REGEX_USERINFO + ")?" + REGEX_DOMAIN + "(?:" + REGEX_PORT + ")?(?:" + REGEX_PATH + ")*(?:" + REGEX_QUERY + ")?";
    public static final String REGEX_HAS_SCHEME = REGEX_SCHEME + REGEX_AUTHORATIVE_DECLARATION + ".+";

    // DEFAULTS
    public static final String DEFAULT_URL_SCHEME = "http://";

    // URL VALIDATION

    public static final boolean checkIfIsURL(final String url) {
        logger.debug("Matching URL: '{}' against URL regex. URL is {} match!", url, url.matches(REGEX_URL) ? "a" : "no");
        return (url.matches(REGEX_URL));
    }

    public static final boolean checkIfURLHasScheme(final String url) {
        logger.debug("Checking if URL: '{}' has a scheme/protocol. Url has {} scheme!", url, url.matches(REGEX_HAS_SCHEME) ? "a" : "no");
        return (url.matches(REGEX_HAS_SCHEME));
    }

    // URL MODIFYING

    public static final String prependScheme(final String url, final String scheme) {
        logger.debug("Prepending '{}' with '{}'.", url, scheme);
        return new StringBuilder(scheme).append(url).toString();
    }

    public static final String prependHttpScheme(final String url) {
        return prependScheme(url, DEFAULT_URL_SCHEME);
    }
}