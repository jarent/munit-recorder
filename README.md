munit-recorder
=======

Helper tool for automatic [Munit](https://docs.mulesoft.com/munit/) mocks definitions creation based on flow execution, inspired by [Nock recording](https://github.com/node-nock/nock#recording).

### Install

Add to project's pom.xml the dependency:
``` xml
<dependency>
   <groupId>io.github.jarent</groupId>
   <artifactId>munit-recorder</artifactId>
   <version>0.0.5</version>
 </dependency>
```

### Usage

To start generating mocks import munit-recorder.xml to test flow config:
``` xml
<spring:beans>
    <spring:import resource="classpath:munit-recorder.xml"/>
</spring:beans>
```

Mocks definitions are written to log4j2 logger with 'io.github.jarent.munit.recorder.MunitRecorder' name. It is best to redirect them to separate file using log4j2 configuration:
``` xml
<AsyncLogger name="io.github.jarent.munit.recorder.MunitRecorder" level="DEBUG" additivity="false">
    <AppenderRef ref="MunitRecorderMocks"/>
</AsyncLogger>
```

Sample generated mock declaration for Salesforce 'query-single' message processor (Munit-recorder uses [groovy-io](https://github.com/jdereg/groovy-io) for payload and exceptionThrown serialization):
``` xml
<scripting:script name="mockGetCaseDetailsPayloadGenerator" engine="groovy"><![CDATA[
  import com.cedarsoftware.util.io.GroovyJsonReader

	def result = GroovyJsonReader.jsonToGroovy('''{
  "@type":"java.util.HashMap",
  "Custom_Case_Description__c":"Wait for update on network status",
  "CaseNumber":"00001026",
  "Id":null,
  "type":"Case",
  "Custom_Case_Id__c":"123.0"
}''')

	return result]]>
</scripting:script>

<mock:when messageProcessor="sfdc:query-single" doc:name="Mock Get Case Details">
	 <mock:with-attributes>
		  <mock:with-attribute name="doc:name" whereValue="#['Get Case Details']"/>
	 </mock:with-attributes>
	 <mock:then-return payload="#[resultOfScript('mockGetCaseDetailsPayloadGenerator')]"/>
</mock:when>
```

Copy-paste generated elements to your munit test config flow xml. *'scripting:script'* portion must be defined in global elements, while *'mock:when'* within *'munit:test'* element:

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<mule xmlns:scripting="http://www.mulesoft.org/schema/mule/scripting" xmlns:mock="http://www.mulesoft.org/schema/mule/mock"
	xmlns="http://www.mulesoft.org/schema/mule/core" xmlns:doc="http://www.mulesoft.org/schema/mule/documentation" xmlns:munit="http://www.mulesoft.org/schema/mule/munit" xmlns:spring="http://www.springframework.org/schema/beans" xmlns:core="http://www.mulesoft.org/schema/mule/core" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.mulesoft.org/schema/mule/mock http://www.mulesoft.org/schema/mule/mock/current/mule-mock.xsd
http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-current.xsd
http://www.mulesoft.org/schema/mule/core http://www.mulesoft.org/schema/mule/core/current/mule.xsd
http://www.mulesoft.org/schema/mule/scripting http://www.mulesoft.org/schema/mule/scripting/current/mule-scripting.xsd">
    <munit:config name="munit" doc:name="MUnit configuration" mock-connectors="false" mock-inbounds="false"/>
    <spring:beans>
        <spring:import resource="classpath:flow_to_test.xml"/>
        <spring:import resource="classpath:munit-recorder.xml"/>
    </spring:beans>
          <!-- copy scripting:script here -->
    <munit:test name="new-test-flow" description="Test">
        <!-- copy mock:when here -->
        <flow-ref name="flow_to_test" doc:name="Tested flow"/>
    </munit:test>
</mule>
```

### Options

Output from 'munit-record' can be customized using global properties. You can provide the values directly in the munit configuration flow, for example:

``` xml
    <global-property value="true" name="munit.recorder.serializeIterator" />
```    

Available options:
* **munit.recorder.serializeIterator** (true|false) - turn on/off iterator serializations. Iterator can be read only once, so if message processor returns iterator then munit-recorder will read it in order to serialize payload. After that next steps in the flow will see empty data. The workaround is to capture iterator content, then mock it and turn off iterator serialization. Option added as fix for [Should serialize iterators](/../../issues/12) issue.
