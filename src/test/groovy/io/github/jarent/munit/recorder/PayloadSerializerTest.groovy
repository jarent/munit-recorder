package io.github.jarent.munit.recorder

import com.cedarsoftware.util.io.GroovyJsonReader

import spock.lang.Ignore
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
	
	def "Should Serialize Exception"() {
		given:
		def exception = new IllegalStateException("Exception with the message")
		
		when:
		//String script = GroovyJsonWriter.objectToJson(exception)
		//def obj = GroovyJsonReader.jsonToGroovy(script)
		
		String script = MessageProcessorInfoLoggers.serializeToScript(exception)
		
		def result = Eval.me(script)
		
		then:
		exception.getClass().getName() == result.getClass().getName()
		exception.getMessage() == result.getMessage()
	}
	
	
	@Ignore
	def "Should Deserialize Complex Exception"() {
		given:
		String json = '''
{  
   "@type":"org.mule.api.MessagingException",
   "causeRollback":false,
   "handled":false,
   "info":{  
      "@type":"java.util.HashMap",
      "Element":"/navis-sfdc-demoFlow/processors/5 @ e94f3420-06ec-11e7-98a1-d25e667d54c4"
   },
   "errorCode":-1,
   "message":"Json content is not compliant with schema\ncom.github.fge.jsonschema.core.report.ListProcessingReport: failure\n--- BEGIN MESSAGES ---\nerror: object has missing required properties ([\"NavisCase2\"])\n    level: \"error\"\n    schema: {\"loadingURI\":\"file:/Users/jacek/workspace/navis-sfdc-demo/target/classes/NavisCase.json#\",\"pointer\":\"\"}\n    instance: {\"pointer\":\"\"}\n    domain: \"validation\"\n    keyword: \"required\"\n    required: [\"NavisCase2\"]\n    missing: [\"NavisCase2\"]\n---  END MESSAGES  ---\n (org.mule.module.json.validation.JsonSchemaValidationException).",
   "i18nMessage":{  
      "message":"Json content is not compliant with schema\ncom.github.fge.jsonschema.core.report.ListProcessingReport: failure\n--- BEGIN MESSAGES ---\nerror: object has missing required properties ([\"NavisCase2\"])\n    level: \"error\"\n    schema: {\"loadingURI\":\"file:/Users/jacek/workspace/navis-sfdc-demo/target/classes/NavisCase.json#\",\"pointer\":\"\"}\n    instance: {\"pointer\":\"\"}\n    domain: \"validation\"\n    keyword: \"required\"\n    required: [\"NavisCase2\"]\n    missing: [\"NavisCase2\"]\n---  END MESSAGES  ---\n (org.mule.module.json.validation.JsonSchemaValidationException)",
      "code":-1,
      "args":{  
         "@id":4,
         "@items":[  

         ]
      },
      "nextMessage":null
   },
   "detailMessage":"org.mule.module.json.validation.JsonSchemaValidationException: Json content is not compliant with schema\ncom.github.fge.jsonschema.core.report.ListProcessingReport: failure\n--- BEGIN MESSAGES ---\nerror: object has missing required properties ([\"NavisCase2\"])\n    level: \"error\"\n    schema: {\"loadingURI\":\"file:/Users/jacek/workspace/navis-sfdc-demo/target/classes/NavisCase.json#\",\"pointer\":\"\"}\n    instance: {\"pointer\":\"\"}\n    domain: \"validation\"\n    keyword: \"required\"\n    required: [\"NavisCase2\"]\n    missing: [\"NavisCase2\"]\n---  END MESSAGES  ---\n",
   "cause":{  
      "@id":3,
      "@type":"org.mule.module.json.validation.JsonSchemaValidationException",
      "invalidJson":"{\"NavisCase\":{\"Id\":\"00001026\",\"Desc\":\"Wait for update on network status\",\"NavisId\":\"123.0\"}}",
      "info":{  
         "@type":"java.util.HashMap"
      },
      "errorCode":-1,
      "message":"Json content is not compliant with schema\ncom.github.fge.jsonschema.core.report.ListProcessingReport: failure\n--- BEGIN MESSAGES ---\nerror: object has missing required properties ([\"NavisCase2\"])\n    level: \"error\"\n    schema: {\"loadingURI\":\"file:/Users/jacek/workspace/navis-sfdc-demo/target/classes/NavisCase.json#\",\"pointer\":\"\"}\n    instance: {\"pointer\":\"\"}\n    domain: \"validation\"\n    keyword: \"required\"\n    required: [\"NavisCase2\"]\n    missing: [\"NavisCase2\"]\n---  END MESSAGES  ---\n",
      "i18nMessage":{  
         "message":"Json content is not compliant with schema\ncom.github.fge.jsonschema.core.report.ListProcessingReport: failure\n--- BEGIN MESSAGES ---\nerror: object has missing required properties ([\"NavisCase2\"])\n    level: \"error\"\n    schema: {\"loadingURI\":\"file:/Users/jacek/workspace/navis-sfdc-demo/target/classes/NavisCase.json#\",\"pointer\":\"\"}\n    instance: {\"pointer\":\"\"}\n    domain: \"validation\"\n    keyword: \"required\"\n    required: [\"NavisCase2\"]\n    missing: [\"NavisCase2\"]\n---  END MESSAGES  ---\n",
         "code":-1,
         "args":{  
            "@ref":4
         },
         "nextMessage":null
      },
      "detailMessage":null,
      "cause":{  
         "@ref":3
      },
      "stackTrace":{  
         "@id":2,
         "@items":[  

         ]
      },
      "suppressedExceptions":{  
         "@id":1,
         "@type":"java.util.Collections$UnmodifiableRandomAccessList"
      }
   },
   "stackTrace":{  
      "@ref":2
   },
   "suppressedExceptions":{  
      "@ref":1
   }
}
'''
		when:
		def result = GroovyJsonReader.jsonToGroovy(json)
		
		then:
		System.err.println result
	}
	

}
