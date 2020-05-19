package com.eden.orchid.api.resources;

import com.eden.orchid.api.OrchidService;
import com.eden.orchid.api.resources.resource.OrchidResource;
import com.eden.orchid.api.resources.resourcesource.DataResourceSource;
import com.eden.orchid.api.resources.resourcesource.OrchidResourceSource;
import com.eden.orchid.api.resources.resourcesource.TemplateResourceSource;
import com.eden.orchid.api.theme.AbstractTheme;
import com.google.inject.ImplementedBy;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;
import java.util.List;

@ImplementedBy(ResourceServiceImpl.class)
public interface ResourceService extends OrchidService {

    default String[] getIgnoredFilenames() {
        return getService(ResourceService.class).getIgnoredFilenames();
    }

    default OrchidResourceSource getDefaultResourceSource(@Nullable OrchidResourceSource.Scope scopes, @Nullable AbstractTheme theme) {
        return getService(ResourceService.class).getDefaultResourceSource(scopes, theme);
    }

    default TemplateResourceSource getTemplateResourceSource(@Nullable OrchidResourceSource.Scope scopes, @NonNull AbstractTheme theme) {
        return getService(ResourceService.class).getTemplateResourceSource(scopes, theme);
    }

    default DataResourceSource getDataResourceSource(@Nullable OrchidResourceSource.Scope scopes) {
        return getService(ResourceService.class).getDataResourceSource(scopes);
    }

    default @Nullable OrchidResource findClosestFile(String filename) {
        return getService(ResourceService.class).findClosestFile(filename);
    }

    default @Nullable OrchidResource findClosestFile(String filename, boolean strict) {
        return getService(ResourceService.class).findClosestFile(filename, strict);
    }

    default @Nullable OrchidResource findClosestFile(String filename, boolean strict, int maxIterations) {
        return getService(ResourceService.class).findClosestFile(filename, strict, maxIterations);
    }

    default @Nullable OrchidResource findClosestFile(String baseDir, String filename, boolean strict, int maxIterations) {
        return getService(ResourceService.class).findClosestFile(baseDir, filename, strict, maxIterations);
    }

    default OrchidResource locateLocalResourceEntry(final String fileName) {
        return getService(ResourceService.class).locateLocalResourceEntry(fileName);
    }

    default OrchidResource locateLocalResourceEntry(final String fileName, String[] fileExtensions) {
        return getService(ResourceService.class).locateLocalResourceEntry(fileName, fileExtensions);
    }

    default OrchidResource locateLocalResourceEntry(final String fileName, List<String> fileExtensions) {
        return getService(ResourceService.class).locateLocalResourceEntry(fileName, fileExtensions);
    }

}
