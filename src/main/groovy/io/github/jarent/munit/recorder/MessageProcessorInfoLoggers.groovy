package io.github.jarent.munit.recorder

import groovy.json.JsonBuilder
import org.mule.api.MessagingException
import org.mule.transport.NullPayload
import com.cedarsoftware.util.io.GroovyJsonWriter
import com.cedarsoftware.util.io.GroovyJsonReader

class MessageProcessorInfoLoggers {
	
	public static String serializeToScript(payload) {
		if (payload instanceof NullPayload) {
			return "return null"
		} else if (payload instanceof String || payload instanceof Number) {
			def inspectedPayload = payload.inspect()
			return "return $inspectedPayload"
		} else {
			 def jsonPayload = GroovyJsonWriter.objectToJson(payload, [(GroovyJsonWriter.PRETTY_PRINT):true])
			 return """import com.cedarsoftware.util.io.GroovyJsonReader
	
	def result = GroovyJsonReader.jsonToGroovy('''$jsonPayload''')

	return result"""
		}
	}
	
	
	
	public static String logJSON(MessageProcessorInfo mpInfo) {
		
		return new groovy.json.JsonBuilder(mpInfo).toString()
		
	}
	
	
	
	public static String logGroovy(MessageProcessorInfo mpInfo) {
		
		return """>>>>>>>>>>>>>>>>>>> MOCK START >>>>>>>>>>>>>>>>>>>>>>>>

		whenMessageProcessor("$mpInfo.elementName").ofNamespace("$mpInfo.elementNamespace").
		withAttributes(['doc:name': '$mpInfo.docName']).thenReturn(
			muleMessageWithPayload('''$mpInfo.payload''')
			)

<<<<<<<<<<<<<<<<<<< MOCK END <<<<<<<<<<<<<<<<<<<<<<<<"""
		
	}
	
	public static String logXML(MessageProcessorInfo mpInfo) {
		
		def messageProcessor = mpInfo.elementNamespace ? mpInfo.elementNamespace + ":" + mpInfo.elementName : mpInfo.elementName
		
		if (mpInfo.exceptionThrown != null) {
			def scriptName = 'mock' + mpInfo.docName.tokenize().join('') + "ExceptionGenerator"
			def exception = mpInfo.exceptionThrown
			if (exception instanceof MessagingException) {
				exception = mpInfo.exceptionThrown.getCauseException()
			}
			def scriptContent = serializeToScript(exception)
			
			return """>>>>>>>>>>>>>>>>>>> MOCK START >>>>>>>>>>>>>>>>>>>>>>>>
<scripting:script name="$scriptName" engine="groovy"><![CDATA[
  $scriptContent]]>
</scripting:script>

<mock:throw-an whenCalling="$messageProcessor" doc:name="Mock $mpInfo.docName" exception-ref="#[resultOfScript('$scriptName')]">
	 <mock:with-attributes>
		  <mock:with-attribute name="doc:name" whereValue="#['$mpInfo.docName']"/>
	 </mock:with-attributes>
</mock:throw-an>

<<<<<<<<<<<<<<<<<<< MOCK END <<<<<<<<<<<<<<<<<<<<<<<<"""
		} else {
			def scriptName = 'mock' + mpInfo.docName.tokenize().join('') + "PayloadGenerator"			
			def scriptContent = serializeToScript(mpInfo.payload)
			
			return """>>>>>>>>>>>>>>>>>>> MOCK START >>>>>>>>>>>>>>>>>>>>>>>>
<scripting:script name="$scriptName" engine="groovy"><![CDATA[
  $scriptContent]]>
</scripting:script>

<mock:when messageProcessor="$messageProcessor" doc:name="Mock $mpInfo.docName">
	 <mock:with-attributes>
		  <mock:with-attribute name="doc:name" whereValue="#['$mpInfo.docName']"/>
	 </mock:with-attributes>
	 <mock:then-return payload="#[resultOfScript('$scriptName')]"/>
</mock:when>

<<<<<<<<<<<<<<<<<<< MOCK END <<<<<<<<<<<<<<<<<<<<<<<<"""
		}

	}

}
