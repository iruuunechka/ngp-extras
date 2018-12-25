package ru.ifmo.ctd.ngp.demo.ffchooser.vfs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This exception is thrown if a cyclic dependency on resources is detected.
 *
 * @author Maxim Buzdalov
 */
public class ResourceCyclicDependencyException extends ResourceConstructionException {
    private static final long serialVersionUID = -544464206994702809L;

    private final List<String> deps;

    public ResourceCyclicDependencyException(List<String> deps) {
        super(constructMessage(deps = Collections.unmodifiableList(new ArrayList<>(deps))));
        this.deps = deps;
    }

    public List<String> getCyclicDependencyDescription() {
        return deps;
    }

    private static String constructMessage(List<String> deps) {
        StringBuilder sb = new StringBuilder();
        sb.append("Resource ").append(deps.get(0)).append(" has cyclic dependency: ");
        for (int i = 0; i < deps.size(); ++i) {
            sb.append(deps.get(i));
            if (i + 1 < deps.size()) {
                sb.append(" -> ");
            }
        }
        return sb.toString();
    }
}
