package org.tuberlin.de.server.controller;

import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tuberlin.de.deployment.DeploymentImplementation;
import org.tuberlin.de.deployment.DeploymentInterface;
import org.tuberlin.de.deployment.util.DownloadUtils;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/graph/jar/*"})
public class GraphJarController extends HttpServlet {

    private static final long serialVersionUID = 23523652345L;
    private static final Logger LOG = LoggerFactory.getLogger(GraphZipController.class);

    private DeploymentInterface deploymentInterface;

    public GraphJarController() {
        LOG.debug("Init");
        deploymentInterface = DeploymentImplementation.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.debug("Request: " + req.getPathInfo().substring(1));
        String uuid = req.getPathInfo().substring(1);

        LOG.debug("UUID" + uuid + " addr" + req.getRemoteAddr());

        Session clientSession = JettyWebSocket.getSession(req.getRemoteAddr());

        InputStream inputStream = deploymentInterface.getJarStream(clientSession, uuid);

        if (inputStream == null) {
            return;
        }

        resp.setContentType("application/java-archive");
        resp.setHeader("Content-disposition", "attachment; filename=FlinkJob.jar");

        DownloadUtils.startDownload(resp, inputStream);
    }
}