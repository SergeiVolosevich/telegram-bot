package by.resliv.traveladvisor.util;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.MessageFormat;
import java.util.StringJoiner;

public final class RequestUtil {

    private static final String PAGE = "page";
    private static final String SIZE = "size";
    private static final String REL_NEXT = "next";
    private static final String REL_PREV = "prev";
    private static final String REL_FIRST = "first";
    private static final String REL_LAST = "last";
    private static final String X_TOTAL_COUNT_HEADER = "X-Total-Count";
    private static final String LINK_HEADER_TEMPLATE = "<{0}>; rel=\"{1}\"";

    private RequestUtil() {
    }

    public static URI buildLocationURL(UriComponentsBuilder ucb, String partPath, String locationPartPath) {
        return ucb.path("/")
                .path(partPath)
                .path("/")
                .path(locationPartPath)
                .build()
                .toUri();
    }

    public static void addTotalCountHeader(HttpHeaders headers, String headerValue) {
        headers.add(X_TOTAL_COUNT_HEADER, headerValue);
    }

    public static void addLinkHeader(UriComponentsBuilder uriBuilder, HttpHeaders headers, String rootPath,
                                     Page<?> page) {
        uriBuilder.path(rootPath);
        final StringJoiner linkHeader = new StringJoiner(", ");
        if (page.hasNext()) {
            final String uriForNextPage = constructNextPageUri(uriBuilder, page.getNumber(), page.getSize());
            linkHeader.add(createLinkHeader(uriForNextPage, REL_NEXT));
        }
        if (page.hasPrevious()) {
            final String uriForPrevPage = constructPrevPageUri(uriBuilder, page.getNumber(), page.getSize());
            linkHeader.add(createLinkHeader(uriForPrevPage, REL_PREV));
        }
        if (!page.isFirst()) {
            final String uriForFirstPage = constructFirstPageUri(uriBuilder, page.getSize());
            linkHeader.add(createLinkHeader(uriForFirstPage, REL_FIRST));
        }
        if (!page.isLast()) {
            final String uriForLastPage = constructLastPageUri(uriBuilder, page.getTotalPages(), page.getSize());
            linkHeader.add(createLinkHeader(uriForLastPage, REL_LAST));
        }
        if (linkHeader.length() > 0) {
            headers.add(HttpHeaders.LINK, linkHeader.toString());
        }
    }

    private static String createLinkHeader(String uri, String rel) {
        return MessageFormat.format(LINK_HEADER_TEMPLATE, uri, rel);
    }

    private static String constructNextPageUri(UriComponentsBuilder uriBuilder, int page, int size) {
        return uriBuilder.replaceQueryParam(PAGE, page + 1)
                .replaceQueryParam(SIZE, size)
                .build()
                .encode()
                .toUriString();
    }

    private static String constructPrevPageUri(final UriComponentsBuilder uriBuilder, int page, int size) {
        return uriBuilder.replaceQueryParam(PAGE, page - 1)
                .replaceQueryParam(SIZE, size)
                .build()
                .encode()
                .toUriString();
    }

    private static String constructFirstPageUri(final UriComponentsBuilder uriBuilder, int size) {
        return uriBuilder.replaceQueryParam(PAGE, 0)
                .replaceQueryParam(SIZE, size)
                .build()
                .encode()
                .toUriString();
    }

    private static String constructLastPageUri(final UriComponentsBuilder uriBuilder, int totalPages, int size) {
        return uriBuilder.replaceQueryParam(PAGE, totalPages - 1)
                .replaceQueryParam(SIZE, size)
                .build()
                .encode()
                .toUriString();
    }
}
