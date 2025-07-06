package com.velluto.uncaughtguardtestapp.services;

import com.velluto.uncaughtguardtestapp.exceptions.TestKnownException;
import com.velluto.uncaughtguardtestapp.models.TestRequestDTO;
import com.velluto.uncaughtguardtestapp.models.TestResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    private void canThrow(boolean throwKnown) throws TestKnownException {
        if (throwKnown)
            throw new TestKnownException("Query param set to throw known exception");
    }

    public TestResponseDTO handlePostTest(
            Integer testId,
            boolean throwKnown,
            TestRequestDTO body,
            String aTestString
    ) throws TestKnownException {
        canThrow(throwKnown);

        TestResponseDTO responseDTO = new TestResponseDTO();
        responseDTO.setData(body.getData().toLowerCase());
        return responseDTO;
    }
}
