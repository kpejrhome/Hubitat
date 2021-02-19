/** 
* Sunset Door Light Parent
*
*  Author: 
*    Kevin Earley 
*
*  Documentation:  Turn on a light when opening a door after sunset
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
    name: "Sunset Door Light",
    namespace: "kpejr",
    author: "Kevin Earley",
    description: "Turn on a light when opening a door after sunset",
    category: "Convenience",
	    
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "",
)


preferences {
     page name: "mainPage", title: "", install: true, uninstall: true
} 

def installed() {
    log.info "Installed with settings: ${settings}"
    initialize()
}


def updated() {
    log.info "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    childApps.each { child ->
        log.info "Child app: ${child.label}"
    }
}

def mainPage() {
    dynamicPage(name: "mainPage") {
        
        section() {
            paragraph "This app will turn a light on when a door is opened after sunset."
            app(name: "newSunset", appName: "Sunset Door Light - Child", namespace: "kpejr", title: "<b>Add a Sunset Door Light app</b>", multiple: true)
        }		
	}
}
