
package com.taskmanager.api.focus.controller;

import com.taskmanager.api.focus.dto.FocusRecommendation;
import com.taskmanager.api.focus.dto.FocusRequest;
import com.taskmanager.api.focus.service.FocusService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/focus")
public class FocusController {

    private final FocusService focusService;

    public FocusController(FocusService focusService) {
        this.focusService = focusService;
    }

    @PostMapping("/recommendations")
    public List<FocusRecommendation> getRecommendations(
            @Valid @RequestBody FocusRequest request
    ) {
        return focusService.getRecommendations(request);
    }
}
