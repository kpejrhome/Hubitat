/** 
* NOAA Weather Display Driver
*
*  Author: 
*    Kevin Earley 
*
*  Documentation:  Displays temperature from NOAA site
*
*  Changelog: v1.0
*  1.0.1 Fixed XML nodes
 
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
        importURL: "https://raw.githubusercontent.com/kpejrhome/Hubitat/master/kpejr-NOAA-Weather-driver.groovy") {
        
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
        input name:	"enableLogging", type: "bool", title: "Enable Debug Logging?", defaultValue: false, required: true
    }
}

def installed() {
    logDebug("Installed application")
	runEvery1Hour(poll)
	poll()
}

def updated() {
    logDebug("Updated application")
	poll()
}

def initialize() {
    logDebug("Initializing with settings: ${settings}")
	poll()
}

def uninstalled() {
    logDebug("Uninstalled application")
	unschedule()
}

def logDebug(msg)
{
    if(enableLogging)
    {
        log.debug "${msg}"
    }
}

def poll(){
    logDebug("Device poll")
    
      def params = [
        uri: settings.weatherUrl,	
        contentType: "text/xml"
    ]
     
    try {
        httpGet(params) { response ->
            
            city = "${response.data.data.location.description}"
            sendEvent(name: "city", value: city, displayed:true)
            logDebug("City: ${city}")
            
            temperature = response.data.data[1].parameters.temperature[0].value.toInteger()
            sendEvent(name: "temperature", value: temperature, displayed:true)
            sendEvent(name: "TemperatureMeasurement", value: temperature, displayed:true)
            logDebug("Temperature ${temperature}")
            
            weather = response.data.data[0].parameters.weather.'weather-conditions'[0].@'weather-summary'
            sendEvent(name: "weather", value: weather, displayed:true)
            logDebug("wWather: ${weather}")
            
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
    logDebug("Tile Html: ${tileHtml}")
    
    sendEvent(name: "tileHtml", value: tileHtml, displayed:true)
}

def makeDayTile(data, tileNumber){
    
    dayNodes = data.data[0].'time-layout'.findAll { it.'layout-key' == 'k-p24h-n7-2' }
    
    if(dayNodes == null || dayNodes.count(0) == 0){
       dayNodes = data.data[0].'time-layout'.findAll { it.'layout-key' == 'k-p24h-n7-1' } 
    }
 
    day =dayNodes[0].'start-valid-time'[tileNumber].@'period-name'
    logDebug("Day: ${day}")
    
    lowNodes = data.data[0].parameters.temperature.findAll { it.name == 'Daily Minimum Temperature'} 
    low = lowNodes[0].value[tileNumber]
    logDebug("Low: ${low}")
    
    highNodes = data.data[0].parameters.temperature.findAll { it.name == 'Daily Maximum Temperature'} 
    high = highNodes[0].value[tileNumber]
    logDebug("High: ${high}")
    
    weather = data.data[0].parameters.weather.'weather-conditions'[tileNumber * 2].'@weather-summary'
    logDebug("Weather: ${weather}")
    
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
                        <td align=center style='font-size:small;'>High: ${high}&#176; F</td>
                    </tr>

                  <tr><td></td></tr>

                    <tr>
                        <td align=center style='font-size:small;'>Low: ${low}&#176; F</td>
                    </tr>

                    </table>"""
    
    day0html = html

    logDebug("Day Html: ${day0html}")
    
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
