package io.github.jarent.munit.recorder

import org.junit.Test

import static io.github.jarent.munit.recorder.MunitRecorderHelper.*;
import spock.lang.Specification;

class MunitRecorderHelperTest  extends Specification {
	
	
	@Test
	public void shouldRecongizeNamespaceAndElementName() {
		when:
		def elementNameAndNamespace = MunitRecorderHelper.getElementNameAndNamespace('''<http:request config-ref="IFTTT_HTTP_Request_Configuration" path="/trigger/#[flowVars.iftttMakerTrigger.eventName]/with/key/#[flowVars.iftttMakerTrigger.key]" method="POST" doc:name="Trigger IFTTT Notification">
					<http:request-builder>
					<http:header headerName="Content-Type" value="application/json"></http:header>
					</http:request-builder>
					</http:request>''')
		
		then:
		elementNameAndNamespace.namespace == 'http'
		elementNameAndNamespace.name == 'request-builder'
	}
	
	public void shouldRecognizeNamespaceAndElementNameFromFlow() {
		when:
		
		def mpFileLine = 13
		
		def attributes = [(SOURCE_FILE_LINE): 12, 
						   (SOURCE_ELEMENT): '''<munit:test name="new-test-suite-navis-sfdc-demoFlowTest" description="Test">
			<munit:set payload="#[]" doc:name="Set Message">
			<munit:inbound-properties>
			<munit:inbound-property key="http.query.params" value="['caseNumber':'00001026']"></munit:inbound-property>
			</munit:inbound-properties>
			</munit:set>
			<flow-ref name="navis-sfdc-demoFlow" doc:name="Flow-ref to navis-sfdc-demoFlow"></flow-ref>
			<sfdc:query-single config-ref="Salesforce__Basic_authentication" query="dsql:SELECT CaseNumber,leadcalled__Navis_Case_Description__c,leadcalled__Navis_Case_Id__c FROM Case WHERE CaseNumber = '#[flowVars.requestedCaseNumber]'" doc:name="Get Case Details"></sfdc:query-single>
			<logger level="ERROR" message="#[payload]" doc:name="Logger"></logger>
			</munit:test>}'''];
			
		def elementNameAndNamespace =	MunitRecorderHelper.getElementNameAndNamespaceFromFlow(mpFileLine, attributes)
			
		then:
		
		elementNameAndNamespace.namespace == 'munit'
		elementNameAndNamespace.name== 'set'
		
	}
	
	@Test
	public void shouldRecongizeElementName() {
		when:
		def elementNameAndNamespace =  MunitRecorderHelper.getElementNameAndNamespace(
					'''<set-payload value="{ "answer": "#[payload.result.fulfillment.speech]" }" doc:name="Set JSON response"></set-payload>'''
					)
		
		then:
		elementNameAndNamespace.namespace == null
		elementNameAndNamespace.name== 'set-payload'
	}
	
	@Test
	public void shouldRecongizeDocName() {
		when:
		def docName =  MunitRecorderHelper.getDocName(
					'''<set-payload value="{ "answer": "#[payload.result.fulfillment.speech]" }" doc:name="Set JSON response"></set-payload>'''
					)
		
		then:
		docName == 'Set JSON response'
	}

}
