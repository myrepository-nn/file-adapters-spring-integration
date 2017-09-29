package com.nishant.spring.integration.nodsl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.transformer.HeaderEnricher;
import org.springframework.integration.transformer.support.HeaderValueMessageProcessor;
import org.springframework.integration.transformer.support.StaticHeaderValueMessageProcessor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import com.fasterxml.jackson.core.JsonProcessingException;
@SpringBootApplication
public class SpringIntegrationClientApplication {

	public static void main(String[] args) throws JsonProcessingException {
		ConfigurableApplicationContext cxt=SpringApplication.run(SpringIntegrationClientApplication.class, args);
		Sender sn=cxt.getBean(Sender.class);
		Scanner scn=new Scanner(System.in);
		while(scn.hasNext()) {
			String msgtosend=scn.next();
			sn.send(msgtosend);	
		}
		scn.close();
	}

	@MessagingGateway(defaultRequestChannel="messageChannel",errorChannel="errorChannel")
	public interface Sender{
		public void send(Object msg);
	}

	@Bean
	public MessageChannel messageChannel() {
		DirectChannel dc= new DirectChannel();
		return dc;
	}
	@Transformer(inputChannel="messageChannel",outputChannel="messageOutChannel")
	@Bean
	public HeaderEnricher header() {
		Map<String,HeaderValueMessageProcessor<String>> mp=new HashMap<>();
		mp.put(FileHeaders.FILENAME, new StaticHeaderValueMessageProcessor<String>("nish.txt"));
		HeaderEnricher he=new HeaderEnricher(mp);
		return he;
	}
	@Bean
	public MessageChannel messageOutChannel() {
		DirectChannel dc= new DirectChannel();
		return dc;
	}
	@Bean
	@ServiceActivator(inputChannel="messageOutChannel")
	public MessageHandler fileWriter() {
		FileWritingMessageHandler fwmh=new FileWritingMessageHandler(new File("C://Users//SHISHER//Desktop//file"));
		fwmh.setAppendNewLine(true);
		fwmh.setFileExistsMode(FileExistsMode.APPEND);
		fwmh.setExpectReply(false);
		return fwmh;
	}

	@Bean
	public MessageChannel errorChannel() {
		return  new DirectChannel();
	}
	@Bean
	@ServiceActivator(inputChannel="errorChannel")
	public MessageHandler errorChannelhandle() {
		return new MessageHandler() {

			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				System.out.println("errorChannel="+message);

			}
		};

	}

}
