package io.github.hpsocket.soa.framework.logging.gelf.jboss7;

import java.util.Set;

import org.jboss.logmanager.ExtLogRecord;

import io.github.hpsocket.soa.framework.logging.gelf.DynamicMdcMessageField;
import io.github.hpsocket.soa.framework.logging.gelf.GelfUtil;
import io.github.hpsocket.soa.framework.logging.gelf.LogMessageField;
import io.github.hpsocket.soa.framework.logging.gelf.MdcMessageField;
import io.github.hpsocket.soa.framework.logging.gelf.MessageField;
import io.github.hpsocket.soa.framework.logging.gelf.Values;
import io.github.hpsocket.soa.framework.logging.gelf.jul.JulLogEvent;

/**
 * @author Mark Paluch
 * @since 26.09.13 18:32
 */
public class JBoss7JulLogEvent extends JulLogEvent {

    private ExtLogRecord extLogRecord;

    public JBoss7JulLogEvent(ExtLogRecord logRecord) {
        super(logRecord);
        this.extLogRecord = logRecord;
    }

    @Override
    public Values getValues(MessageField field) {
        if (field instanceof MdcMessageField) {
            return new Values(field.getName(), getMdcValue(((MdcMessageField) field).getMdcName()));
        }

        if (field instanceof DynamicMdcMessageField) {
            return getMdcValues((DynamicMdcMessageField) field);
        }

        return super.getValues(field);
    }

    @Override
    public String getValue(LogMessageField field) {
        switch (field.getNamedLogField()) {
            case NDC:
                String ndc = this.extLogRecord.getNdc();
                if (ndc != null && !"".equals(ndc)) {
                    return ndc;
                }
                return null;
            case SourceLineNumber:
                return getSourceLineNumber();
            case ThreadName:
                return this.extLogRecord.getThreadName();
            default:
                ;
        }
        return super.getValue(field);
    }

    private String getSourceLineNumber() {
        if (this.extLogRecord.getSourceLineNumber() <= 0) {
            return null;
        }

        return "" + this.extLogRecord.getSourceLineNumber();
    }

    private Values getMdcValues(DynamicMdcMessageField field) {
        Values result = new Values();

        Set<String> mdcNames = getAllMdcNames();

        Set<String> matchingMdcNames = GelfUtil.getMatchingMdcNames(field, mdcNames);

        for (String mdcName : matchingMdcNames) {
            String mdcValue = getMdcValue(mdcName);
            if (mdcValue != null) {
                result.setValue(mdcName, mdcValue);
            }
        }

        return result;
    }

    private Set<String> getAllMdcNames() {
        return extLogRecord.getMdcCopy().keySet();
    }

    @Override
    public String getMdcValue(String mdcName) {
        return extLogRecord.getMdc(mdcName);
    }

    @Override
    public Set<String> getMdcNames() {
        return getAllMdcNames();
    }
}
