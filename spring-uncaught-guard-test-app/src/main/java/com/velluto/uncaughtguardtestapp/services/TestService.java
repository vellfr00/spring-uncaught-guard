package com.velluto.uncaughtguardtestapp.services;

import com.velluto.uncaughtguardtestapp.exceptions.TestKnownException;
import com.velluto.uncaughtguardtestapp.models.TestRequestDTO;
import com.velluto.uncaughtguardtestapp.models.TestResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class TestService {
    private void canThrow(boolean throwKnown) throws TestKnownException {
        if (throwKnown)
            throw new TestKnownException("Query param set to throw known exception");
    }

    public TestResponseDTO handlePostTest(
            @PathVariable Integer testId,
            @RequestParam boolean throwKnown,
            @RequestBody TestRequestDTO body)
            throws TestKnownException {
        canThrow(throwKnown);

        TestResponseDTO responseDTO = new TestResponseDTO();
        responseDTO.setData(body.getData().toLowerCase());
        return responseDTO;
    }
}
