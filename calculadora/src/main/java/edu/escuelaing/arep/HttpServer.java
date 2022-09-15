package edu.escuelaing.arep;

import com.github.cliftonlabs.json_simple.JsonObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class HttpServer {
    private static final HttpServer _instance = new HttpServer();
    public static HttpServer getInstance(){return _instance;}

    public funciones funciones = new funciones();
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
        double val=0;
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
            String[] a = (request.get(0).split("/")[1]).split("");
            UrlStr = a[0]+a[1]+a[2];
            if(UrlStr.equals("cos") || UrlStr.equals("sin") || UrlStr.equals("tan")){
                val = Double.parseDouble(((request.get(0).split("/")[1]).split("=")[1]).split(" ")[0]);
            }
            System.out.println(UrlStr);
        }
        JsonObject jsonObject = new JsonObject();
        if (UrlStr.equals("/cos")){
            jsonObject.put("res",this.funciones.cos(Double.valueOf(val)));
        }
        else if (UrlStr.equals("/sin")){
            jsonObject.put("res",this.funciones.sin(Double.valueOf(val)));
        }
        else if (UrlStr.equals("/tan")){
            jsonObject.put("res",this.funciones.tan(Double.valueOf(val)));
        }
        else if (UrlStr.equals("/qck")){
        }


    }


    public int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }
}
