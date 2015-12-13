package ua.kh.khpi.alex_babenko.art.service;

import org.springframework.stereotype.Service;
import ua.kh.khpi.alex_babenko.art.entity.KnowledgeBase;

@Service
public class KnowledgeBaseService {

    public KnowledgeBase getKnowledgeBase() {
        KnowledgeBase knowledgeBase = new KnowledgeBase();

        return knowledgeBase;
    }

}
