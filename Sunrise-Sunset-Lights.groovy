/** 
* Sunrise Sunset Lights
*
*  Author: 
*    Kevin Earley 
*
*  Documentation:  Turn on/off lights at sunset/sunrise.
*
*  Changelog: v1.0
*  1.01 - FIxed event handlers
*       - Added debug logging
*  1.02 - Fixed bug in event logging
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
definition(
    name: "Sunrise Sunset Lights",
    namespace: "kpejr",
    author: "Kevin Earley",
    description: "Turn on/off lights at sunrise/sunset.",
    category: "Convenience",
    importUrl: "https://raw.githubusercontent.com/kpejrhome/Hubitat/master/Sunrise-Sunset-Lights.groovy",
    
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "" )

preferences{ 
    page( name: "appSetup")
}

def appSetup(){ 
    dynamicPage(name: "appSetup", title: "Sunrise Sunset Lights", nextPage: null, install: true, uninstall: true, refreshInterval: 0) {
        
        section("Device Section"){
            
            label title: "Enter a name for this app", required: true
            
            input "sunriseOnSwitch", "capability.switch", title: "Which switch(s) do you want to turn on at sunrise?", multiple: true, required: false
            input "sunRiseOffSwitch", "capability.switch", title: "Which switch(s) do you want to turn off at sunrise?", multiple: true, required: false
            input "sunsetOnSwitch", "capability.switch", title: "Which switch(s) do you want to turn on at sunset?", multiple: true, required: false
            input "sunsetOffSwitch", "capability.switch", title: "Which switch(s) do you want to turn off at sunset?", multiple: true, required: false
            input name:	"enableLogging", type: "bool", title: "Enable Debug Logging?", defaultValue: false, required: true
        }
    }
}

def installed() {
    logDebug("Installed application")
    unsubscribe()
    unschedule()
    initialize()
}

def updated() {
    logDebug("Updated application")
    unsubscribe()
    unschedule()
    initialize()
}

def initialize(){
    logDebug("Initializing with settings: ${settings}")
    
    subscribe(location, "sunrise", sunriseHandler)
    subscribe(location, "sunset", sunsetHandler)
}

def logDebug(msg)
{
    if(enableLogging)
    {
        log.debug "${msg}"
    }
}

def sunriseHandler(evt){
    logDebug("sunriseHandler")
    
    for(device in settings.sunriseOnSwitch){
        log.info "Sunrise turing on ${device.getLabel()}"
        device.on()
    }
    
     for(device in settings.sunriseOffSwitch){
         log.info "Sunrise turing off ${device.getLabel()}"
        device.off()
    }
}

def sunsetHandler(evt){
    logDebug("sunsetHandler")
    
    for(device in settings.sunsetOnSwitch){
        log.info "Sunset turing on ${device.getLabel()}"
        device.on()
    }
    
     for(device in settings.sunsetOffSwitch){
        log.info "Sunset turing off ${device.getLabel()}"
        device.off()
    }
}
