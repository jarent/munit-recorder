package io.github.jarent.munit.recorder

import org.mule.transport.NullPayload

import io.github.jarent.munit.recorder.payload.GroovyIoIteratorSerializer
import io.github.jarent.munit.recorder.payload.GroovyIoSerializer
import io.github.jarent.munit.recorder.payload.NullPayloadSerializer
import io.github.jarent.munit.recorder.payload.PayloadInspectedSerializer

class PayloadSerializerFactory {
	
	public String serializeToScript(payload) {
		PayloadSerializer serializer = null;
		if (payload instanceof NullPayload) {
			serializer = new NullPayloadSerializer()
		} else if (payload instanceof String || payload instanceof Number) {
			serializer = new PayloadInspectedSerializer()
		} else if (payload instanceof Iterator) {
			serializer = new GroovyIoIteratorSerializer()
		} else {
			serializer = new GroovyIoSerializer()
		}
		return serializer.serilizeToScript(payload)
	}

}
