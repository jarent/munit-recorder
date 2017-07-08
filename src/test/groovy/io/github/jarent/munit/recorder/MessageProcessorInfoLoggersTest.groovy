package io.github.jarent.munit.recorder

import org.junit.Test

import spock.lang.Specification;

class MessageProcessorInfoLoggersTest  extends Specification {
	
	private MessageProcessorInfoLoggers mpInfoLoggers = new MessageProcessorInfoLoggers();
	
	@Test
	public void shouldLogJson() {
		when:
		MessageProcessorInfo mpInfo = new MessageProcessorInfo()
		
		mpInfo.docName = "Test Name"
		mpInfo.elementName = "request"
		mpInfo.elementNamespace = "http"
		mpInfo.payload = "Sample payload"
		mpInfo.variables = [['name':'var', 'value':'test']]
		
		then:
		
		mpInfoLoggers.logJSON(mpInfo) == '{"fakeDocName":false,"docName":"Test Name","elementName":"request","exceptionThrown":null,"elementNamespace":"http","variables":[{"name":"var","value":"test"}],"payload":"Sample payload"}'
		
	}
	
	
	
	@Test
	public void shouldLogGroovy() {
		when:
		MessageProcessorInfo mpInfo = new MessageProcessorInfo()
		
		mpInfo.docName = "Test Name"
		mpInfo.elementName = "request"
		mpInfo.elementNamespace = "http"
		mpInfo.payload = "Sample payload"
		mpInfo.variables = [['name':'var', 'value':'test']]
		
		then:
		mpInfoLoggers.logGroovy(mpInfo) == """>>>>>>>>>>>>>>>>>>> MOCK START >>>>>>>>>>>>>>>>>>>>>>>>

		whenMessageProcessor("request").ofNamespace("http").
		withAttributes(['doc:name': 'Test Name']).thenReturn(
			muleMessageWithPayload('''Sample payload''')
			)

<<<<<<<<<<<<<<<<<<< MOCK END <<<<<<<<<<<<<<<<<<<<<<<<"""
	}
	
	@Test
	public void shouldLogXML() {
		when:
		MessageProcessorInfo mpInfo = new MessageProcessorInfo()
		
		mpInfo.docName = "Test Name"
		mpInfo.elementName = "request"
		mpInfo.elementNamespace = "http"
		mpInfo.payload = "Sample payload"
		mpInfo.variables = [['name':'var', 'value':'test']]
		
		then:
		
		System.err.println mpInfoLoggers.logXML(mpInfo) 
		
		mpInfoLoggers.logXML(mpInfo) == """>>>>>>>>>>>>>>>>>>> MOCK START >>>>>>>>>>>>>>>>>>>>>>>>
<scripting:script name="mockTestNamePayloadGenerator" engine="groovy"><![CDATA[
  return 'Sample payload']]>
</scripting:script>

<mock:when messageProcessor="http:request" doc:name="Mock Test Name">
	  <mock:with-attributes>
		  <mock:with-attribute name="doc:name" whereValue="#['Test Name']"/>
	 </mock:with-attributes>
	 <mock:then-return payload="#[resultOfScript('mockTestNamePayloadGenerator')]"/>
</mock:when>

<<<<<<<<<<<<<<<<<<< MOCK END <<<<<<<<<<<<<<<<<<<<<<<<"""
	}
	
	@Test
	public void shouldLogXMLForExceptionThrown() {
		when:
		MessageProcessorInfo mpInfo = new MessageProcessorInfo()
		
		mpInfo.docName = "Test Name"
		mpInfo.elementName = "request"
		mpInfo.elementNamespace = "http"
		mpInfo.payload = "Sample payload"
		mpInfo.exceptionThrown = new IllegalStateException("Exception with the message")
		
		then:
		
		System.err.println mpInfoLoggers.logXML(mpInfo)
		
	}
	
	@Test
	public void shouldPrintWarningWhenDocNameIsNotProvided() {
		when:
		MessageProcessorInfo mpInfo = new MessageProcessorInfo()
		
		mpInfo.docName = "http_request"
		mpInfo.elementName = "request"
		mpInfo.elementNamespace = "http"
		mpInfo.payload = "Sample payload"
		mpInfo.fakeDocName = true
		
		then:
		
		System.err.println mpInfoLoggers.logXML(mpInfo)
		
		mpInfoLoggers.logXML(mpInfo) == """>>>>>>>>>>>>>>>>>>> MOCK START >>>>>>>>>>>>>>>>>>>>>>>>
<scripting:script name="mockhttp_requestPayloadGenerator" engine="groovy"><![CDATA[
  return 'Sample payload']]>
</scripting:script>

<mock:when messageProcessor="http:request" doc:name="Mock http_request">
	 <!-- WARNING: Message processor doesn't have doc:name filled -->
	 <mock:then-return payload="#[resultOfScript('mockhttp_requestPayloadGenerator')]"/>
</mock:when>

<<<<<<<<<<<<<<<<<<< MOCK END <<<<<<<<<<<<<<<<<<<<<<<<"""
	}
	
	@Test
	public void shouldPrintInfoWhenIteratorSerializationDisabled() {
		when:
		MessageProcessorInfo mpInfo = new MessageProcessorInfo()
		
		mpInfo.docName = "http_request"
		mpInfo.elementName = "request"
		mpInfo.elementNamespace = "http"
		mpInfo.payload = [1,2,3,4,5].iterator();
		mpInfo.fakeDocName = true
		
		then:
		
		new MessageProcessorInfoLoggers(false).logXML(mpInfo) == 'Iterator Serialization disabled. Payload java.util.ArrayList$Itr from http:request not mocked'
		
		
	}

}
