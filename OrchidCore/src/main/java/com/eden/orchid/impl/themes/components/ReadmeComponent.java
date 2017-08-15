package com.eden.orchid.impl.themes.components;

import com.eden.orchid.api.OrchidContext;
import com.eden.orchid.api.resources.resource.OrchidResource;
import com.eden.orchid.api.theme.components.OrchidComponent;

import javax.inject.Inject;

public class ReadmeComponent extends OrchidComponent {

    @Inject
    public ReadmeComponent(OrchidContext context) {
        super(50, "readme", context);
    }

    public String getContent() {
        OrchidResource readmeResource = context.findClosestFile("readme");
        if (readmeResource != null) {
            return context.compile(readmeResource.getReference().getExtension(), readmeResource.getContent(), this);
        }
        return null;
    }
}
