Design a simple web service with the purpose of script storage and execution.

It has the following structure:

    POST /eval

        Request body should contain the script.  
        Optionally, request body should indicate the scripting language to use.  
        Stores the script, assigns a unique identifier, and returns it as a response.

    GET /eval/{id}

        Runs the script with the provided identifier, returns its result and output.

Full-featured examples of such services that present the general idea are [Ideone.com](http://ideone.com/) and [codepad](http://codepad.org/).

### Rules

- The resulting application must run on the JVM
- You should supply automated unit and functional tests
- The service and tests must be buildable and executable via command line
- You can use any language, framework, and libraries
- You can use any transport, e.g. JSON or XML
- Scripts may be stored in memory or persisted to disk
- Provide a README with instructions on how to communicate with the service or make a UI
