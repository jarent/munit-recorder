package io.github.jarent.munit.recorder

import static io.github.jarent.munit.recorder.MunitRecorderHelper.*

import java.util.Map

import org.mule.api.AnnotatedObject
import org.mule.api.construct.FlowConstructAware
import org.mule.api.context.notification.MessageProcessorNotificationListener
import org.mule.context.notification.MessageProcessorNotification
import org.slf4j.Logger
import org.slf4j.LoggerFactory



class MunitRecorder implements MessageProcessorNotificationListener<MessageProcessorNotification>{
	
	private final Logger recorderLog = LoggerFactory.getLogger(MunitRecorder.class);
	
	private static final ACTION_MAP = ['IN':1601, 'OUT':1602]
	

	public void onNotification(MessageProcessorNotification mpNotification) {
		try {
				
				MessageProcessorInfo mpInfo = new MessageProcessorInfoBuilder()
													  .fromMessageProcessor(mpNotification.getProcessor())
													  .withPayload(mpNotification.getSource().getMessage().getPayload())
													  .withVariables(getVariables(mpNotification.getSource()))
													  .withExceptionThrown(mpNotification.exceptionThrown)
													  .build()					
				
				if (mpNotification.action == 									  
				(ACTION_MAP[mpNotification.getSource().getMuleContext().getRegistry().get("munit.recorder.action")] ?: mpNotification.action)) {
													  	
				
					def logMethodName = mpNotification.getSource().getMuleContext().getRegistry().get("munit.recorder.logMethod") ?: "logXML"
					
					def logMethod = MessageProcessorInfoLoggers.&"$logMethodName"
					
					recorderLog.info(logMethod(mpInfo))
					
				}
				
		} catch (UnsupportedOperationException e) {
			recorderLog.warn(e.getMessage())
		} catch (Exception e) {
			recorderLog.error("MunitRecorder Error", e);
		}
		
	}
	
	
	
}
