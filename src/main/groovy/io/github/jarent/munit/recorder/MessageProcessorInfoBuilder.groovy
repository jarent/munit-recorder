package io.github.jarent.munit.recorder

import static io.github.jarent.munit.recorder.MunitRecorderHelper.*

import java.util.Map

import org.mule.api.AnnotatedObject
import org.mule.api.construct.FlowConstructAware


class MessageProcessorInfoBuilder {
	
	private MessageProcessorInfo mpInfo = new MessageProcessorInfo();
	
	
	public MessageProcessorInfoBuilder fromMessageProcessor(def mp) {		
		if (mp instanceof AnnotatedObject) {
			AnnotatedObject annoted = mp;
			fillMessageProcessorInfo(annoted)
		} else if (mp instanceof FlowConstructAware) {
			FlowConstructAware fcAware = mp
			fillMessageProcessorInfo(fcAware)
		} else {
			throw new IllegalStateException("No annotations - " + mpNotification.getProcessor().getClass().getName());
		}
		return this
	}
	
	public MessageProcessorInfoBuilder withPayload(def payload) {
		mpInfo.payload = payload
		return this
	}
	
	public MessageProcessorInfoBuilder withVariables(List variables) {
		mpInfo.variables = variables
		return this
	}
	
	public MessageProcessorInfo build() {
		return mpInfo
	}
	
	
	
	private void fillMessageProcessorInfo(AnnotatedObject annoted) {
		mpInfo.docName = annoted.getAnnotations()[DOC_NAME];
		
		String sourceElement = annoted.getAnnotations()[SOURCE_ELEMENT].toString();
		if (mpInfo.docName == null) {
			mpInfo.docName = getDocName(sourceElement)
		}
				
		def elementNameAndNamespace = getElementNameAndNamespace(sourceElement)
						
		mpInfo.elementNamespace = elementNameAndNamespace.namespace
		mpInfo.elementName = elementNameAndNamespace.name
		
		if (mpInfo.docName == null) {
			mpInfo.docName = mpInfo.elementNamespace + "_" + mpInfo.elementName
		}
	}
	
	private void fillMessageProcessorInfo(FlowConstructAware fcAware) {
		mpInfo.docName = fcAware?.'CGLIB$CALLBACK_0'.getAttributes().'doc:name'			
					
		def elementNameAndNamespace = getElementNameAndNamespaceFromFlow(fcAware?.'CGLIB$CALLBACK_0'.lineNumber as Integer, fcAware.flowConstruct.getAnnotations())
		
		mpInfo.elementNamespace = elementNameAndNamespace.namespace
		mpInfo.elementName = elementNameAndNamespace.name
	}
	

}
