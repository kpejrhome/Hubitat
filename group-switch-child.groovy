/** 
* Group Switch - Child
*
*  Author: 
*    Kevin Earley 
*
*  Documentation:  Use a virtual, real button, or double tap switch to control other groups of switches
*
*  Changelog: V1.0
*  v1.0.1 - Added debug logging
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
    name: "Group Switch - Child",
    namespace: "kpejr",
    author: "Kevin Earley",
    description: "Use a virtual or real button to group other switches",
    category: "Convenience",
    parent: "kpejr:Group Switch",
    importUrl: "https://raw.githubusercontent.com/kpejrhome/Hubitat/master/group-switch-child.groovy",
    
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "" )

preferences{ 
    page( name: "childSetup")
}

def childSetup(){ 
    dynamicPage(name: "childSetup", title: "Group Switch - Child", nextPage: null, install: true, uninstall: true, refreshInterval: 0) {
        
        section("Device Section"){
            
            label title: "Enter a name for this app", required: true
            
             input "triggerButton", "capability.pushableButton", title: "Which button do you want to use to activate the switches?", multiple: false, required: false
             input "triggerDoubleTapSwitch", "capability.switch", title: "Which switch(s) do you want to double tap to activate other switches?", multiple: true, required: false
            
             input "targetOnSwitch", "capability.switch", title: "Which switch(s) do you want to turn on?", multiple: true, required: false
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
    
    subscribe(triggerButton, "pushed.1", buttonOnHandler)
    subscribe(triggerButton, "pushed.2", buttonOffHandler)
    
    subscribe(triggerDoubleTapSwitch, "doubleTapped.1", buttonOnHandler)
    subscribe(triggerDoubleTapSwitch, "doubleTapped.2", buttonOffHandler)
}
       
def logDebug(msg)
{
    if(enableLogging)
    {
        log.debug "${msg}"
    }
}


def buttonOnHandler( evt ){
      logDebug("buttonOnHandler Device: ${evt.getDevice().getLabel()} Value: ${evt.value}")
    
      for(device in settings.targetOnSwitch){
        log.info "Group Switch turing on ${device.getLabel()}"
        device.on()
      }
}

def buttonOffHandler( evt ){
     logDebug("buttonOffHandler Device: ${evt.getDevice().getLabel()} Value: ${evt.value}")
    
      for(device in settings.targetOffSwitch){
        log.info "Group Switch turing off ${device.getLabel()}"
        device.off()
      }
}
