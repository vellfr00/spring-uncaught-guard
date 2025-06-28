package com.velluto.uncaughtguardtestapp.controllers;

import com.velluto.uncaughtguardtestapp.models.TestRequestDTO;
import com.velluto.uncaughtguardtestapp.models.TestResponseDTO;
import com.velluto.uncaughtguardtestapp.services.TestService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {
    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @PostMapping("/{testId}")
    public TestResponseDTO postTestData(
            @PathVariable(name = "testId") Integer testId,
            @RequestParam(name = "throwKnown", required = false) boolean throwKnown,
            @RequestBody TestRequestDTO body) {
        return testService.handlePostTest(testId, throwKnown, body);
    }
}
