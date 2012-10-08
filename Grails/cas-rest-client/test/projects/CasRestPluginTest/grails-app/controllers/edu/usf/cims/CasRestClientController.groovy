package edu.usf.cims

class CasRestClientController {

    def index() { 

        def result = withCasRest("https://dev.it.usf.edu/nams/ws_convert","GET","casUser","casPasswd",[:],[submit_type:"netid",return_type:"mail","return":"json",value:"james"])

        render(result)
    }
}
