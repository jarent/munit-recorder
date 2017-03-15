munit-recorder
=======

Helper tool for automatic Munit mocks creation based on flow execution.

Download the project from github repo and build it using maven (jar is not upload to any maven repo yet)
After that add to project the dependency:
```
<dependency>
   <groupId>io.github.jarent</groupId>
   <artifactId>munit-recorder</artifactId>
   <version>0.0.1-SNAPSHOT</version>
 </dependency>
```

Munit-recorder uses [groovy-io](https://github.com/jdereg/groovy-io) for payload and exceptionThrown serialization.

To start generating mocks import munit-recorder.xml to test flow config:
```
<spring:beans>
    <spring:import resource="classpath:munit-recorder.xml"/>
</spring:beans>
```

Mocks definitions are written to log4j2 logger with 'io.github.jarent.munit.recorder.MunitRecorder' name. It is best to redirect them to separate file using log4j2 configuration:
```
<AsyncLogger name="io.github.jarent.munit.recorder.MunitRecorder" level="DEBUG" additivity="false">
    <AppenderRef ref="MunitRecorderMocks"/>
</AsyncLogger>
```

Sample generated mock declaration for Salesforce 'query-single' message processor:
```
<script:script name="mockGetCaseDetailsPayloadGenerator" engine="groovy"><![CDATA[
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
</script:script>

<mock:when messageProcessor="sfdc:query-single" doc:name="Mock Get Case Details">
	 <mock:with-attributes>
		  <mock:with-attribute name="doc:name" whereValue="#['Get Case Details']"/>
	 </mock:with-attributes>
	 <mock:then-return payload="#[resultOfScript('mockGetCaseDetailsPayloadGenerator')]"/>
</mock:when>
```
