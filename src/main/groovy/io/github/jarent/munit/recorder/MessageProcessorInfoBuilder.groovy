package io.github.jarent.munit.recorder

import static io.github.jarent.munit.recorder.MunitRecorderHelper.*

import java.util.Map

import org.mule.api.AnnotatedObject
import org.mule.api.construct.FlowConstructAware
import org.mule.munit.common.processor.interceptor.MunitMessageProcessorInterceptor
import org.mule.munit.common.processor.interceptor.WrapperMunitMessageProcessorInterceptor


class MessageProcessorInfoBuilder {
	
	private MessageProcessorInfo mpInfo = new MessageProcessorInfo();
	
	
	public MessageProcessorInfoBuilder fromMessageProcessor(def mp) {		
		if (mp instanceof AnnotatedObject && mp.getAnnotations().size() > 0) {
			fillMessageProcessorInfoFromAnnotations((AnnotatedObject)mp)
		} else if (mp.'CGLIB$CALLBACK_0' != null) {
			if (mp.'CGLIB$CALLBACK_0' instanceof WrapperMunitMessageProcessorInterceptor) {
				return fromMessageProcessor(mp.'CGLIB$CALLBACK_0'.realMp)
			} else if (mp.'CGLIB$CALLBACK_0' instanceof MunitMessageProcessorInterceptor) {
				fillMessageProcessorInfoFromCallback(mp)
			} else  {
				throw new IllegalStateException("Unexpected CGLIB$CALLBACK_0 class: " + fcAware.'CGLIB$CALLBACK_0'.getClass().getName());
			}			
		} else {
			throw new IllegalStateException("No annotations nor CGLIB$CALLBACK_0 - " + mp.getClass().getName());
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
	
	
	
	private void fillMessageProcessorInfoFromAnnotations(AnnotatedObject annoted) {
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
	
	private void fillMessageProcessorInfoFromCallback(def mp) {
		mpInfo.docName = mp?.'CGLIB$CALLBACK_0'.getAttributes().'doc:name'		
					
		def elementNameAndNamespace = getElementNameAndNamespaceFromFile(mp?.'CGLIB$CALLBACK_0'.'lineNumber' as Integer, mp?.'CGLIB$CALLBACK_0'.getFileName() )
		
		mpInfo.elementNamespace = elementNameAndNamespace.namespace
		mpInfo.elementName = elementNameAndNamespace.name
	}
	

}
