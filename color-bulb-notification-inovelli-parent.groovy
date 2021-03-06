/** 
* Color bulb notification Parent
*
*  Author: 
*    Kevin Earley 
*
*  Documentation:  Changes color led when contact is open
*
*  Changelog: V1.0
*  V1.0.1 - Added debug log option
*         - Moved notification bulb setting to parent
*  V1.0.2 - Added Inovelli red switch notification leds
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
    name: "Color Bulb Notification",
    namespace: "kpejr",
    author: "Kevin Earley",
    description: "Changes color bulb when contact is open",
    category: "Convenience",
    importUrl: "https://raw.githubusercontent.com/kpejrhome/Hubitat/master/color-bulb-notification-parent.groovy",
	    
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "",
)

preferences {
     page name: "mainPage", title: "", install: true, uninstall: true
} 

def mainPage() {
    dynamicPage(name: "mainPage") {
                
        section("Bulb Setup"){
                        
                input "notificationBulb", "capability.colorControl", title: "Which color bulb(s) will be the notification bulb(s)?", multiple: true, required: true
            
                // using power meter to list inovelli red, not capabilty found for indicaor light
                input "notificationSwitch", "capability.powerMeter", title: "Which switches will be used for notification?", multiple: true, required: true
            
                input(name: "returnColor", type: "enum", title: "Which color should the bulb(s) return to after notifcation ends?", options: ["Off","Blue","Green","Grey", "Orange","Red","Purple", "White","Yellow"])
        }
        
        section() {
            app(name: "newBulb", appName: "Color Bulb Notification - Child", namespace: "kpejr", title: "<b>Add a Color Bulb Notificaiton app</b>", multiple: true)
            input name:	"enableLogging", type: "bool", title: "Enable Debug Logging?", defaultValue: false, required: true
        }		
	}
}

def installed() {
    logDebug("Installed application")
    initialize()
}


def updated() {
    logDebug("Updated application")
    unsubscribe()
    initialize()
}

def initialize() {
    logDebug("Initialized with settings: ${settings}")
    
    childApps.each { child ->
        logDebug("Child app: ${child.label}")
    }
    
      subscribe(settings.notificationSwitch, "switch.off", switchOffHandler)
      subscribe(settings.notificationSwitch, "switch.on", switchOnHandler)
}

def logDebug(msg)
{
    if(enableLogging)
    {
        log.debug "${msg}"
    }
}

def switchOffHandler(evt) {
    logDebug("Turning off indicator for ${evt.getDevice().name}")
     evt.getDevice().setIndicator(16714240)
}

def switchOnHandler(evt) {
    logDebug("Turning off indicator for ${evt.getDevice().name}")
    setIndicatorColor(settings.returnColor)
}

// call child apps to see if any contacts are open
def areAllClosed(){

      allClosed = "YES"
    
      childApps.each { child ->
          
          bulbColor = child.areAllClosed()
          
          if(bulbColor != "YES")
          {
              allClosed = bulbColor
          }
     }
    
    return allClosed
}

    def setIndicatorColor(bulbColor){
    for(device in settings.notificationSwitch){
        log.info device.currentValue("switch")
        
        if(device.currentValue("switch") == "off" && bulbColor == settings.returnColor){
                 device.setIndicator(16714240)
        }
        else{
        
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
                    device.setIndicator(16714240)
                    break;
                }
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
