/** 
* Color bulb notification - Child
*
*  Author: 
*    Kevin Earley 
*
*  Documentation:  Changes color led when contact is open
*
*  Changelog: v1.0
*  v1.0.1 - Added debug logging option
*         - Moved notification bulb setting to parent
*  v1.0.2 - Added Inovelli red switch notification leds
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
    importUrl: "https://raw.githubusercontent.com/kpejrhome/Hubitat/master/color-bulb-notification-child.groovy",
    
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

            input(name: "notificationColor", type: "enum", title: "Which color should be used for notification?", options: ["Off","Blue","Green", "Grey", "Orange","Red","Purple","White","Yellow"])
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
    subscribe(settings.triggerWater, "water", waterHandler)
}

def logDebug(msg)
{
    if(enableLogging)
    {
        log.debug "${msg}"
    }
}
            
def areAllClosed(){
    
    allClosed = "YES"
    
    for(device in settings.triggerContact){
        if(device.currentValue("contact") == "open"){
            logDebug("${device.getLabel()} is still open")
            allClosed = notificationColor
        }
    }
    
     for(device in settings.triggerWater){
        if(device.currentValue("water") == "wet"){
            
            logDebug("${device.getLabel()} is still wet")
            allClosed = notificationColor
        }
    }
    
    return allClosed
}

def contactHandler( evt ){
    logDebug("contactHandler Device: ${evt.getDevice().getLabel()} Value: ${evt.value}")
    
    if(evt.value == "open"){
        setBulbColor(notificationColor)
        setIndicatorColor(notificationColor)
    } else{ // "closed"
        
        allClosed = parent.areAllClosed()
        
        if(allClosed == "YES"){
            setBulbColor(parent.returnColor)
             setIndicatorColor(parent.returnColor)
        }
        else{
            setBulbColor(allClosed)   
            setIndicatorColor(allClosed)
        }
    }
}

def waterHandler( evt ){
    logDebug("waterHandler Device: ${evt.getDevice().getLabel()} Value: ${evt.value}")
    
    if(evt.value == "wet"){ 
       setBulbColor(notificationColor)
       setIndicatorColor(notificationColor)
    } else{ // "closed"
        
        allClosed = parent.areAllClosed()
        
        if(allClosed == "YES"){
            setBulbColor(parent.returnColor)
            setIndicatorColor(parent.returnColor)
        }
        else{
            setBulbColor(allClosed)
            setIndicatorColor(allClosed)
        }
    }
}

def setBulbColor(bulbColor){
    for(device in parent.notificationBulb){
        
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
    def setIndicatorColor(bulbColor){
    for(device in parent.notificationSwitch){
        
    log.info "Setting ${device.getLabel()} color to ${bulbColor}"
    
    switch(bulbColor) { 
        case "White":
            device.setIndicator(33491711)
            break;
        case "Grey":
            device.setIndicator(33489440)
            break;
        case "Blue":
            device.setIndicator(33490843)
            break;
        case "Green":
            device.setIndicator(33490776)
            break;
        case "Orange":
            device.setIndicator(33490711)
            break;
        case "Red":
            device.setIndicator(33490688)
            break;
        case "Purple":
            device.setIndicator(33490887)
            break;
        case "Yellow":
            device.setIndicator(33490736)
            break;
        case "Off":
            device.off()
            break;
        }
    }
    }
