package io.github.jarent.munit.recorder

import static io.github.jarent.munit.recorder.MunitRecorderHelper.*

import org.mule.api.AnnotatedObject
import org.mule.module.extension.internal.runtime.processor.OperationMessageProcessor
import org.mule.munit.common.endpoint.MockOutboundEndpoint
import org.mule.munit.common.processor.interceptor.MunitMessageProcessorInterceptor
import org.mule.munit.common.processor.interceptor.WrapperMunitMessageProcessorInterceptor
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper
import org.mule.processor.chain.InterceptingChainLifecycleWrapper


class MessageProcessorInfoBuilder {
	
	private MessageProcessorInfo mpInfo = new MessageProcessorInfo();
	
	
	public MessageProcessorInfoBuilder fromMessageProcessor(def mp) {		
		if (mp.hasProperty('CGLIB$CALLBACK_0')) {
			if (mp.'CGLIB$CALLBACK_0' instanceof WrapperMunitMessageProcessorInterceptor) {
				return fromMessageProcessor(mp.'CGLIB$CALLBACK_0'.realMp)
			} else if (mp.'CGLIB$CALLBACK_0' instanceof MunitMessageProcessorInterceptor) {
				fillMessageProcessorInfoFromCallback(mp)
			} else  {
				throw new IllegalStateException("Unexpected CGLIB$CALLBACK_0 class: " + mp.'CGLIB$CALLBACK_0'.getClass().getName());
			}			
		} else if (mp instanceof AnnotatedObject && mp?.getAnnotations().size() > 0) {
			fillMessageProcessorInfoFromAnnotations((AnnotatedObject)mp)
		} else if (mp instanceof OperationMessageProcessor || 
				   mp instanceof SubflowInterceptingChainLifecycleWrapper || 
				   mp instanceof MockOutboundEndpoint ||
				   mp instanceof InterceptingChainLifecycleWrapper) {
			throw new UnsupportedOperationException("Cant't read metadata from " + mp.getClass().getName())
		} else if (mp.getClass().getName().contains("org.mule.config.spring.factories.FlowRefFactoryBean") ) {
			throw new UnsupportedOperationException("Cant't read metadata from " + mp.getClass().getName())
		} else {
			throw new IllegalStateException('No annotations nor CGLIB$CALLBACK_0 - ' + mp.getClass().getName());
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
	
	public MessageProcessorInfoBuilder withExceptionThrown(def exceptionThrown) {
		mpInfo.exceptionThrown = exceptionThrown
		return this
	}
	
	
	public MessageProcessorInfo build() {
		if (mpInfo.docName == null) {
			mpInfo.with {
				//if docName is null, set if with namescpae and name
				docName = (elementNamespace != null ? elementNamespace + "_" : "") + elementName
				fakeDocName = true
			}
		}
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
		
	}
	
	private void fillMessageProcessorInfoFromCallback(def mp) {
		mpInfo.docName = mp?.'CGLIB$CALLBACK_0'.getAttributes().'doc:name'		
					
		def elementNameAndNamespace = getElementNameAndNamespaceFromFile(mp?.'CGLIB$CALLBACK_0'.'lineNumber' as Integer, mp?.'CGLIB$CALLBACK_0'.getFileName() )
		
		mpInfo.elementNamespace = elementNameAndNamespace.namespace
		mpInfo.elementName = elementNameAndNamespace.name
	}
	

}
