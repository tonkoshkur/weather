package ua.tonkoshkur.weather.common.util;

import ua.tonkoshkur.weather.common.exception.BadRequestException;

import java.util.function.Function;

public final class PathVariableExtractor {
    private PathVariableExtractor() {
    }

    public static <T> T extract(String path, Function<String, T> mapper) throws BadRequestException {
        String variable = extract(path);
        try {
            return mapper.apply(variable);
        } catch (Exception e) {
            throw new BadRequestException("Invalid path variable");
        }
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
