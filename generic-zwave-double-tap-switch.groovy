/** 
* Generic Zwave DoubleTap Switch
*
*  Author: 
*    Kevin Earley 
*
*  Documentation: Generic Switch with Double Tap
*
*  Changelog: V1.0
*
*  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License. You may obtain a copy of the License at:
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
*  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
*  for the specific language governing permissions and limitations under the License.
*
*/
metadata {
    definition(
    name: "Generic Zwave DoubleTap Switch",
    namespace: "kpejr",
    author: "Kevin Earley",
    description: "Generic Switch with Double Tap",
    category: "Driver",
    importUrl: "https://raw.githubusercontent.com/kpejrhome/Hubitat/master/generic-zwave-double-tap-switch.groovy",	    
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "",
    )
	{
		capability "Actuator"
		capability "Configuration"
        capability "DoubleTapableButton"
        capability "HoldableButton"
        capability "PushableButton"
		capability "Refresh"
        capability "ReleasableButton"
		capability "Sensor"
		capability "Switch"

		attribute "inverted", "enum", ["inverted", "not inverted"]
        
        command "doubleTapUp"
        command "doubleTapDown"
        command "flash"
	}

    preferences {
        input name: "indicatorLed", type: "enum", title: "Turn on Indicator", multiple: false, options: ["0" : "When Off", "1" : "When On", "2" : "Never"], required: false, displayDuringSetup: true
        input name: "invertedButtons", type: "enum", title: "Invert Buttons", multiple: false, options: ["0" : "Normal", "1" : "Inverted"], required: false, displayDuringSetup: true
        
        input name: "enableLogging", type: "bool", title: "Enable debug logging", defaultValue: false	
	    input name: "logDescription", type: "bool", title: "Enable descriptionText logging", defaultValue: true	
    }
}

def logDebug(msg)
{
    if(enableLogging)
    {
        logDebug "${msg}"
    }
}


def configure() {
    def cmds = []
  
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 3).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 4).format()
    
    delayBetween(cmds,500)
}

def updated() {
    if (state.lastUpdated && now() <= state.lastUpdated + 3000) return
    state.lastUpdated = now()

    // Set LED param
	if (paramLED==null) {
		paramLED = 0
	}	
	cmds << secure(zwave.configurationV2.configurationSet(scaledConfigurationValue: indicatorLed.toInteger(), parameterNumber: 3, size: 1).format())
	cmds << secure(zwave.configurationV2.configurationGet(parameterNumber: 3).format())

	// Set Inverted param
	if (paramInverted==null) {
		paramInverted = 0
	}
	cmds << secure(zwave.configurationV2.configurationSet(scaledConfigurationValue: invertedButtons.toInteger(), parameterNumber: 4, size: 1).format())
	cmds << secure(zwave.configurationV2.configurationGet(parameterNumber: 4).format())
}

def parse(String description) {
    def result = null
    def cmd = zwave.parse(description, [0x20: 1, 0x25: 1, 0x56: 1, 0x70: 2, 0x72: 2, 0x85: 2])
    
    if (cmd) {
        result = zwaveEvent(cmd)
        logDebug "parse description: ${cmd} to ${result.inspect()}"
    } 
    else {
        logDebug "Did Not description: ${description}"
    }
    
    result    
}

def zwaveEvent(hubitat.zwave.commands.crc16encapv1.Crc16Encap cmd) {
	logDebug("zwaveEvent(): CRC-16 Encapsulation Command received: ${cmd}")
    
	def encapsulatedCommand = zwave.commandClass(cmd.commandClass)?.command(cmd.command)?.parse(cmd.data)
    
	if (!encapsulatedCommand) {
		logDebugg("zwaveEvent(): Could not extract command from ${cmd}")
	} else {
		return zwaveEvent(encapsulatedCommand)
	}
}

def zwaveEvent(hubitat.zwave.commands.basicv1.BasicReport cmd) {
    logDebug "Basic Report: ${device.displayName} Command: ${cmd}"
    
    createEvent(name: "switch", value: cmd.value ? "on" : "off", isStateChange: true, type: "digital")
}

def zwaveEvent(hubitat.zwave.commands.basicv1.BasicSet cmd) {
    logDebug "Basic Set: ${device.displayName} Command: ${cmd}"
    
	if (cmd.value == 255) {
        createEvent(name: "doubleTapped", value: 1, descriptionText: "DoubleTap up $device.displayName", isStateChange: true, type: "digital")
    }
	else if (cmd.value == 0) {
         createEvent(name: "doubleTapped", value: 2, descriptionText: "DoubleTap down $device.displayName", isStateChange: true, type: "digital")
    }
}

def zwaveEvent(hubitat.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd) {
    logDebug "Switch: ${device.displayName} Command: ${cmd}"

	createEvent(name: "switch", value: cmd.value ? "on" : "off", isStateChange: true, type: "physical")
}

def zwaveEvent(hubitat.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
    logDebug "Devce Report: ${cmd}"
    
    state.manufacturer=cmd.manufacturerName

	def msr = String.format("%04X-%04X-%04X", cmd.manufacturerId, cmd.productTypeId, cmd.productId)
	updateDataValue("MSR", msr)	
    createEvent([descriptionText: "$device.displayName MSR: $msr", isStateChange: false])
}

def zwaveEvent(hubitat.zwave.commands.versionv1.VersionReport cmd) {
	def fw = "${cmd.applicationVersion}.${cmd.applicationSubVersion}"
	updateDataValue("fw", fw)
    
	logDebug "Version report: Device: ${device.displayName} Firmware: $fw, Z-Wave version: ${cmd.zWaveProtocolVersion}.${cmd.zWaveProtocolSubVersion}"
}


def zwaveEvent(hubitat.zwave.Command cmd) {
    log.warn "Unhandled Command: Device: ${device.displayName} Command: ${cmd}"
}

def doubleTapUp() {
	sendEvent(name: "doubleTapped", value: 1, descriptionText: "Double-tap up (button 1) on $device.displayName", isStateChange: true, type: "digital")
}

def doubleTapDown() {
	sendEvent(name: "doubleTapped", value: 2, descriptionText: "Double-tap down (button 2) on $device.displayName", isStateChange: true, type: "digital")
}

def flash() {
      delayBetween([
        zwave.basicV1.basicSet(value: 0xFF).format(),
        zwave.basicV1.basicGet().format(),
        zwave.basicV1.basicSet(value: 0x00).format(),
		zwave.basicV1.basicGet().format()
        ], 500)
}

def refresh() {
	def cmds = []
	cmds << zwave.switchBinaryV1.switchBinaryGet().format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 3).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 4).format()
    
	if (getDataValue("MSR") == null) {
		cmds << zwave.manufacturerSpecificV1.manufacturerSpecificGet().format()
	}
    
	delayBetween(cmds,500)
}

def on() {

    delayBetween([
        zwave.basicV1.basicSet(value: 0xFF).format(),
        zwave.basicV1.basicGet().format()
        ], 200)
}

def off() {
	delayBetween([
		zwave.basicV1.basicSet(value: 0x00).format(),
		zwave.basicV1.basicGet().format()
	], 200)
}
