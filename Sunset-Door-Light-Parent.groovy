/** 
* Sunset Door Light Parent
*
*  Author: 
*    Kevin Earley 
*
*  Documentation:  Turn on a light when opening a door after sunset
*
*  Changelog: v1.0
*  V1.0.1 - Added debug logging option
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
    name: "Sunset Door Light",
    namespace: "kpejr",
    author: "Kevin Earley",
    description: "Turn on a light when opening a door after sunset",
    category: "Convenience",
    importUrl: "https://raw.githubusercontent.com/kpejrhome/Hubitat/master/Sunset-Door-Light-Parent.groovy",	
	    
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
            paragraph "This app will turn a light on when a door is opened after sunset."
            app(name: "newSunset", appName: "Sunset Door Light - Child", namespace: "kpejr", title: "<b>Add a Sunset Door Light app</b>", multiple: true)
            input name:	"enableLogging", type: "bool", title: "Enable Debug Logging?", defaultValue: true, required: true
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

