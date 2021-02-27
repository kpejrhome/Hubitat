/** 
* Light Timer - Child
*
*  Author: 
*    Kevin Earley 
*
*  Documentation:  Turns lights off and on at a certain time.
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
    name: "Light Timer - Child",
    namespace: "kpejr",
    author: "Kevin Earley",
    description: "Turns lights off and on at a certain time.",
    category: "Convenience",
    parent: "kpejr:Light Timer",
    importUrl: "https://raw.githubusercontent.com/kpejrhome/Hubitat/master/light-timer-child.groovy",
    
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "" )

preferences{ 
    page( name: "childSetup")
}

def childSetup(){ 
    dynamicPage(name: "childSetup", title: "Light Timer - Child", nextPage: null, install: true, uninstall: true, refreshInterval: 0) {
        
        section("Device Section"){
            
             label title: "Enter a name for this app", required: true
            
             input name: "timerOn", type: "time", title: "What time do you want to turn the switches on?", required: false
             input "targetOnSwitch", "capability.switch", title: "Which switch(s) do you want to turn on?", multiple: true, required: false
            
             input name: "timerOff", type: "time", title: "What time do you want to turn the switches off?", required: false
             input "targetOffSwitch", "capability.switch", title: "Which switch(s) do you want to turn of?", multiple: true, required: false
            
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
    
    if(timerOn != null){
         schedule(timerOn, timerOnHandler)
    }
    
     if(timerOff != null){
         schedule(timerOff, timerOffHandler)
     }
}

def logDebug(msg)
{
    if(enableLogging)
    {
        log.debug "${msg}"
    }
}
      
def timerOnHandler( evt ){
      logDebug("Timer On Event")
    
      for(device in settings.targetOnSwitch){
        log.info "Group Switch turing on ${device.getLabel()}"
        device.on()
      }
}

def timerOffHandler( evt ){
     logDebug("Timer Off Event")
    
      for(device in settings.targetOffSwitch){
        log.info "Group Switch turing off ${device.getLabel()}"
        device.off()
      }
}

