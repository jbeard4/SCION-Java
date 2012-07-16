var Executors = Packages.java.util.concurrent.Executors,
    InetSocketAddress = Packages.java.net.InetSocketAddress,
    Headers = Packages.com.sun.net.httpserver.Headers,
    HttpExchange = Packages.com.sun.net.httpserver.HttpExchange,
    HttpHandler = Packages.com.sun.net.httpserver.HttpHandler,
    HttpServer = Packages.com.sun.net.httpserver.HttpServer;

var SCXML = Packages.com.inficon.scion.SCXML;

var port = 42000;
var addr = new InetSocketAddress(port);
var server = HttpServer.create(addr, 0);

var sessionCounter = 0, sessions = {}, timeouts = {}, timeoutMs = 5000;

function loadScxml(scxmlStr,cb){
}

//best way I could find to turn a hashset into a native js array
function hashSetToJsArray(hs){
    var r = [];
    var itr = hs.iterator();
    while(itr.hasNext()){
        r.push(String(itr.next()));
    }
    return r;
}

function cleanUp(sessionToken){
    delete sessions[sessionToken];
}

function toBytes(s){
    if(typeof s === "object"){
        s = JSON.stringify(s);
    }
    return (new Packages.java.lang.String(s)).getBytes();
}

var handler = new HttpHandler({handle : function(exchange){
    var reqBody = exchange.getRequestBody();
    var bodyArr = [], c;
    while((c = reqBody.read()) !== -1){
        bodyArr.push(String.fromCharCode(c));
    } 
    var s = bodyArr.join("");
    //print("body",s);

    var responseHeaders = exchange.getResponseHeaders();
    var responseBody = exchange.getResponseBody();

    try{
        var reqJson = JSON.parse(s);
        if(reqJson.load){
            print("Loading new statechart");

            var interpreter = new SCXML(new Packages.java.net.URL(reqJson.load));

            var conf = interpreter.start();

            var sessionToken = sessionCounter;
            sessionCounter++;
            sessions[sessionToken] = interpreter; 

            responseHeaders.set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, 0);
            responseBody.write(toBytes({
                sessionToken : sessionToken,
                nextConfiguration : hashSetToJsArray(conf)
            }));

            responseBody.close();

            //timeouts[sessionToken] = setTimeout(function(){cleanUp(sessionToken);},timeoutMs);  
        }else if(reqJson.event && (typeof reqJson.sessionToken === "number")){
            print("sending event to statechart",JSON.stringify(reqJson.event,4,4));
            sessionToken = reqJson.sessionToken;
            var nextConfiguration = sessions[sessionToken].gen(reqJson.event.name,null);
            responseHeaders.set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, 0);
            responseBody.write(toBytes({
                nextConfiguration : hashSetToJsArray(nextConfiguration)
            }));

            //clearTimeout(timeouts[sessionToken]);
            //timeouts[sessionToken] = setTimeout(function(){cleanUp(sessionToken);},timeoutMs);  
        }else{
            //unrecognized. send back an error
            responseHeaders.set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(400, 0);
            responseBody.write(toBytes("Unrecognized request.\n"));
        }
    }catch(e){
        print(e);
        print(e.stack);

        responseHeaders.set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(500, 0);
        responseBody.write(toBytes(e.message));
    }


    responseBody.close();
}});
server.createContext("/", handler);
server.setExecutor(Executors.newCachedThreadPool());
server.start();
print("Server is listening on port " + port );

