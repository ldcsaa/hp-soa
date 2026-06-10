package io.github.hpsocket.soa.framework.logging.gelf.log4j2;

import io.github.hpsocket.soa.framework.logging.gelf.LogMessageField;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * @author Mark Paluch
 */
public class PatternLogMessageField extends LogMessageField {

    private PatternLayout patternLayout;

    public PatternLogMessageField(String name, NamedLogField namedLogField, PatternLayout patternLayout) {
        super(name, namedLogField);
        this.patternLayout = patternLayout;
    }

    public PatternLayout getPatternLayout() {
        return patternLayout;
    }
}
