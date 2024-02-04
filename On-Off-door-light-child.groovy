/** 
* On Off Door Light- Child
*
*  Author: 
*    Kevin Earley 
*
*  Documentation:  Turn on or off a light when opening a door or closing a door
*
*  Changelog: V1.0
*
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
    name: "On Off Door Light - Child",
    namespace: "kpejr",
    author: "Kevin Earley",
    description: "Turn on or off a light when opening or closing a door.",
    category: "Convenience",
    parent: "kpejr:On Off Door Light",
    importUrl: "https://raw.githubusercontent.com/kpejrhome/Hubitat/master/Sunset-Door-Light-Child.groovy",
    
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "" )

preferences{ 
    page( name: "childSetup")
}

def childSetup(){ 
    dynamicPage(name: "childSetup", title: "On Off Door Light - Child", nextPage: null, install: true, uninstall: true, refreshInterval: 0) {
        
        section("Device Section"){
            
            label title: "Enter a name for this app", required: true
            
            input "triggerContact", "capability.contactSensor", title: "Which contact sensor(s) will trigger the switch?", multiple: true, required: true
            input "targetSwitch", "capability.switch", title: "Which switch(s) do you want to turn on?", multiple: true, required: true
            input name: "pauseOnSeconds", type: "number", title: "How many seconds to wait till turning on?", defaultValue: 0, required: true
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
    
    subscribe(settings.triggerContact, "contact", contactHandler)
}

def logDebug(msg)
{
    if(enableLogging)
    {
        log.debug "${msg}"
    }
}

def contactHandler(evt){
    logDebug("contactHandler Device: ${evt.getDevice().getLabel()} Value: ${evt.value}")
    
    if(settings.pauseOnSeconds > 0){
        pauseExecution(settings.pauseOnSeconds * 1000)
    }
        
    if(evt.getDevice().currentValue("contact") == "open"){
            turnLightsOn()
    }
    else{
         turnLightsOff()
    }
}


def turnLightsOn(){
      for(device in settings.targetSwitch){
                log.info "Turning on ${device.getLabel()}"
                device.on()
      }
}

def turnLightsOff(){
          for(device in settings.targetSwitch){
                log.info "Turning off ${device.getLabel()}"
                device.off()
          }
}
