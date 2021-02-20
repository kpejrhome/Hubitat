/** 
* Group Switch Parent
*
*  Author: 
*    Kevin Earley 
*
*  Documentation:  Use a virtual or real switch to group other switches
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
    name: "Group Switch",
    namespace: "kpejr",
    author: "Kevin Earley",
    description: "Use a virtual or real switch to group other switches",
    category: "Convenience",
    importUrl: "https://raw.githubusercontent.com/kpejrhome/Hubitat/master/kpejr-group-switch-parent.groovy",
	    
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
            app(name: "newGroup", appName: "Group Switch - Child", namespace: "kpejr", title: "<b>Add a Group Switch app</b>", multiple: true)
        }		
	}
}

 