package io.github.jarent.munit.recorder

import groovy.json.JsonBuilder

class MessageProcessorInfoLoggers {
	
	public static String serializeToScript(payload) {
		if (payload instanceof String || payload instanceof Number) {
			def inspectedPayload = payload.inspect()
			return "return $inspectedPayload"
		} else {
			 def jsonPayload = new JsonBuilder( payload ).toString()
			 def payloadClassName = payload.getClass().getName()
			 System.out.println payload.inspect()
			 return """import groovy.json.*;
	import $payloadClassName;

	def resultMap = new JsonSlurper().
			 	setType(JsonParserType.INDEX_OVERLAY).
			 			setCheckDates(true).
			 				parseText( '$jsonPayload' )

	return new $payloadClassName( resultMap )"""
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
		def scriptName = 'mock' + mpInfo.docName.tokenize().join('') + "PayloadGenerator"
		def scriptContent = serializeToScript(mpInfo.payload)
		
		return """>>>>>>>>>>>>>>>>>>> MOCK START >>>>>>>>>>>>>>>>>>>>>>>>
<script:script name="$scriptName" engine="groovy"><![CDATA[
  $scriptContent]]>
</script:script>

<mock:when messageProcessor="$messageProcessor" doc:name="Mock $mpInfo.docName">
	 <mock:with-attributes>
		  <mock:with-attribute name="doc:name" whereValue="#['$mpInfo.docName']"/>
	 </mock:with-attributes>
	 <mock:then-return payload="#[resultOfScript('$scriptName')]"/>
</mock:when>

<<<<<<<<<<<<<<<<<<< MOCK END <<<<<<<<<<<<<<<<<<<<<<<<"""
	}

}
