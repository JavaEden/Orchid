package com.eden.orchid.impl.compilers.pebble;

import com.eden.orchid.api.OrchidContext;
import com.eden.orchid.api.compilers.TemplateFunction;
import com.eden.orchid.api.theme.pages.OrchidPage;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.escaper.SafeString;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import javax.inject.Provider;
import java.util.List;
import java.util.Map;

public final class PebbleWrapperTemplateFilter implements Filter {
    private final Provider<OrchidContext> contextProvider;
    private final String name;
    private final String inputParam;
    private final List<String> params;
    private final Class<? extends TemplateFunction> functionClass;

    public PebbleWrapperTemplateFilter(Provider<OrchidContext> contextProvider, String name, List<String> params, Class<? extends TemplateFunction> functionClass) {
        this.contextProvider = contextProvider;
        this.name = name;
        if (params.size() > 0) {
            this.inputParam = params.get(0);
            this.params = params.subList(1, params.size());
        } else {
            this.inputParam = "";
            this.params = params;
        }
        this.functionClass = functionClass;
    }

    @Override
    public List<String> getArgumentNames() {
        return params;
    }

    @Override
    public Object apply(Object input, Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) throws PebbleException {
        args.put(inputParam, input);
        TemplateFunction freshFunction = contextProvider.get().resolve(functionClass);

        Object pageVar = context.getVariable("page");
        final OrchidPage actualPage;
        if (pageVar instanceof OrchidPage) {
            actualPage = (OrchidPage) pageVar;
        }
        else {
            actualPage = null;
        }

        freshFunction.extractOptions(contextProvider.get(), args);
        Object output = freshFunction.apply(contextProvider.get(), actualPage);

        if (freshFunction.isSafeString()) {
            return new SafeString(output.toString());
        } else {
            return output;
        }
    }

    public Provider<OrchidContext> getContextProvider() {
        return this.contextProvider;
    }

    public String getName() {
        return this.name;
    }

    public String getInputParam() {
        return this.inputParam;
    }

    public List<String> getParams() {
        return this.params;
    }

    public Class<? extends TemplateFunction> getFunctionClass() {
        return this.functionClass;
    }
}
