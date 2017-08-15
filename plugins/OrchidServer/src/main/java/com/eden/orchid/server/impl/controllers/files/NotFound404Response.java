package com.eden.orchid.server.impl.controllers.files;

import com.caseyjbrooks.clog.Clog;
import com.eden.orchid.api.OrchidContext;
import com.eden.orchid.api.resources.resource.OrchidResource;
import fi.iki.elonen.NanoHTTPD;
import org.json.JSONObject;

import javax.inject.Inject;

public class NotFound404Response {

    private OrchidContext context;

    @Inject
    public NotFound404Response(OrchidContext context) {
        this.context = context;
    }

    public NanoHTTPD.Response getResponse(String targetPath) {
        JSONObject page = new JSONObject();
        page.put("title", "404 - " + targetPath);
        page.put("path", targetPath);

        JSONObject object = new JSONObject(context.getRoot().toMap());
        object.put("page", page);

        OrchidResource resource = context.getResourceEntry("templates/server/404.twig");

        String content = "";
        if(resource != null) {
            content = context.compile(resource.getReference().getExtension(), resource.getContent(), object.toString(2));
        }

        Clog.i("Rendering 404: #{$1}", targetPath);
        return NanoHTTPD.newFixedLengthResponse(content);
    }
}
