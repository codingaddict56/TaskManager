
package com.taskmanager.api.focus.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record FocusRequest(

        @NotNull(message = "Available minutes are required")
        @Min(value = 1, message = "Available minutes must be at least 1")
        @Max(value = 1440, message = "Available minutes cannot exceed 1440")
        Integer availableMinutes

) {
}
