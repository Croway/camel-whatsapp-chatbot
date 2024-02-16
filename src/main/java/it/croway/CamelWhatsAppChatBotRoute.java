package it.croway;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.whatsapp.model.TextMessageRequest;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class CamelWhatsAppChatBotRoute extends RouteBuilder {

	private ConversationalAIAgent agent;

	public CamelWhatsAppChatBotRoute(ConversationalAIAgent agent) {
		this.agent = agent;
	}

	@Override
	public void configure() {
		from("webhook:whatsapp:{{camel.component.whatsapp.phone-number-id}}")
			.log("${body}")
			// A lot of events are received by the webhook, in this case, we want to choose only the ones that contain a message
			.choice().when().jsonpath("$.entry[0].changes[0].value.messages", true)
				// We will use this variable in the transformer to retrieve the recipient phone number
				.setVariable("CamelWhatsappPhoneNumber", jsonpath("$.entry[0].changes[0].value.contacts[0].wa_id"))
				// The body is used as input String in ConversationalAIAgent.chat(String)
				.setBody(jsonpath("$.entry[0].changes[0].value.messages[0].text.body"))
				// Invoke the LLM
				.bean(agent)
				.convertBodyTo(TextMessageRequest.class)
				// reply to the number that started the conversation
				.to("whatsapp:{{camel.component.whatsapp.phone-number-id}}")
			.end();
	}
}
