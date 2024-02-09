package it.croway;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.whatsapp.model.TextMessage;
import org.apache.camel.component.whatsapp.model.TextMessageRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class MySpringBootRouter extends RouteBuilder {

	@Value("${camel.component.whatsapp.phone-number-id}")
	private String phoneNumberId;

	private ConversationalAIAgent agent;

	public MySpringBootRouter(ConversationalAIAgent agent) {
		this.agent = agent;
	}

	@Override
	public void configure() {
		fromF("webhook:whatsapp:%s", phoneNumberId)
				.log("${body}")
				.choice().when().jsonpath("$.entry[0].changes[0].value.messages", true)
				.setHeader("CamelWhatsappBody").jsonpath("$.entry[0].changes[0].value.messages[0].text.body")
				.setHeader("CamelWhatsappSentMessage").jsonpath("$.entry[0].changes[0].value.contacts[0].profile.name")
				.setHeader("CamelWhatsappPhoneNumber").jsonpath("$.entry[0].changes[0].value.contacts[0].wa_id")
				.process(exchange -> {
					TextMessageRequest request = new TextMessageRequest();
					request.setTo(exchange.getIn().getHeader("CamelWhatsappPhoneNumber").toString());
					request.setText(new TextMessage());
					request.getText().setBody(
							agent.chat(exchange.getIn().getHeader("CamelWhatsappBody").toString()));
					exchange.getIn().setBody(request);
				})
				.toF("whatsapp:%s", phoneNumberId)
				.end();
	}

}
