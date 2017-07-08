package io.github.jarent.munit.recorder.payload

import com.cedarsoftware.util.io.GroovyJsonWriter

import io.github.jarent.munit.recorder.PayloadSerializer

class GroovyIoIteratorSerializer implements PayloadSerializer {

	public String serilizeToScript(Object payload) {
		try {
			def jsonPayload = GroovyJsonWriter.objectToJson(payload.toList(), [(GroovyJsonWriter.PRETTY_PRINT):true])

			return """import com.cedarsoftware.util.io.GroovyJsonReader
	
	def result = GroovyJsonReader.jsonToGroovy('''$jsonPayload''').iterator()

	return result"""
		} catch (StackOverflowError e){
			def payloadClass = payload.getClass().getName()
			return """return '$payloadClass is not supported by iterator serialization'"""
		}
	}
}
