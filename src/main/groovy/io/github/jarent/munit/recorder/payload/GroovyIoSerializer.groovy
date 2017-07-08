package io.github.jarent.munit.recorder.payload

import com.cedarsoftware.util.io.GroovyJsonWriter

import io.github.jarent.munit.recorder.PayloadSerializer

class GroovyIoSerializer implements PayloadSerializer {

	public String serilizeToScript(Object payload) {
		try {
			def jsonPayload = GroovyJsonWriter.objectToJson(payload, [(GroovyJsonWriter.PRETTY_PRINT):true])

			return """import com.cedarsoftware.util.io.GroovyJsonReader
	
	def result = GroovyJsonReader.jsonToGroovy('''$jsonPayload''')

	return result"""
		} catch (StackOverflowError e){
			def payloadClass = payload.getClass().getName()
			return """return '$payloadClass is not supported by serialization'"""
		}
	}
}
