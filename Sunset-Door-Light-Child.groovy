/** 
* Sunset Door Light- Child
*
*  Author: 
*    Kevin Earley 
*
*  Documentation:  Turn on a light when opening a door after sunset.
*
*  Changelog: V1.0
*  V1.01 - Fixed presence event
*        - Added debug logging
*  V1.02 - Added turn off after sunset only option so a door can turn a light on after sunset or always.
*  V1.03 - Added pause before turning on option
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
    name: "Sunset Door Light - Child",
    namespace: "kpejr",
    author: "Kevin Earley",
    description: "Turn on a light when opening a door after sunset.",
    category: "Convenience",
    parent: "kpejr:Sunset Door Light",
    importUrl: "https://raw.githubusercontent.com/kpejrhome/Hubitat/master/Sunset-Door-Light-Child.groovy",
    
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "" )

preferences{ 
    page( name: "childSetup")
}

def childSetup(){ 
    dynamicPage(name: "childSetup", title: "Sunset Door Light - Child", nextPage: null, install: true, uninstall: true, refreshInterval: 0) {
        
        section("Device Section"){
            
            label title: "Enter a name for this app", required: true
            
            input "triggerContact", "capability.contactSensor", title: "Which contact sensor(s) will trigger the switch?", multiple: true, required: true
            input "triggerPresence", "capability.presenceSensor", title: "Which presence sensor(s) will trigger the switch?", multiple: true, required: false
            input "targetSwitch", "capability.switch", title: "Which switch(s) do you want to turn on?", multiple: true, required: true
            input name:	"afterSunsetOnly", type: "bool", title: "Only turn on after sunset?", defaultValue: false, required: true
            input name: "offTimerMinutes", type: "number", title: "How many minutes to wait till turning off?", defaultValue: 0, required: true
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
    subscribe(settings.triggerPresence, "presence", presenseHandler)
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
        
    if(evt.value == "open"){
        def currTime = new Date()
        
        if(settings.afterSunsetOnly == true)
        {
            // after sonset on only set
            if (currTime > location.sunset || currTime < location.sunrise) {
                // it's between sunset and sunrise turn on target devices
               turnLightsOn()
            }
        }
        else
        {
            // Always turn on no matter time set
            turnLightsOn()
        }    
    } 
}

def presenseHandler(evt){
    logDebug("contactHandler Device: ${evt.getDevice().getLabel()} Value: ${evt.value}")
    
    def currTime = new Date()

     if(settings.afterSunsetOnly == true)
     {
        if (currTime > location.sunset || currTime < location.sunrise) {
           // it's between sunset and sunrise turn on target devices
           turnLightsOn()
        }
    }
    else {
        // any time light turn on
       turnLightsOn()
    }
}

def turnLightsOn(){
      for(device in settings.targetSwitch){
                log.info "Turning on ${device.getLabel()}"
                device.on()
      }
    
    if(settings.offTimerMinutes > 0){
        def offMinutes = settings.offTimerMinutes * 60
        logDebug "Off Minutes ${offMinutes}"
        
        runIn(offMinutes, "turnLightsOff")
    }
}

def turnLightsOff(){
      for(device in settings.targetSwitch){
                log.info "Turning off ${device.getLabel()}"
                device.off()
      }
}
