package org.jeecg.modules.campusqa.rag.service;

import org.jeecg.modules.campusqa.rag.dto.RagAskRequest;
import org.jeecg.modules.campusqa.rag.dto.RagAnswerResponse;

public interface IRagService {
    RagAnswerResponse ask(RagAskRequest request);
}
