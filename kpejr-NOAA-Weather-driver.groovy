/** 
* NOAA Weather Display Driver
*
*  Author: 
*    Kevin Earley 
*
*  Documentation:  Displays temperature from NOAA site
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

metadata{
    definition(
        name: "NOAA Weather",
        namespace: "kpejr",
        author: "Kevin Earley",
        importURL: "") {
        
        capability "Actuator"
        capability "Initialize"
        capability "Polling"
        capability "TemperatureMeasurement"
        
        attribute "city", "string"
        attribute "temperature", "Number"
        attribute "weather", "string"
        attribute "tileHtml", "string"
    
        for(count = 0; count < 7; count++){
            attribute "day${count}Html", "string"
        }
    }
}

preferences {
    section("URIs") {
        input "weatherUrl", "text", title: "Weather URI", required: false
        input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: true
    }
}

def installed() {
	runEvery1Hour(poll)
	poll()
}

def updated() {
	poll()
}

def initialize() {
	poll()
}

def uninstalled() {
	unschedule()
}

def poll(){
    log.debug "Device refresh"
    
      def params = [
        uri: settings.weatherUrl,	
        contentType: "text/xml"
    ]
     
    try {
        httpGet(params) { response ->
            
            city = "${response.data.data.location.description}"
            sendEvent(name: "city", value: city, displayed:true)
            
            temperature = response.data.data[1].parameters.temperature[0].value.toInteger()
            sendEvent(name: "temperature", value: temperature, displayed:true)
            sendEvent(name: "TemperatureMeasurement", value: temperature, displayed:true)
            
            weather = response.data.data[0].parameters.weather.'weather-conditions'[0].@'weather-summary'
            sendEvent(name: "weather", value: weather, displayed:true)
            
            makeTile()
            for(count = 0; count < 7; count++){
                makeDayTile(response.data, count)
            }
            
            if (logEnable) {
               log.debug city
                log.debug "${response.data.data[1].parameters.temperature[0].value}"
             
            }
        }
    } catch (Exception e) {
        log.warn "Call to ${settings.weatherUrl} failed: ${e}"
    }
}

def makeTile(){
    
    html =  """<table width=100% align=center>
                 </tr>
                      <tr><td></td></tr>
                    <tr>
                    <tr>
                        <td align=center style='font-size:xx-large;'>${temperature}&#176; F</td>
                    </tr>
                   <tr>
                        <td align=center style='font-size:small;'>${weather}</td>
                    </tr>

                    <tr><td></td></tr>
                     <tr>
                        <td align=center style='font-size:small;'>${getCurrentDate()} ${getCurrentTime()}</td>
                    </tr>
              
                      <tr><td></td></tr>
                    <tr>
                        <td align=center>${city}</td>
                    </tr>
                  
                    </table>"""
    
    tileHtml = html
    
     sendEvent(name: "tileHtml", value: tileHtml, displayed:true)
}

def makeDayTile(data, tileNumber){
    
    day = data.data[0].'time-layout'[4].'start-valid-time'[tileNumber].@'period-name'
    low = data.data[0].parameters.temperature[0].value[tileNumber]
    high = data.data[0].parameters.temperature[1].value[tileNumber] 
    weather = data.data[0].parameters.weather.'weather-conditions'[(tileNumber * 2) + 1].'@weather-summary'
    
    html =  """<table width=100% align=center>
   
                    <tr><td></td></tr>

                    <tr>
                        <td align=center style='font-size:small;'>${day}</td>
                    </tr>

                   <tr><td></td></tr>

                    <tr>
                       <td align=center style='font-size:small;'>${weather}</td>
                      
                    </tr>
                  <tr><td></td></tr>

                    <tr>
                        <td align=center style='font-size:small;'>Low: ${low}&#176; F</td>
                    </tr>

                    <tr><td></td></tr>

                    <tr>
                        <td align=center style='font-size:small;'>High: ${high}&#176; F</td>
                    </tr>

                    </table>"""
    
    day0html = html

    sendEvent(name: "day${tileNumber}Html", value: day0html, displayed:true)
}

def getCurrentDate(){
	def dateTime = new Date()
    def currentDateTime = dateTime.format("M/d/yyyy", location.timeZone)
	return currentDateTime
}

def getCurrentTime(){
	def dateTime = new Date()
    def currentDateTime = dateTime.format("h:mm:ss a", location.timeZone)
	return currentDateTime
}