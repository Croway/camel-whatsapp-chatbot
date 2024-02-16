package it.croway;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.camel.TypeConverters;
import org.apache.camel.component.whatsapp.model.TextMessage;
import org.apache.camel.component.whatsapp.model.TextMessageRequest;
import org.springframework.stereotype.Component;

@Converter(generateLoader = true)
@Component
public class WhatsAppTypeConverters implements TypeConverters {

	/**
	 * Create an object that can be serialized to WhatsApp APIs, a variable CamelWhatsappPhoneNumber is expected
	 * as well as a body containing the String message
	 */
	@Converter
	public static TextMessageRequest toTextMessageRequest(String message, Exchange exchange) {
		String phoneNumber = exchange.getVariable("CamelWhatsappPhoneNumber", String.class);

		TextMessageRequest responseMessage = new TextMessageRequest();
		responseMessage.setTo(phoneNumber);
		responseMessage.setText(new TextMessage());
		responseMessage.getText().setBody(message);

		return responseMessage;
	}
}
