package by.resliv.traveladvisor;

public final class ApplicationConstants {

    public static final String ENTITY_NOT_EXIST_TEMPLATE = "{0} with id - {1} does not exist";
    public static final String FAILED_TO_FIND_ENTITY_BY_ID = "Failed to find {} by id - {}. {} does not exist.";
    public static final String UNIQUE_KEY_CONSTRAINT_TEMPLATE = "{0} with {1} already exists";
    public static final String RESOURCE_NOT_FOUND_BY_PAGE = "Resource on page {0} does not exist.";
    public static final String MEDIA_TYPE_IS_NOT_SUPPORTED_TEMPLATE = "{0} media type is not supported. Supported" +
            " media types are {1}";
    public static final String PARAMETER_IS_MISSING_TEMPLATE = "{0} parameter is missing.";
    public static final String METHOD_NOT_SUPPORTED_TEMPLATE = "Method {0} not supported. Supported methods: {1}";
    public static final String NO_HANDLER_FOUND_TEMPLATE = "No handler found for {0} {1}";

    private ApplicationConstants() {
    }
}
