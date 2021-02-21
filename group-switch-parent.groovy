/** 
* Group Switch Parent
*
*  Author: 
*    Kevin Earley 
*
*  Documentation:  Use a virtual or real switch to group other switches
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
    name: "Group Switch",
    namespace: "kpejr",
    author: "Kevin Earley",
    description: "Use a virtual or real switch to group other switches",
    category: "Convenience",
    importUrl: "https://raw.githubusercontent.com/kpejrhome/Hubitat/master/kpejr-group-switch.groovy",
	    
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
            app(name: "newGroup", appName: "Group Switch - Child", namespace: "kpejr", title: "<b>Add a Group Switch app</b>", multiple: true)
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
    logDebug("Updated with settings: ${settings}")
    
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
