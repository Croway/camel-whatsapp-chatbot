package it.croway;

import dev.langchain4j.service.SystemMessage;

public interface ConversationalAIAgent {

	@SystemMessage({
			"You are java developer working on 'Apache Camel' that provides Apache Camel contributing information",
	})
	String chat(String userMessage);
}
