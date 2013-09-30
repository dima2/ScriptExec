#ScriptExec

##About

ScriptExec is demo application which allows to store and execute Java code, Python and Shell scripts. 

#UI

Web service simple test can be done via UI. Run application & navigate to <http://localhost:8080>

* [Load]: loads script with id set in `id` field
* [Evaluate]: submits script for execution and checks for result after 1 sec. If script runs longer than 1 sec, result can be checked by pressing [Load] button.
* Other fields are pretty much self-explanatory 

##REST API

Web service supports two methods: 

* POST /eval
  Request {JSON}
  * `lang`: `JAVA`,`SHELL`,`PYTHON` - case sensitive
  * `script`: Script for execution

  Response (JSON):
  * `id`: numeric id of the script
  * `status`: status of the script

* GET /eval/`id`
  Parameter `id` is identifier of the script

    Response (JSON):
  * `id`: numeric id of the script
  * `status`: status of the script
  * `lang`: language of the script
  * `script`: script body
  * `result`: output of the script if it was executed

* Script can have following statuses:
  * `PENDING` - script is waiting for execution
  * `EXCECUTED` - script was executed 
  * `SCRIPT_ERROR` - script produced error during execution
  * `NOT_FOUND` - script with following id is not found
  * `TIMEOUT` - script excecution took too long (>10 sec by default)

##Customization

Settings are stored in `Settings` class.

Additional language support can be added by implementing `net.dmi3.scriptexec.infrastructure.ExecStrategy` interface and registering it in `net.dmi3.scriptexec.infrastructure.Lang` class.

##Limitations

Because ScriptExec was created for demo purposes following functionality wasn't implemented:

* Script maximum count in memory limitation. Can be simply implemented by using ehcache
* User roles limitation (i.e. restriction of deletion of files etc)

##Tests

To run unit tests:

    mvn test

To run integration tests (note that it OS specific, and requires Linux to run):

    mvn verify

##Compilation

To compile application and run application:

    mvn clean compile exec:java

To create jar:

    mvn clean compile assembly:single

To run jar use (8080 is optional parameter to define port of embeded server):     

    java -jar ScriptExec.jar 8080 

