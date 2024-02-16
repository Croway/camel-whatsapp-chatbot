package it.croway;

import org.apache.camel.Message;
import org.apache.camel.component.whatsapp.model.TextMessage;
import org.apache.camel.component.whatsapp.model.TextMessageRequest;
import org.apache.camel.spi.DataType;
import org.apache.camel.spi.DataTypeTransformer;
import org.apache.camel.spi.Transformer;
import org.springframework.stereotype.Component;

/**
 * transform a question in String format, invoke the LLM to generate a response, and finally
 * create  TextMessageRequest object needed by WhatsApp.
 */
@DataTypeTransformer(name = "whatsapp-text-message")
@Component
public class TextMessageTransformer extends Transformer {

	private ConversationalAIAgent agent;

	public TextMessageTransformer(ConversationalAIAgent agent) {
		this.agent = agent;
	}

	@Override
	public void transform(Message message, DataType from, DataType to) {
		// This variable was stored in the Route.
		String phoneNumber = message.getExchange().getVariable("CamelWhatsappPhoneNumber", String.class);

		TextMessageRequest responseMessage = new TextMessageRequest();
		responseMessage.setTo(phoneNumber);
		responseMessage.setText(new TextMessage());
		responseMessage.getText().setBody(agent.chat(message.getBody(String.class)));

		message.setBody(responseMessage);
	}
}
