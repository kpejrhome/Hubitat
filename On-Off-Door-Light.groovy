/** 
* On Off Door ligth Parent
*
*  Author: 
*    Kevin Earley 
*
*  Documentation:  Turn on/off a light when opening or closing door
*
*  Changelog: v1.0
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
    name: "On Off Door Light",
    namespace: "kpejr",
    author: "Kevin Earley",
    description: "Turn on or off a light when opening or closing a door",
    category: "Convenience",
    importUrl: "https://raw.githubusercontent.com/kpejrhome/Hubitat/master/On-Off-Light.groovy",	
	    
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "",
)


preferences {
     page name: "mainPage", title: "", install: true, uninstall: true
} 

def mainPage() {
    dynamicPage(name: "mainPage") {
        
        section() {
            paragraph "This app will turn a light on or off when a door is opened or closed."
            app(name: "newOnOff", appName: "On Off Door Light - Child", namespace: "kpejr", title: "<b>Add a On Off Door Light app</b>", multiple: true)
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

def initialize() {
    logDebug("Installed with settings: ${settings}")
    
    childApps.each { child ->
        logDebug("Child app: ${child.label}")
    }
}

def logDebug(msg)
{
    if(enableLogging)
    {
        log.debug "${msg}"
    }
}
