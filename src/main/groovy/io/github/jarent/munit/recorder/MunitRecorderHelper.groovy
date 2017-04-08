package io.github.jarent.munit.recorder

import java.util.List
import java.util.Map
import groovy.json.JsonBuilder
import javax.xml.namespace.QName

import org.mule.api.MuleEvent

class MunitRecorderHelper {
	
	
	public static final QName DOC_NAME = new QName("http://www.mulesoft.org/schema/mule/documentation","name")
	public static final QName SOURCE_ELEMENT = new QName("http://www.mulesoft.org/schema/mule/documentation","sourceElement")
	public static final QName SOURCE_FILE_LINE = new QName("http://www.mulesoft.org/schema/mule/documentation","sourceFileLine")
	
	
	public static String getDocName(String sourceElement) {
		def group = (sourceElement =~ /doc:name=\"([^\"]+)\"/)

		if (group.size() == 1) {
			return group[0][1]
		} else {
			return null
		}
	}
	
	
	public static Map getElementNameAndNamespace(String sourceElement) {
		
		//TODO - couldn't use XmlSlurper nor XmlParser - have to declare namespace upfront - right now parsing using regex...
		def group = (sourceElement =~ /<([\w-]+:)?([\w-]+).+/)
		
		if (group.size() >= 1) {
			return ['namespace': (group[0][1] != null ? group[0][1][0..-2] : null),
					'name': group[0][2]
				   ]
		} else {
			return [:]
		}
		
	}
	
	public static Map getElementNameAndNamespaceFromFlow(int mpFileLine, Map flowAnnotations) {
			
		def mpSourceElement =  flowAnnotations[SOURCE_ELEMENT].readLines()[mpFileLine - flowAnnotations[SOURCE_FILE_LINE]].trim()
		
		return getElementNameAndNamespace(mpSourceElement)
	}
	
	public static Map getElementNameAndNamespaceFromFile(int mpFileLine, String mpFileName) {
		
	def mpSourceElement =  this.getClass().getResource("/" + mpFileName).text.readLines()[mpFileLine-1]
	
	return getElementNameAndNamespace(mpSourceElement)
}
	
	public static List getVariables(MuleEvent event) {
		return event.getFlowVariableNames().collect {
			
			
			['name': it,
			 'value': event.getFlowVariable(it).toString()]
		}
	}
	
	
	
	
}
