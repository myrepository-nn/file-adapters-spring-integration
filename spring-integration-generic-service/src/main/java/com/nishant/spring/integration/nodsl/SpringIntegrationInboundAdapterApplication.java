package com.nishant.spring.integration.nodsl;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.file.tail.ApacheCommonsFileTailingMessageProducer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import com.fasterxml.jackson.core.JsonProcessingException;
@SpringBootApplication
public class SpringIntegrationInboundAdapterApplication {

	public static void main(String[] args) throws JsonProcessingException {
		SpringApplication.run(SpringIntegrationInboundAdapterApplication.class, args);
	}

	@Bean
	public ApacheCommonsFileTailingMessageProducer fileReader(){
		ApacheCommonsFileTailingMessageProducer ac=new ApacheCommonsFileTailingMessageProducer();
		ac.setFile(new File("C://Users//SHISHER//Desktop//file","nish.txt"));
		ac.setPollingDelay(1000);
		ac.setTailAttemptsDelay(2000);
		ac.setOutputChannel(outChannel());
		return ac;
	}

	@Bean
	public MessageChannel outChannel() {
		return  new DirectChannel();
	}

	@Bean
	@ServiceActivator(inputChannel="outChannel")
	public MessageHandler displayChannelhandle() {
		return new MessageHandler() {

			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				System.out.println("outChannel="+message.getPayload()+"----whole message----"+message);

			}
		};

	}

}
