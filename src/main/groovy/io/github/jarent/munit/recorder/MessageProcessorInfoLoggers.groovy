package io.github.jarent.munit.recorder

import groovy.json.JsonBuilder
import org.mule.api.MessagingException
import org.mule.transport.NullPayload
import com.cedarsoftware.util.io.GroovyJsonWriter
import com.cedarsoftware.util.io.GroovyJsonReader
import org.mule.config.ExceptionHelper

class MessageProcessorInfoLoggers {
	
	
	private boolean serializeIterator;
	
	private PayloadSerializerFactory psFactory;
	
	public MessageProcessorInfoLoggers(boolean serializeIterator) {
		this.serializeIterator=serializeIterator
		psFactory = new PayloadSerializerFactory()
	}
	
	public MessageProcessorInfoLoggers() {
		this(true);
	}
	
	public String logJSON(MessageProcessorInfo mpInfo) {
		
		return new groovy.json.JsonBuilder(mpInfo).toString()
		
	}
	
	
	
	public String logGroovy(MessageProcessorInfo mpInfo) {
		
		return """>>>>>>>>>>>>>>>>>>> MOCK START >>>>>>>>>>>>>>>>>>>>>>>>

		whenMessageProcessor("$mpInfo.elementName").ofNamespace("$mpInfo.elementNamespace").
		withAttributes(['doc:name': '$mpInfo.docName']).thenReturn(
			muleMessageWithPayload('''$mpInfo.payload''')
			)

<<<<<<<<<<<<<<<<<<< MOCK END <<<<<<<<<<<<<<<<<<<<<<<<"""
		
	}
	
	public String logXML(MessageProcessorInfo mpInfo) {
		
		def messageProcessor = mpInfo.elementNamespace ? mpInfo.elementNamespace + ":" + mpInfo.elementName : mpInfo.elementName
		
		def docNameAttributeCondition = """ <mock:with-attributes>
		  <mock:with-attribute name="doc:name" whereValue="#['$mpInfo.docName']"/>
	 </mock:with-attributes>"""
		
		if (mpInfo.fakeDocName) {
			docNameAttributeCondition = """<!-- WARNING: Message processor doesn't have doc:name filled -->"""
		} 
		
		if (mpInfo.payload instanceof Iterator && !serializeIterator) {
			
			def iteratorClassName = mpInfo.payload.getClass().getName();
			return "Iterator Serialization disabled. Payload $iteratorClassName from $messageProcessor not mocked"
			
		} else if (mpInfo.exceptionThrown != null) {
			def scriptName = 'mock' + mpInfo.docName.tokenize().join('') + "ExceptionGenerator"
			def exception = mpInfo.exceptionThrown
			if (exception instanceof MessagingException && ExceptionHelper.getRootException(exception) != null) {
				exception = ExceptionHelper.getRootException(exception)
			}
			def scriptContent = psFactory.serializeToScript(exception)
			
			return """>>>>>>>>>>>>>>>>>>> MOCK START >>>>>>>>>>>>>>>>>>>>>>>>
<scripting:script name="$scriptName" engine="groovy"><![CDATA[
  $scriptContent]]>
</scripting:script>

<mock:throw-an whenCalling="$messageProcessor" doc:name="Mock $mpInfo.docName" exception-ref="#[resultOfScript('$scriptName')]">
	 $docNameAttributeCondition
</mock:throw-an>

<<<<<<<<<<<<<<<<<<< MOCK END <<<<<<<<<<<<<<<<<<<<<<<<"""
		} else {
			def scriptName = 'mock' + mpInfo.docName.tokenize().join('') + "PayloadGenerator"			
			def scriptContent = psFactory.serializeToScript(mpInfo.payload)
			
			return """>>>>>>>>>>>>>>>>>>> MOCK START >>>>>>>>>>>>>>>>>>>>>>>>
<scripting:script name="$scriptName" engine="groovy"><![CDATA[
  $scriptContent]]>
</scripting:script>

<mock:when messageProcessor="$messageProcessor" doc:name="Mock $mpInfo.docName">
	 $docNameAttributeCondition
	 <mock:then-return payload="#[resultOfScript('$scriptName')]"/>
</mock:when>

<<<<<<<<<<<<<<<<<<< MOCK END <<<<<<<<<<<<<<<<<<<<<<<<"""
		}

	}

}
