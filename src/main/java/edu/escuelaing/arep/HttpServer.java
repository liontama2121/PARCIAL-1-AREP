package edu.escuelaing.arep;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class HttpServer {
    private static final HttpServer _instance = new HttpServer();
    public static HttpServer getInstance(){return _instance;}
    public void start(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(getPort());
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        Socket clientSocket = null;
        boolean running=true;
        while (running) {
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            serveConneciton(clientSocket);
        }
        serverSocket.close();
    }
    public void serveConneciton(Socket clientSocket) throws IOException {
        PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String inputLine, outputLine;
        ArrayList<String> request = new ArrayList<String>();
        while ((inputLine = bufferedReader.readLine()) != null) {
            System.out.println("Received: " + inputLine);
            request.add(inputLine);
            if (!bufferedReader.ready()) {
                break;
            }
        }
        String UrlStr="";
        if(request.size()>0) {
            UrlStr = request.get(0).split(" ")[1];
        }
        if (UrlStr.equals("/calcular")){
            outputLine=Calcular();
            printWriter.println(outputLine);
            printWriter.close();
        }
        else{
            outputLine=Error(UrlStr);
            System.out.println(outputLine);
            printWriter.println(outputLine);
            printWriter.close();
        }





    }

    public String Error(String UrlStr){
        String outputline="HTTP/1.1 200 OK\r\n"
                + "Content - Type: text/html\r\n"
                + "\r\n" +
                "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" +
                "</head>\n" +
                "<center>\n" +
                "    <body>\n" +
                "        <br> <b><big><FONT COLOR=\"black\" size=\"500\"> The requested URL "+UrlStr+" not found  on this server</FONT></big></b>\n" +
                "    </body>\n" +
                "</center>\n";
        return outputline;
    }

    public String Calcular(){
        String outputline="\"HTTP/1.1 200 OK\\r\\n\"\n" +
                " + \"Content-Type: text/html\\r\\n\"\n" +
                "+ \"\\r\\n\"" +
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>Form Example</title>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>Form with GET</h1>\n" +
                "        <form action=\"/hello\">\n" +
                "            <label for=\"name\">Name:</label><br>\n" +
                "            <input type=\"text\" id=\"name\" name=\"name\" value=\"John\"><br><br>\n" +
                "            <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\n" +
                "        </form> \n" +
                "        <div id=\"getrespmsg\"></div>\n" +
                "\n" +
                "        <script>\n" +
                "            function loadGetMsg() {\n" +
                "                let nameVar = document.getElementById(\"name\").value;\n" +
                "                const xhttp = new XMLHttpRequest();\n" +
                "                xhttp.onload = function() {\n" +
                "                    document.getElementById(\"getrespmsg\").innerHTML =\n" +
                "                    this.responseText;\n" +
                "                }\n" +
                "                xhttp.open(\"GET\", \"/hello?name=\"+nameVar);\n" +
                "                xhttp.send();\n" +
                "            }\n" +
                "        </script>\n" +
                "\n" +
                "        <h1>Form with POST</h1>\n" +
                "        <form action=\"/hellopost\">\n" +
                "            <label for=\"postname\">Name:</label><br>\n" +
                "            <input type=\"text\" id=\"postname\" name=\"name\" value=\"John\"><br><br>\n" +
                "            <input type=\"button\" value=\"Submit\" onclick=\"loadPostMsg(postname)\">\n" +
                "        </form>\n" +
                "        \n" +
                "        <div id=\"postrespmsg\"></div>\n" +
                "        \n" +
                "        <script>\n" +
                "            function loadPostMsg(name){\n" +
                "                let url = \"/hellopost?name=\" + name.value;\n" +
                "\n" +
                "                fetch (url, {method: 'POST'})\n" +
                "                    .then(x => x.text())\n" +
                "                    .then(y => document.getElementById(\"postrespmsg\").innerHTML = y);\n" +
                "            }\n" +
                "        </script>\n" +
                "    </body>\n" +
                "</html>";
        return outputline;
    }

    public String Json(String site) throws IOException{
        String inputLine = null;
        StringBuffer JSON = new StringBuffer();
        URL siteURL = new URL(site);
        URLConnection urlConnection = siteURL.openConnection();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
            while ((inputLine = reader.readLine()) != null) {
                JSON.append(inputLine);
            }
            reader.close();
        } catch (IOException x) {
            System.err.println(x);
        }
        return JSON.toString();
    }

    public int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }
}
