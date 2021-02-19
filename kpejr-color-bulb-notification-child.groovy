/** 
* Color bulb notification - Child
*
*  Author: 
*    Kevin Earley 
*
*  Documentation:  Changes color led when contact is open
*
*  Changelog:

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
    name: "Color Bulb Notification - Child",
    namespace: "kpejr",
    author: "Kevin Earley",
    description: "Changes color bulb when contact is open",
    category: "Convenience",
    parent: "kpejr:Color Bulb Notification",
    
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "" )

preferences{ 
    page( name: "childSetup")
}

def childSetup(){ 
    dynamicPage(name: "childSetup", title: "Color bulb Notification - Child", nextPage: null, install: true, uninstall: true, refreshInterval: 0) {
        
        section("Device Section"){
            
            label title: "Enter a name for this app", required: true
            
            input "triggerContact", "capability.contactSensor", title: "Which contact sensor(s) will trigger the color bulb?", multiple: true, required: false
            input "triggerWater", "capability.waterSensor", title: "Which water sensor(s) will trigger the color bulb?", multiple: true, required: false
            
            input "notificationBulb", "capability.colorControl", title: "Which color bulb(s) will be the notification bulb?", multiple: true, required: true

            input(name: "notificationColor", type: "enum", title: "Which color should be used for notification?", options: ["Off","Blue","Green", "Grey", "Orange","Red","Purple","White","Yellow"])
        
            
        }
    }
}

def installed() {
    log.info "Installed application"
    unsubscribe()
    unschedule()
    initialize()
}

def updated() {
    log.info "Updated application"
    unsubscribe()
    unschedule()
    initialize()
}

def initialize(){
    log.info("Initializing with settings: ${settings}")
    
    subscribe(settings.triggerContact, "contact", contactHandler)
    subscribe(settings.triggerWater, "water", waterHandler)
}
            
def areAllClosed(){
    
    allClosed = "YES"
    
    for(device in settings.triggerContact){
        if(device.currentValue("contact") == "open"){
             log.info "${device.getLabel()} is still open"
            allClosed = notificationColor
        }
    }
    
     for(device in settings.triggerWater){
        if(device.currentValue("water") == "wet"){
            
            log.info "${device.getLabel()} is still wet"
            allClosed = notificationColor
        }
    }
    
    return allClosed
}

def contactHandler( evt ){
    log.info "Contact was turned ${evt.value}"
    
    if(evt.value == "open"){
        
        setBulbColor(notificationColor)
    } else{ // "closed"
        
        allClosed = parent.areAllClosed()
        
        if(allClosed == "YES"){
            setBulbColor(parent.returnColor)
        }
        else{
            setBulbColor(allClosed)   
        }
    }
}

def waterHandler( evt ){
    log.info "Water was reported ${evt.value}"
    
    if(evt.value == "wet"){ 
       setBulbColor(notificationColor)
    } else{ // "closed"
        
        allClosed = parent.areAllClosed()
        
        if(allClosed == "YES"){
            setBulbColor(parent.returnColor)
        }
        else{
            setBulbColor(allClosed)   
        }
    }
}

def setBulbColor(bulbColor){
    for(device in settings.notificationBulb){
        
    log.info "Setting ${device.getLabel()} color to ${bulbColor}"
    
    switch(bulbColor) { 
        case "White":
            device.setColor([hue:1,saturation:1,level:100])
            break;
        case "Grey":
            device.setColor([hue:72,saturation:6,level:39])
            break;
        case "Blue":
            device.setColor([hue:56,saturation:100,level:100])
            break;
        case "Green":
            device.setColor([hue:32,saturation:98,level:91])
            break;
        case "Orange":
            device.setColor([hue:5,saturation:97,level:95])
            break;
        case "Red":
            device.setColor([hue:97,saturation:95,level:95])
            break;
        case "Purple":
            device.setColor([hue:73,saturation:100,level:100])
            break;
        case "Yellow":
            device.setColor([hue:13,saturation:84,level:99])
            break;
        case "Off":
            device.off()
            break;
        }
    }
}