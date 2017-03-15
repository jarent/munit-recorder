package io.github.jarent.munit.recorder

import groovy.transform.ToString
import java.util.List;

@ToString
public class MessageProcessorInfo {
		String elementNamespace, elementName, docName
		Object payload
		List variables
		Throwable exceptionThrown
}