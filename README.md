## Conversational AI with Camel

This example shows how to integrate a conversational AI with Camel capabilities. In particular, we'll leverage the camel-whatsapp component, so that the conversational AI can be easily integrated with whatsapp.

## Workflow

In this example langchain4j is integrated with openapi and the Apache Camel contributing guide is ingested as an embedding, once the application is started a conversation with the model can be done via WhatsApp.

## How to run

The hardest part is WhatsApp and OpenAI configuration, but the following guides can be followed:
* [Camel WhatsApp Component](https://camel.apache.org/components/4.0.x/whatsapp-component.html)
* [langchain4j Getting Started](https://github.com/langchain4j#getting-started)
* [Webhook Configuration](https://developers.facebook.com/docs/whatsapp/cloud-api/guides/set-up-webhooks)
* To test the webhook locally [ngrok](https://ngrok.com/) can be used

and the application.properties file has to be updated accordingly.

Once everything is set up execute the following command

`mvn package spring-boot:run`

and initiate the conversation on WhatsApp