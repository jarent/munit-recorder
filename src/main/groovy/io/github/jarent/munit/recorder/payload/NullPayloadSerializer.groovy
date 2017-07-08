package io.github.jarent.munit.recorder.payload

import io.github.jarent.munit.recorder.PayloadSerializer

class NullPayloadSerializer implements PayloadSerializer {

	public String serilizeToScript(Object payload) {
		return "return null"
	}

}
