package org.neewrobert.jumbo.adapter.in.web.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        Map<String, String> message
) {}
