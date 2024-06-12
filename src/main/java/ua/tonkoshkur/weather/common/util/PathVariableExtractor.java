package ua.tonkoshkur.weather.common.util;

import ua.tonkoshkur.weather.common.exception.BadRequestException;

public final class PathVariableExtractor {
    private PathVariableExtractor() {
    }

    public static String extract(String path) throws BadRequestException {
        if (path != null) {
            String variable = path.substring(1);

            if (!variable.isBlank()) {
                return variable;
            }
        }

        throw new BadRequestException("Path variable is missing");
    }
}
