package io.github.jarent.munit.recorder

import groovy.json.JsonBuilder
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import spock.lang.Specification
import spock.lang.Unroll

class PayloadSerializerTest extends Specification {
	
	@Unroll
	def "should #useCase"() {
		expect: 'payload serialized to script'
		
			String script = MessageProcessorInfoLoggers.serializeToScript(payload)
			
			Eval.me(script) == payload
		
		where: "payload is #payload"	
			
		useCase		|| payload 
'Serialize String'  || "Sample payload"   
'Serialize Integer' || 400 
'Serialize Double'	|| -0.90
'Serialize List'    || ['Test', null, 100, 0.90]  
'Serialize Map'		|| ['key': 'sdfsdfsdfsdff', '1':0.90]
	}
	
	
	def "Should Serialize Object"() {
		given:
		def payload = new Root([first:'1', second:2, third:new Date()])
		
		when:
		String script = MessageProcessorInfoLoggers.serializeToScript(payload)
		
		def result = Eval.me(script)
		
		then:
		payload.first == result.first
		payload.second == result.second
		payload.third.getDateString() == result.third.getDateString()
	}
	
	def "Should Serialize Date"() {
		given:
		def payload = ['date': new Date(), '1':0.90]
		
		when:
		String script = MessageProcessorInfoLoggers.serializeToScript(payload)
		
		def result = Eval.me(script)
		
		then:
		result.'1' == payload.'1'
		result.date.getDateString() == payload.date.getDateString() 
	}
	
	
	

}
