package com.skillbox.cryptochat;

import lombok.extern.java.Log;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;

@Log
public class StartupServlet extends HttpServlet {


    static {
        try {
            log.info("STARTING WS");
            WSServer.startServer(8881);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "WS ERROR", e);
        } catch (IOException e) {
            log.log(Level.SEVERE, "WS ERROR", e);
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(204);
    }
}
