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
			fillMessageProcessorInfo((AnnotatedObject)mp)
		} else if (mp instanceof FlowConstructAware) {
			FlowConstructAware fcAware = mp
			if (fcAware.'CGLIB$CALLBACK_0' != null) {
				if (fcAware.'CGLIB$CALLBACK_0' instanceof WrapperMunitMessageProcessorInterceptor) {
					return fromMessageProcessor(fcAware.'CGLIB$CALLBACK_0'.realMp)
				} else if (fcAware.'CGLIB$CALLBACK_0' instanceof MunitMessageProcessorInterceptor) {
					fillMessageProcessorInfo(fcAware)
				} else  {
					throw new IllegalStateException("Unexpected CGLIB$CALLBACK_0 class: " + fcAware.'CGLIB$CALLBACK_0'.getClass().getName());
				}
			} else {
				throw new IllegalStateException("No CGLIB$CALLBACK_0 - munit record works only in Munit test");
			}
		} else {
			throw new IllegalStateException("No annotations - " + mp.getClass().getName());
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
					
		def elementNameAndNamespace = getElementNameAndNamespaceFromFile(fcAware?.'CGLIB$CALLBACK_0'.'lineNumber' as Integer, fcAware?.'CGLIB$CALLBACK_0'.getFileName() )
		
		mpInfo.elementNamespace = elementNameAndNamespace.namespace
		mpInfo.elementName = elementNameAndNamespace.name
	}
	

}
