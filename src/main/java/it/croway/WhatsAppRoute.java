package it.croway;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.DataType;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class WhatsAppRoute extends RouteBuilder {

	@Override
	public void configure() {
		transformer().scan("it.croway");

		from("webhook:whatsapp:{{camel.component.whatsapp.phone-number-id}}")
				.log("${body}")
				// A lot of events are received by the webhook, in this case, we want to choose only the ones that contain a message
				.choice().when().jsonpath("$.entry[0].changes[0].value.messages", true)
					// We will use this variable in the transformer to retrieve the recipient phone number
					.setVariable("CamelWhatsappPhoneNumber", jsonpath("$.entry[0].changes[0].value.contacts[0].wa_id"))
					// The body is used in the transformer
					.setBody(jsonpath("$.entry[0].changes[0].value.messages[0].text.body"))
					.transform(new DataType("whatsapp-text-message"))
					// reply to the number that started the conversation
					.to("whatsapp:{{camel.component.whatsapp.phone-number-id}}")
				.end();
	}

}
