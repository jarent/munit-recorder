package io.github.jarent.munit.recorder.payload

import io.github.jarent.munit.recorder.PayloadSerializer

class PayloadInspectedSerializer implements PayloadSerializer {

	public String serilizeToScript(Object payload) {
		def inspectedPayload = payload.inspect()
		return "return $inspectedPayload"
	}

}
