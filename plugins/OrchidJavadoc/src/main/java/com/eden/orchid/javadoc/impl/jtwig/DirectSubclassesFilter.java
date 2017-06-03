package com.eden.orchid.javadoc.impl.jtwig;

import com.eden.orchid.api.OrchidContext;
import com.eden.orchid.javadoc.OrchidJavadoc;
import com.eden.orchid.javadoc.impl.docParsers.ClassDocParser;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.JtwigFunction;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Singleton
public class DirectSubclassesFilter implements JtwigFunction {

    private OrchidContext context;
    private ClassDocParser parser;
    private RootDoc rootDoc;

    @Inject
    public DirectSubclassesFilter(OrchidContext context, ClassDocParser parser) {
        this.context = context;
        this.parser = parser;
    }

    @Override
    public String name() {
        return "directSubclasses";
    }

    @Override
    public Collection<String> aliases() {
        return Collections.emptyList();
    }

    @Override
    public Object execute(FunctionRequest request) {
        List<Object> fnParams = request.minimumNumberOfArguments(1)
                                       .maximumNumberOfArguments(1)
                                       .getArguments();

        if(rootDoc == null) {
            rootDoc = OrchidJavadoc.rootDoc;
        }

        if(fnParams.size() == 1 && fnParams.get(0) != null) {
            String classDocName = fnParams.get(0).toString();
            ClassDoc classDoc = rootDoc.classNamed(classDocName);

            if(classDoc != null) {
                return parser.getDirectSubclasses(classDoc).toList();
            }
        }

        return null;
    }
}