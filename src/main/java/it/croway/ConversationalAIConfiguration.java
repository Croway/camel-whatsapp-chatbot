package it.croway;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

@Configuration
public class ConversationalAIConfiguration {

	@Bean
	ConversationalAIAgent customerSupportAgent(ChatLanguageModel chatLanguageModel,
											   ContentRetriever contentRetriever) {
		return AiServices.builder(ConversationalAIAgent.class)
				.chatLanguageModel(chatLanguageModel)
				.chatMemory(MessageWindowChatMemory.withMaxMessages(20))
				.contentRetriever(contentRetriever)
				.build();
	}

	@Bean
	ContentRetriever contentRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {

		// You will need to adjust these parameters to find the optimal setting, which will depend on two main factors:
		// - The nature of your data
		// - The embedding model you are using
		int maxResults = 1;
		double minScore = 0.6;

		return EmbeddingStoreContentRetriever.builder()
				.embeddingStore(embeddingStore)
				.embeddingModel(embeddingModel)
				.maxResults(maxResults)
				.minScore(minScore)
				.build();
	}

	@Bean
	EmbeddingModel embeddingModel() {
		return new AllMiniLmL6V2EmbeddingModel();
	}

	@Bean
	EmbeddingStore<TextSegment> embeddingStore(EmbeddingModel embeddingModel, ResourceLoader resourceLoader) throws IOException {

		// Normally, you would already have your embedding store filled with your data.
		// However, for the purpose of this demonstration, we will:

		// 1. Create an in-memory embedding store
		EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

		// camel-contributing is copied and pasted from https://camel.apache.org/camel-core/contributing/
		Resource resource = resourceLoader.getResource("classpath:camel-contributing.txt");
		Document document = loadDocument(resource.getFile().toPath(), new TextDocumentParser());

		// 3. Split the document into segments 100 tokens each
		// 4. Convert segments into embeddings
		// 5. Store embeddings into embedding store
		// All this can be done manually, but we will use EmbeddingStoreIngestor to automate this:
		DocumentSplitter documentSplitter = DocumentSplitters.recursive(100, 0, new OpenAiTokenizer(GPT_3_5_TURBO));
		EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
				.documentSplitter(documentSplitter)
				.embeddingModel(embeddingModel)
				.embeddingStore(embeddingStore)
				.build();
		ingestor.ingest(document);

		return embeddingStore;
	}
}
